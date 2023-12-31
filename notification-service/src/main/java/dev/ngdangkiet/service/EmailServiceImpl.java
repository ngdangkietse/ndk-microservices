package dev.ngdangkiet.service;

import dev.ngdangkiet.domain.notification.email.JsonMessageEmail;
import dev.ngdangkiet.dto.EmailDTO;
import dev.ngdangkiet.mapper.EmailMapper;
import jakarta.mail.Message;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.util.MapUtils;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender emailSender;

    private final TemplateEngine templateEngine;

    private final EmailMapper emailMapper = EmailMapper.INSTANCE;

    @Value("${spring.mail.username}")
    private String sender;

    public static String replaceProperties(String html, Map<String, Object> properties) {
        String result = html;
        for (Map.Entry<String, Object> property : properties.entrySet()) {
            String placeholder = String.format("${%s}", property.getKey());
            Object replacement = property.getValue();
            result = result.replace(placeholder, String.valueOf(replacement));
        }
        return result;
    }

    @Override
    public void sendMail(EmailDTO email) {
        try {
            MimeMessage mimeMessage = emailSender.createMimeMessage();
            mimeMessage.setFrom(sender);
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(email.getReceiverEmail()));
            mimeMessage.setSubject(email.getSubject());
            if (email.getIsHTML()) {
                String bodyHTML;
                if (!MapUtils.isEmpty(email.getProperties())) {
                    bodyHTML = replaceProperties(email.getMessage(), email.getProperties());
                } else {
                    bodyHTML = email.getMessage();
                }
                mimeMessage.setContent(bodyHTML, "text/html");
            } else {
                mimeMessage.setText(email.getMessage());
            }
            emailSender.send(mimeMessage);
            log.info("Send Email to [%d] Successfully!!!" + email.getReceiverEmail());
        } catch (Exception e) {
            log.error("An error occur when send email : " + e.getMessage());
        }
    }

    @Override
    public void sendSampleEMail(String sendTo, String Subject, String body) {
        try {
            MimeMessage mimeMessage = emailSender.createMimeMessage();
            mimeMessage.setFrom(sender);
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(sendTo));
            mimeMessage.setSubject(Subject);
            mimeMessage.setText(body);
            emailSender.send(mimeMessage);
            log.info("Send Email Successfully!!!");
        } catch (Exception e) {
            log.error("An error occur when send email : " + e.getMessage());
        }
    }

    @Override
    public void sendEmailWithTemplate(EmailDTO details) {
        try {
            Context context = new Context();
            Map<String, Object> properties = details.getProperties();
            if (!properties.isEmpty()) {
                for (Map.Entry<String, Object> entry : properties.entrySet()) {
                    context.setVariable(entry.getKey(), entry.getValue());
                }
            }
            MimeMessage mimeMessage = emailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");


            mimeMessageHelper.setSubject(details.getSubject());
            mimeMessageHelper.setTo(details.getReceiverEmail());
            mimeMessageHelper.setFrom(sender);
            if (Objects.nonNull(details.getAttachment())) {
                // Adding the attachment
                FileSystemResource file = new FileSystemResource(new File(details.getAttachment()));
                mimeMessageHelper.addAttachment(Objects.requireNonNull(file.getFilename()), file);
            }
            String templateName = StringUtils.hasText(details.getEmailTemplate()) ? details.getEmailTemplate() : "index.html";
            String htmlContent = templateEngine.process(templateName, context);
            mimeMessageHelper.setText(htmlContent, true);

            // Send email
            emailSender.send(mimeMessage);
            log.info("Sending email success to: " + details.getReceiverEmail());
        } catch (MessagingException e) {
            log.error("Error when preparing or sending email to: " + details.getReceiverEmail(), e);
        } catch (Exception e) {
            log.error("An unexpected error occurred when sending email to: " + details.getReceiverEmail(), e);
        }
    }

    @Override
    public void sendEmailWithTemplate(List<EmailDTO> emails) {
        if (!CollectionUtils.isEmpty(emails)) {
            emails.forEach(this::sendEmailWithTemplate);
        }
    }

    @Override
    public void receiveEmailNotification(JsonMessageEmail message) {
        EmailDTO email = emailMapper.toDomain(message);
        if (StringUtils.hasText(email.getEmailTemplate())) {
            sendEmailWithTemplate(email);
        } else {
            sendMail(email);
        }
    }
}

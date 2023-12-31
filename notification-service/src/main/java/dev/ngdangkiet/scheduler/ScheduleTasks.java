package dev.ngdangkiet.scheduler;

import dev.ngdangkiet.client.EmployeeGrpcClient;
import dev.ngdangkiet.constant.MessageConstant;
import dev.ngdangkiet.constant.VariableConstant;
import dev.ngdangkiet.dkmicroservices.employee.protobuf.PEmployee;
import dev.ngdangkiet.dkmicroservices.employee.protobuf.PEmployeesResponse;
import dev.ngdangkiet.dkmicroservices.employee.protobuf.PGetEmployeesRequest;
import dev.ngdangkiet.domain.NotificationEntity;
import dev.ngdangkiet.dto.EmailDTO;
import dev.ngdangkiet.enums.notification.EmailTemplate;
import dev.ngdangkiet.enums.notification.Holiday;
import dev.ngdangkiet.enums.notification.NotificationType;
import dev.ngdangkiet.repository.NotificationRepository;
import dev.ngdangkiet.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;
import static org.apache.commons.lang.StringUtils.EMPTY;

/**
 * @author ngdangkiet
 * @since 11/18/2023
 */

@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduleTasks {

    private final EmployeeGrpcClient employeeGrpcClient;
    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    private List<PEmployee> getEmployees() {
        PEmployeesResponse employees = employeeGrpcClient.getEmployees(PGetEmployeesRequest.newBuilder()
                .setDepartmentId(-1L)
                .setPositionId(-1L)
                .build());
        return employees.getDataList();
    }

    private void setAndSaveNotifications(String message, List<PEmployee> receivers) {
        List<NotificationEntity> notifications = new ArrayList<>();
        if (!CollectionUtils.isEmpty(receivers)) {
            receivers.forEach(receiver -> notifications.add(NotificationEntity.builder()
                    .receiverId(receiver.getId())
                    .message(message)
                    .notificationType(NotificationType.ALERT.name())
                    .build()));
        }
        if (!CollectionUtils.isEmpty(notifications)) {
            notificationRepository.saveAll(notifications);
        }
    }

    @Scheduled(cron = "0 0 8 * * MON")
    public void executeBeginOfTheWeekTask() {
        log.info("Begin day of the week [{}]", LocalDate.now().getDayOfWeek().name());
        setAndSaveNotifications(MessageConstant.Notification.BeginEndDayOfWeek.BEGIN_DAY_OF_WEEK, getEmployees());
    }

    @Scheduled(cron = "0 0 17 * * FRI")
    public void executeEndOfTheWeekTask() {
        log.info("End day of the week [{}]", LocalDate.now().getDayOfWeek().name());
        setAndSaveNotifications(MessageConstant.Notification.BeginEndDayOfWeek.END_DAY_OF_WEEK, getEmployees());
    }

    // Every day at 7:00 AM
    @Scheduled(cron = "0 0 7 * * ?")
    public void executeHolidayTask() {
        for (Holiday holiday : Holiday.values()) {
            if (isDateNow(holiday.getDate())) {
                log.info("Holiday [{}]", holiday.name());
                setAndSaveNotifications(holiday.getMessage(), getEmployees());
                break;
            }
        }
    }

    // Every day at 8:00 AM
    @Scheduled(cron = "0 0 8 * * ?")
    public void executeBirthdayTask() {
        List<PEmployee> employees = getEmployees();
        if (!employees.isEmpty()) {
            List<NotificationEntity> notifications = new ArrayList<>();
            List<EmailDTO> emailDTOS = new ArrayList<>();
            employees.forEach(e -> {
                if (!e.getBirthDay().equals(EMPTY) && isDateNow(LocalDate.parse(e.getBirthDay(), DateTimeFormatter.ofPattern("yyyy-MM-dd")))) {
                    log.info("Birthday [{}]", e.getFullName());
                    notifications.add(NotificationEntity.builder()
                            .receiverId(e.getId())
                            .message(MessageConstant.Notification.BirthDay.HAPPY_BIRTHDAY)
                            .notificationType(NotificationType.ALERT.name())
                            .build());

                    //Send Email
                    emailDTOS.add(EmailDTO.builder()
                            .setSubject(MessageConstant.Notification.BirthDay.HAPPY_BIRTHDAY)
                            .setEmailTemplate(EmailTemplate.HAPPY_BIRTHDAY.getValue())
                            .setReceiverEmail(e.getEmail())
                            .setIsHTML(Boolean.TRUE)
                            .setProperties(Map.ofEntries(entry(VariableConstant.Employee.FULLNAME, e.getFullName())))
                            .build());
                }
            });
            //save notification
            notificationRepository.saveAll(notifications);

            //send email
            emailService.sendEmailWithTemplate(emailDTOS);
        }
    }

    private boolean isDateNow(LocalDate date) {
        LocalDate now = LocalDate.now();
        return date.getDayOfMonth() == now.getDayOfMonth() && date.getMonthValue() == now.getMonthValue();
    }

    private EmailDTO setValueEmail(String sendTo, String fullName) {
        return EmailDTO.builder()
                .setSubject(MessageConstant.Notification.BirthDay.HAPPY_BIRTHDAY)
                .setEmailTemplate(EmailTemplate.HAPPY_BIRTHDAY.getValue())
                .setReceiverEmail(sendTo)
                .setIsHTML(Boolean.TRUE)
                .setProperties(Map.ofEntries(entry(VariableConstant.Employee.FULLNAME, fullName)))
                .build();
    }
}

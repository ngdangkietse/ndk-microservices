package dev.ngdangkiet.domain;

import lombok.*;

import java.io.Serializable;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder(setterPrefix = "set", builderClassName = "newBuilder")
public class JsonMessageEmail implements Serializable {
    private Long receiverId;
    private String receiverEmail;
    private String message;
    private String subject;
    private String attachment;
    private Map<String, Object> properties;
    private String emailTemplate;
    private Boolean isHtml;
}

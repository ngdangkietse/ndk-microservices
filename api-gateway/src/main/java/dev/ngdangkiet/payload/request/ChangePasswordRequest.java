package dev.ngdangkiet.payload.request;

import dev.ngdangkiet.validation.ValidationPassword;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author ngdangkiet
 * @since 11/16/2023
 */

@Getter
@Setter
public class ChangePasswordRequest implements Serializable {

    @ValidationPassword
    private String currentPassword;
    @ValidationPassword
    private String newPassword;
}

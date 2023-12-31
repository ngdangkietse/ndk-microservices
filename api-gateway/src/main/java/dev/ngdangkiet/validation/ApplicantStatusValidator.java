package dev.ngdangkiet.validation;

import dev.ngdangkiet.enums.recruitment.ApplicantStatus;
import dev.ngdangkiet.validation.annotation.ValidationApplicantStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * @author ngdangkiet
 * @since 11/30/2023
 */

public class ApplicantStatusValidator implements ConstraintValidator<ValidationApplicantStatus, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return ApplicantStatus.isValidApplicantStatus(value);
    }
}

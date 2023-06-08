package ro.axon.dot.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.YearMonth;
import java.util.Collections;
import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import ro.axon.dot.exceptions.BusinessErrorCode;
import ro.axon.dot.exceptions.BusinessException;
import ro.axon.dot.exceptions.BusinessException.BusinessExceptionElement;

@Documented
@Constraint(validatedBy = PeriodFormat.Validator.class)
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface PeriodFormat {
  String message() default "Period must be in format yyyy-MM";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  class Validator implements ConstraintValidator<PeriodFormat, String[]> {

    @Override
    public boolean isValid(String[] periods, ConstraintValidatorContext context) {
      if (periods == null) {
        return true;
      }
      for (String period : periods) {
        try {
          YearMonth.parse(period);
        } catch (Exception e) {
          throw new BusinessException(Collections.singletonList(
              BusinessExceptionElement.builder()
                  .errorCode(BusinessErrorCode.INVALID_PERIOD_FORMAT)
                  .build()));
        }
      }
      return true;
    }
  }
}

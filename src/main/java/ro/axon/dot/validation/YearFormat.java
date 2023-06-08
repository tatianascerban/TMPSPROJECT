package ro.axon.dot.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.Year;
import java.util.Collections;
import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import ro.axon.dot.exceptions.BusinessErrorCode;
import ro.axon.dot.exceptions.BusinessException;
import ro.axon.dot.exceptions.BusinessException.BusinessExceptionElement;

@Documented
@Constraint(validatedBy = YearFormat.Validator.class)
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface YearFormat {
  String message() default "Year must be in format yyyy";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  class Validator implements ConstraintValidator<YearFormat, String[]> {

    @Override
    public boolean isValid(String[] years, ConstraintValidatorContext context) {
      if (years == null) {
        return true;
      }
      for (String year : years) {
        try {
          Year.parse(year);
        } catch (Exception e) {
          throw new BusinessException(Collections.singletonList(
              BusinessExceptionElement.builder()
                  .errorCode(BusinessErrorCode.INVALID_YEAR_FORMAT)
                  .build()));
        }
      }
      return true;
    }
  }
}

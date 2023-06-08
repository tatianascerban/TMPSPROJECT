package ro.axon.dot.api;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ro.axon.dot.exceptions.BusinessException;
import ro.axon.dot.exceptions.ErrorDetails;
import ro.axon.dot.exceptions.ErrorDetails.ErrorDetailsItem;

@RestControllerAdvice
public class ApiExceptionHandler {

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ErrorDetails> handleBusinessException(BusinessException exception) {

    ErrorDetails errorDetails = new ErrorDetails();
    List<ErrorDetailsItem> errorDetailsItems = exception.getErrors().stream()
        .map(businessExceptionElement -> ErrorDetailsItem.builder()
            .errorCode(businessExceptionElement.getErrorCode().getErrorCode())
            .message(businessExceptionElement.getErrorCode().getDevMsg())
            .build())
        .collect(Collectors.toList());

    errorDetails.setErrors(errorDetailsItems);

    List<HttpStatus> statusList = exception.getErrors().stream()
        .map(errorItem -> errorItem.getErrorCode()
            .getStatus())
        .collect(Collectors.toList());

    Optional<HttpStatus> statusServerError = statusList.stream()
        .filter(HttpStatus::is5xxServerError)
        .findAny();

    HttpStatus status = statusServerError.orElseGet(() -> statusList.stream()
        .filter(HttpStatus::is4xxClientError)
        .findFirst()
        .orElse(HttpStatus.BAD_REQUEST));

    return ResponseEntity.status(status)
        .body(errorDetails);

  }

  @ExceptionHandler(value = MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorDetails> handleException(MethodArgumentNotValidException exception){
    var errorDetails = new ErrorDetails();
    errorDetails.setErrors(List.of(ErrorDetailsItem.builder()
        .errorCode(String.valueOf(HttpStatus.BAD_REQUEST.value()))
        .message(exception.getBindingResult().getAllErrors().stream()
            .map(objectError -> Objects.requireNonNull(objectError.getCodes())[0])
            .collect(Collectors.joining(",")))
        .build()));
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails);
  }
}

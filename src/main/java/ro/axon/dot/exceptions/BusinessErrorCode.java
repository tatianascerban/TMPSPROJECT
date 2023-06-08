package ro.axon.dot.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@JsonFormat(shape = Shape.OBJECT)
@Getter
public enum BusinessErrorCode {
  EMPLOYEE_ALREADY_EXISTING_EXCEPTION("EDOT0001409",
      "Employee with this username or email is already existing"),

  INVALID_TEAM_EXCEPTION("EDOT0002400",
      "Invalid team provided"),

  INVALID_YEAR_FORMAT("EDOT0003400",
      "Invalid year format"),

  INVALID_PERIOD_FORMAT("EDOT0004400",
      "Invalid period format"),

  EMPLOYEE_NOT_FOUND("EDOT0005400",
      "Employee with given id not found"),

  NEGATIVE_DAYS_OFF_EXCEPTION("EDOT0006400",
      "Number of days off can't be negative"),

  TEAM_NOT_FOUND("EDOT0007400",
      "Team with given id not found"),

  INVALID_EMPLOYEE_CREDENTIALS_EXCEPTION("EDOT0008400",
      "Username or password is incorrect"),

  TOKEN_GENERATION_EXCEPTION("EDOT0009500",
      "Error signing jwt token"),

  TOKEN_DETAILS_USERNAME_MISSING("EDOT0010500",
      "Error getting username from token details"),

  INVALID_ACCESS_TOKEN_PROVIDED("EDOT0011500",
      "Access token is invalid or expired"),

  LOGIN_INACTIVE_USER("EDOT0012500",
      "User can't login, status is INACTIVE"),

  TOKEN_DETAILS_EMPLOYEE_ID_MISSING("EDOT0013500",
      "Error getting employee id from token details"),

  INVALID_REFRESH_TOKEN_KEY_ID("EDOT0014500",
      "Provided refresh token has invalid key id"),

  INVALID_REFRESH_TOKEN_AUDIENCE("EDOT0015500",
      "Provided refresh token audience does not match"),

  TOKEN_AUDIENCE_MISSING("EDOT0016500",
      "Error getting token audience"),

  INVALID_REFRESH_TOKEN_STATUS("EDOT0017500",
      "Provided refresh token status is not active"),

  REFRESH_TOKEN_IS_EXPIRED("EDOT0018500",
      "Refresh token is expired"),

  TOKEN_CLAIM_SET_EXCEPTION("EDOT0019500",
      "Error getting token claims set"),

  INVALID_TOKEN_PROVIDED("EDOT0020500",
      "Provided value is not a token"),

  EMPLOYEE_UPDATE_VERSION_CONFLICT("EDOT0021409", "Conflict on employee's versions!"),


  LEAVE_REQUEST_NOT_FOUND("EDOT0022400",
      "Leave Request with given id not found"),

  LEAVE_REQUEST_PATCH_NOT_ALLOWED("EDOT0023400",
      "Leave request must have pending status"),

  LEAVE_REQUEST_VERSION_CONFLICT("ED0T0024409", "Conflict on leave request's versions!"),

  LEAVE_REQUEST_REJECTED_WITHOUT_REJECTION_REASON("ED0T0025400",
      "Leave Request type is REJECTION, but does not have rejectionReason"),

  DAYS_OFF_MISSING("EDOT0026400",
      "Days off for current year are not yet set"),

  LEAVE_REQUEST_DELETE_NOT_PERMITTED_REJECTED_STATUS("EDOT0027400",
      "Leave request with given id has rejected status"),

  LEAVE_REQUEST_DELETE_NOT_PERMITTED_APPROVED_DAYS_IN_PAST("EDOT0028400",
      "Leave request with given id has approved days in past"),

  LEAVE_REQUEST_CREATE_INVALID_PERIOD("EDOT0029400",
      "Cannot create leave request with invalid period!"),

  LEAVE_REQUEST_CREATE_INVALID_NO_DAYS_OFF("EDOT0030400",
      "Cannot create the leave request. The number of days related to the leave request exceeds the number of days available."),

  LEAVE_REQUEST_UPDATE_ALREADY_REJECTED("EDOT0031405",
      "Cannot update already rejected leave request!"),

  LEAVE_REQUEST_CREATE_DIFFERENT_YEARS("EDOT0032400",
      "Cannot create leave request with dates in different years!"),

  LEAVE_REQUEST_UPDATE_IN_PAST("EDOT0033405", "Cannot update leave request with date in the past!"),

  INVALID_DATE_PROVIDED("EDOT0034400",
      "Provided date is invalid");
  private final String errorCode;

  private final String devMsg;

  private final HttpStatus status;

  BusinessErrorCode(String errorCode, String devMsg) {
    this.errorCode = errorCode;
    this.devMsg = devMsg;
    this.status = HttpStatus.resolve(Integer.parseInt(errorCode
        .substring(errorCode.length() - 3)));
  }

}

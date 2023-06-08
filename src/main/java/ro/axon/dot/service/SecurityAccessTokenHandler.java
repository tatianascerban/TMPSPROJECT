package ro.axon.dot.service;

import java.util.Collections;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import ro.axon.dot.exceptions.BusinessErrorCode;
import ro.axon.dot.exceptions.BusinessException;
import ro.axon.dot.exceptions.BusinessException.BusinessExceptionElement;

@Service
public class SecurityAccessTokenHandler {

  public String getUsernameFromAuthenticationToken() {
    var authentication = getJwtAuthenticationToken();
    if (authentication.getToken().getClaim("username") == null) {
      throw new BusinessException(Collections.singletonList(BusinessExceptionElement.builder()
          .errorCode(BusinessErrorCode.TOKEN_DETAILS_USERNAME_MISSING)
          .build()));
    }
    return authentication.getToken().getClaim("username");
  }

  public String getEmployeeIdFromAuthenticationToken(){
    var authentication = getJwtAuthenticationToken();
    if (authentication.getToken().getSubject() == null) {
      throw new BusinessException(Collections.singletonList(BusinessExceptionElement.builder()
          .errorCode(BusinessErrorCode.TOKEN_DETAILS_EMPLOYEE_ID_MISSING)
          .build()));
    }

    return authentication.getToken().getSubject();
  }

  private JwtAuthenticationToken getJwtAuthenticationToken() {
    var authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication instanceof JwtAuthenticationToken) {
      return (JwtAuthenticationToken) authentication;
    }

    throw new IllegalStateException("No JWT Auth found in Security Context!");
  }
}

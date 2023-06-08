package ro.axon.dot.service;

import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;
import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.axon.dot.configuration.JwtProperties;
import ro.axon.dot.domain.entity.EmployeeEty;
import ro.axon.dot.domain.entity.RefreshTokenEty;
import ro.axon.dot.domain.entity.enums.EmployeeStatus;
import ro.axon.dot.domain.entity.enums.RefreshTokenStatus;
import ro.axon.dot.domain.repository.EmployeeRepository;
import ro.axon.dot.domain.repository.RefreshTokenRepository;
import ro.axon.dot.exceptions.BusinessErrorCode;
import ro.axon.dot.exceptions.BusinessException;
import ro.axon.dot.exceptions.BusinessException.BusinessExceptionElement;
import ro.axon.dot.model.request.LoginRequestDTO;
import ro.axon.dot.model.request.TokenRequestDTO;
import ro.axon.dot.model.response.LoginResponseDTO;
import ro.axon.dot.model.response.UserDetailsResponse;
import ro.axon.dot.model.response.UserDetailsResponse.TeamDetails;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginService {

  private final EmployeeRepository employeeRepository;

  private final TokenGeneratorHandler tokenGeneratorHandler;

  private final RefreshTokenRepository refreshTokenRepository;

  private final JwtProperties jwtProperties;

  private final UuidGeneratorHandler uuidGeneratorHandler;

  private final Clock clock;

  private final SecurityAccessTokenHandler securityAccessTokenHandler;

  @Transactional
  public LoginResponseDTO login(LoginRequestDTO loginRequest) {
    Optional<EmployeeEty> employee = employeeRepository.findEmployeeEtyByUsername(
        loginRequest.getUsername());

    if (employee.isEmpty() || !Objects.equals(loginRequest.getPassword(),
        employee.get().getPassword())) {
      throw new BusinessException(Collections.singletonList(BusinessExceptionElement.builder()
          .errorCode(BusinessErrorCode.INVALID_EMPLOYEE_CREDENTIALS_EXCEPTION).build()));
    }
    var employeeEty = employee.get();

    if (employeeEty.getStatus() == EmployeeStatus.INACTIVE) {
      throw new BusinessException(Collections.singletonList(
          BusinessExceptionElement.builder().errorCode(BusinessErrorCode.LOGIN_INACTIVE_USER)
              .build()));
    }
    var refreshTokenUuid = uuidGeneratorHandler.generateUuid();

    var accessToken = tokenGeneratorHandler.generateToken(employeeEty,
        jwtProperties.getAccessDuration(), jwtProperties.getAccessType(), jwtProperties.getKeyId());

    var refreshToken = tokenGeneratorHandler.generateToken(employeeEty,
        jwtProperties.getRefreshDuration(), jwtProperties.getRefreshType(), refreshTokenUuid);

    try {
      var accessTokenExpiration = accessToken.getJWTClaimsSet().getExpirationTime();
      var refreshTokenExpiration = refreshToken.getJWTClaimsSet().getExpirationTime();

      createRefreshTokenEty(employeeEty, refreshTokenExpiration.toInstant(), refreshTokenUuid);

      return LoginResponseDTO.builder().accessToken(accessToken.serialize())
          .refreshToken(refreshToken.serialize()).accessTokenExpirationTime(
              OffsetDateTime.ofInstant(accessTokenExpiration.toInstant(), ZoneOffset.UTC))
          .refreshTokenExpirationTime(
              OffsetDateTime.ofInstant(refreshTokenExpiration.toInstant(), ZoneOffset.UTC)).build();

    } catch (ParseException e) {
      throw new BusinessException(Collections.singletonList(
          BusinessExceptionElement.builder().errorCode(BusinessErrorCode.TOKEN_GENERATION_EXCEPTION)
              .build()));
    }
  }

  @Transactional
  public LoginResponseDTO refresh(TokenRequestDTO refreshRequest) {

    SignedJWT providedToken = getTokenFromRequest(refreshRequest);
    String keyId = providedToken.getHeader().getKeyID();

    var refreshTokenEty = refreshTokenRepository.findRefreshTokenEtyById(keyId).orElseThrow(
        () -> new BusinessException(Collections.singletonList(BusinessExceptionElement.builder()
            .errorCode(BusinessErrorCode.INVALID_REFRESH_TOKEN_KEY_ID).build())));

    checkExistingTokenWithProvided(refreshTokenEty, providedToken);

    var employeeEty = employeeRepository.findEmployeeEtyById(refreshTokenEty.getAudience().getId())
        .orElseThrow(() -> new BusinessException(Collections.singletonList(
            BusinessExceptionElement.builder().errorCode(BusinessErrorCode.EMPLOYEE_NOT_FOUND)
                .build())));

    var accessToken = tokenGeneratorHandler.generateToken(employeeEty,
        jwtProperties.getAccessDuration(), jwtProperties.getAccessType(), jwtProperties.getKeyId());

    var newRefreshToken = tokenGeneratorHandler.generateToken(employeeEty,
        jwtProperties.getRefreshDuration(), jwtProperties.getRefreshType(),
        refreshTokenEty.getId());

    try {
      var accessTokenExpirationTime = accessToken.getJWTClaimsSet().getExpirationTime();
      var refreshTokenExpirationTime = newRefreshToken.getJWTClaimsSet().getExpirationTime();

      updateRefreshTokenEty(refreshTokenEty, refreshTokenExpirationTime.toInstant());

      return LoginResponseDTO.builder().accessToken(accessToken.serialize())
          .refreshToken(newRefreshToken.serialize()).accessTokenExpirationTime(
              OffsetDateTime.ofInstant(accessTokenExpirationTime.toInstant(), ZoneOffset.UTC))
          .refreshTokenExpirationTime(
              OffsetDateTime.ofInstant(refreshTokenExpirationTime.toInstant(), ZoneOffset.UTC))
          .build();
    } catch (ParseException e) {
      throw new BusinessException(Collections.singletonList(
          BusinessExceptionElement.builder().errorCode(BusinessErrorCode.TOKEN_GENERATION_EXCEPTION)
              .build()));
    }
  }

  @Transactional
  public void logout(TokenRequestDTO logoutRequest) {
    SignedJWT providedToken = getTokenFromRequest(logoutRequest);
    String keyId = providedToken.getHeader().getKeyID();

    var refreshTokenEty = refreshTokenRepository.findRefreshTokenEtyById(keyId).orElseThrow(
        () -> new BusinessException(Collections.singletonList(BusinessExceptionElement.builder()
            .errorCode(BusinessErrorCode.INVALID_REFRESH_TOKEN_KEY_ID).build())));

    checkExistingTokenWithProvided(refreshTokenEty, providedToken);

    refreshTokenEty.setStatus(RefreshTokenStatus.REVOKED);
    refreshTokenEty.setModifyTime(clock.instant());
    refreshTokenRepository.save(refreshTokenEty);

  }

  @Transactional
  public UserDetailsResponse getLoggedUserDetails() {
    var loggedUserId = securityAccessTokenHandler.getEmployeeIdFromAuthenticationToken();
    var loggedUser = employeeRepository.findEmployeeEtyById(loggedUserId).orElseThrow(
        () -> new BusinessException(Collections.singletonList(
            BusinessExceptionElement.builder().errorCode(BusinessErrorCode.EMPLOYEE_NOT_FOUND)
                .build())));

    var teamDetails = TeamDetails.builder()
        .teamId(loggedUser.getTeam().getId())
        .name(loggedUser.getTeam().getName())
        .build();

    return UserDetailsResponse.builder()
        .username(loggedUser.getUsername())
        .employeeId(loggedUserId)
        .roles(Collections.singletonList(loggedUser.getRole()))
        .teamDetails(teamDetails)
        .build();
  }

  private static SignedJWT getTokenFromRequest(TokenRequestDTO logoutRequest) {
    try {
      return SignedJWT.parse(logoutRequest.getRefreshToken());
    } catch (ParseException e) {
      throw new BusinessException(Collections.singletonList(
          BusinessExceptionElement.builder().errorCode(BusinessErrorCode.TOKEN_GENERATION_EXCEPTION)
              .build()));
    }
  }

  private void createRefreshTokenEty(EmployeeEty employeeEty, Instant refreshTokenExpiration,
      String refreshTokenUuid) {

    var refreshToken = RefreshTokenEty.builder().id(refreshTokenUuid).audience(employeeEty)
        .status(RefreshTokenStatus.ACTIVE).createTime(clock.instant()).modifyTime(clock.instant())
        .expireTime(refreshTokenExpiration).build();

    refreshTokenRepository.save(refreshToken);
  }

  private void checkExistingTokenWithProvided(RefreshTokenEty existingToken,
      SignedJWT providedToken) {
    try {
      var providedClaims = providedToken.getJWTClaimsSet();

      String existingAudience = existingToken.getAudience().getId();
      String providedAudience = providedClaims.getSubject();

      if (!existingAudience.equals(providedAudience)) {
        throw new BusinessException(Collections.singletonList(BusinessExceptionElement.builder()
            .errorCode(BusinessErrorCode.INVALID_REFRESH_TOKEN_AUDIENCE).build()));
      }

      if (existingToken.getStatus() != RefreshTokenStatus.ACTIVE) {
        throw new BusinessException(Collections.singletonList(BusinessExceptionElement.builder()
            .errorCode(BusinessErrorCode.INVALID_REFRESH_TOKEN_STATUS).build()));
      }

      if (existingToken.getExpireTime().isBefore(clock.instant())
          || providedClaims.getExpirationTime().before(Date.from(clock.instant()))) {
        throw new BusinessException(Collections.singletonList(
            BusinessExceptionElement.builder().errorCode(BusinessErrorCode.REFRESH_TOKEN_IS_EXPIRED)
                .build()));
      }
    } catch (ParseException e) {
      throw new BusinessException(Collections.singletonList(
          BusinessExceptionElement.builder().errorCode(BusinessErrorCode.TOKEN_CLAIM_SET_EXCEPTION)
              .build()));
    }

  }

  private void updateRefreshTokenEty(RefreshTokenEty refreshTokenEty, Instant expirationTime) {
    refreshTokenEty.setModifyTime(clock.instant());
    refreshTokenEty.setExpireTime(expirationTime);
    refreshTokenRepository.save(refreshTokenEty);
  }

}

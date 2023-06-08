package ro.axon.dot.service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.time.Clock;
import java.util.Collections;
import java.util.Date;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ro.axon.dot.configuration.JwtProperties;
import ro.axon.dot.configuration.JwtKeyProperties;
import ro.axon.dot.domain.entity.EmployeeEty;
import ro.axon.dot.exceptions.BusinessErrorCode;
import ro.axon.dot.exceptions.BusinessException;
import ro.axon.dot.exceptions.BusinessException.BusinessExceptionElement;

@Component
@RequiredArgsConstructor
public class TokenGeneratorHandler {

  private final JwtProperties jwtProperties;

  private final JwtKeyProperties jwtKeyProperties;

  private final Clock clock;

  private RSASSASigner signer;

  @PostConstruct
  public void setup() {
    signer = new RSASSASigner((jwtKeyProperties.getPrivateKeyLocation()));
  }

  public SignedJWT generateToken(EmployeeEty employeeEty, int duration, String type, String keyId) {
    JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
        .subject(employeeEty.getId())
        .audience(jwtProperties.getAudience())
        .issuer(jwtProperties.getIssuer())
        .expirationTime(
            Date.from(clock.instant().plusSeconds(duration)))
        .notBeforeTime(Date.from(clock.instant()))
        .claim("username", employeeEty.getUsername())
        .claim("email", employeeEty.getEmail())
        .claim("type", type)
        .claim("roles", Collections.singletonList(employeeEty.getRole()))
        .build();

    var jwtToken = new SignedJWT(new JWSHeader
        .Builder(JWSAlgorithm.RS256).keyID(keyId).build(), claimsSet);
    try {
      jwtToken.sign(signer);
      return jwtToken;
    } catch (JOSEException e) {
      throw new BusinessException(Collections.singletonList(BusinessExceptionElement.builder()
          .errorCode(BusinessErrorCode.TOKEN_GENERATION_EXCEPTION)
          .build()));
    }

  }
}

package ro.axon.dot.model.response;

import java.time.OffsetDateTime;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LoginResponseDTO {

  private String accessToken;

  private String refreshToken;

  private OffsetDateTime accessTokenExpirationTime;

  private OffsetDateTime refreshTokenExpirationTime;
}

package ro.axon.dot.configuration;

import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "ro.axon.dot")
public class RolesProperties {

  private List<String> roles;
}
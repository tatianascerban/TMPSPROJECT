package ro.axon.dot.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "ro.axon.dot.create-employee")
public class EmployeeProperties {

  private String passwordSequence;

}

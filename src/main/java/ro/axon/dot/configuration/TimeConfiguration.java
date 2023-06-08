package ro.axon.dot.configuration;

import java.time.Clock;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Time related beans.
 */
@Configuration
public class TimeConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public Clock clock() {
    return Clock.systemDefaultZone();
  }

}

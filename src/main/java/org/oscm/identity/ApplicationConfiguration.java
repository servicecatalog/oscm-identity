/**
 * *****************************************************************************
 *
 * <p>Copyright FUJITSU LIMITED 2019
 *
 * <p>Creation Date: July 17, 2019
 *
 * <p>*****************************************************************************
 */
package org.oscm.identity;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/** SpringBoot main configuration class */
@Configuration
@ComponentScan(basePackages = "org.oscm.identity")
public class ApplicationConfiguration {

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }
}

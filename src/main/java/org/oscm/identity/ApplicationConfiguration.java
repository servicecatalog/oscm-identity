/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2019
 *
 *  Creation Date: Jul 17, 2019
 *
 *******************************************************************************/

package org.oscm.identity;

import org.apache.catalina.connector.Connector;
import org.oscm.identity.oidc.request.proxy.ProxyHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.net.InetSocketAddress;
import java.net.Proxy;

/** SpringBoot main configuration class */
@Configuration
@ComponentScan(basePackages = "org.oscm.identity")
public class ApplicationConfiguration {

  @Value("${http.port}")
  private int httpPort;

  @Autowired private ProxyHandler proxyHandler;

  @Bean
  @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
  public RestTemplate restTemplate() {

    RestTemplate restTemplate = new RestTemplate();

    if (proxyHandler.isRequired()) {
      SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
      Proxy proxy =
          new Proxy(
              Proxy.Type.HTTP,
              new InetSocketAddress(proxyHandler.getHost(), proxyHandler.getPort()));
      factory.setProxy(proxy);
      restTemplate.setRequestFactory(factory);
    }

    return restTemplate;
  }

  @Bean
  public ServletWebServerFactory servletContainer() {
    Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
    connector.setPort(httpPort);

    TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
    tomcat.addAdditionalTomcatConnectors(connector);
    return tomcat;
  }
}

/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2019
 *
 *  Creation Date: Jul 26, 2019
 *
 *******************************************************************************/
package org.oscm.identity.oidc.request.proxy;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/** Simple handler class for getting the proxy related settings form application.properties. */
@Component
@Getter
public class ProxyHandler {

  @Value("${proxy.required}")
  private boolean required;

  @Value("${proxy.host}")
  private String host;

  @Value("${proxy.port}")
  private int port;
}

/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2019
 *
 *  Creation Date: Jul 25, 2019
 *
 *******************************************************************************/

package org.oscm.identity.oidc.request;

import org.oscm.identity.error.IdentityProviderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/** Simple handler for getting proper RequestManager instance */
@Component
public class RequestHandler {

  RestTemplate restTemplate;

  @Autowired
  public RequestHandler(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  /**
   * Retrieves RequestManager instance base on given provider
   *
   * @param provider external identity provider name
   * @return RequestManager instance
   */
  public RequestManager getRequestManager(String provider) {

    RequestManager requestManager;

    switch (provider) {
      case "default":
        requestManager = new DefaultRequestManager(this.restTemplate);
        break;
      default:
        throw new IdentityProviderException(
            "No request manager implementation for identity provider [" + provider + "]");
    }
    return requestManager;
  }
}

/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2019
 *
 *  Creation Date: Jul 25, 2019
 *
 *******************************************************************************/

package org.oscm.identity.oidc.request;

import org.oscm.identity.error.IdentityProviderException;

/** Simple handler for getting proper RequestManager instance */
public class RequestHandler {

  /**
   * Retrieves RequestManager instance base on given provider
   *
   * @param provider external identity provider name
   * @return RequestManager instance
   */
  public static RequestManager getRequestManager(String provider) {

    RequestManager requestManager;

    switch (provider) {
      case "default":
        requestManager = new DefaultRequestManager();
        break;
      default:
        throw new IdentityProviderException(
            "No request manager implementation for identity provider [" + provider + "]");
    }
    return requestManager;
  }
}

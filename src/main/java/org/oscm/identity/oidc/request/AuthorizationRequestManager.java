/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2019
 *
 *  Creation Date: Jun 18, 2019
 *
 *******************************************************************************/
package org.oscm.identity.oidc.request;

import org.oscm.identity.error.IdentityProviderException;

public class AuthorizationRequestManager {

  public static AuthorizationRequest buildRequest(String provider) {

    AuthorizationRequest request;

    switch (provider) {
      case "default":
        request = new DefaultAuthorizationRequest();
        break;
      default:
        throw new IdentityProviderException(
            "No auth request implementation for identity provider [" + provider + "]");
    }

    return request;
  }
}

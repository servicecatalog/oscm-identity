/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2019
 *
 *  Creation Date: Jul 22, 2019
 *
 *******************************************************************************/
package org.oscm.identity.oidc.request;

import org.oscm.identity.error.IdentityProviderException;

public class LogoutRequestManager {

  public static LogoutRequest buildRequest(String provider) {

    LogoutRequest request;

    switch (provider) {
      case "default":
        request = new DefaultLogoutRequest();
        break;
      default:
        throw new IdentityProviderException(
            "No logout request implementation for identity provider [" + provider + "]");
    }

    return request;
  }
}

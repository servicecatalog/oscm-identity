package org.oscm.identity.oidc.request;

import org.oscm.identity.error.IdentityProviderException;

public class RequestHandler {

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

/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2019
 *
 *  Creation Date: Jun 18, 2019
 *
 *******************************************************************************/

package org.oscm.identity.oidc.request;

import org.oscm.identity.error.IdentityProviderException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class DefaultAuthorizationRequest extends AuthorizationRequest {

  @Override
  public void execute(HttpServletResponse response) {

    String url = this.buildUrl();
    try {
      response.sendRedirect(url);
    } catch (IOException exc) {
      throw new IdentityProviderException("Problem with contacting identity provider", exc);
    }
  }

  public String buildUrl() {
    return new StringBuilder(getBaseUrl())
        .append("?")
        .append("client_id=" + getClientId())
        .append("&")
        .append("response_type=" + getResponseType())
        .append("&")
        .append("redirect_uri=" + getRedirectUrl())
        .append("&")
        .append("response_mode=" + getResponseMode())
        .append("&")
        .append("scope=" + getScope())
        .append("&")
        .append("nonce=" + getNonce())
        .append("&")
        .append("state=" + getState())
        .toString();
  }
}

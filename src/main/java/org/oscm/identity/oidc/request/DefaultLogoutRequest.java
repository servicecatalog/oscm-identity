/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2019
 *
 *  Creation Date: Jul 22, 2019
 *
 *******************************************************************************/

package org.oscm.identity.oidc.request;

import org.oscm.identity.error.IdentityProviderException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DefaultLogoutRequest extends LogoutRequest {

  @Override
  public void execute(HttpServletResponse response) {
    String url = this.buildUrl();
    try {
      response.sendRedirect(url);
    } catch (IOException exc) {
      throw new IdentityProviderException("Problem with contacting identity provider", exc);
    }
  }

  @Override
  public String buildUrl() {

    return new StringBuilder(getBaseUrl())
        .append("?")
        .append("post_logout_redirect_uri=" + getRedirectUrl())
        .toString();
  }
}

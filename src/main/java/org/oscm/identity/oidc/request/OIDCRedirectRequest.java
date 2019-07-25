package org.oscm.identity.oidc.request;

import javax.servlet.http.HttpServletResponse;

public interface OIDCRedirectRequest {

  void execute(HttpServletResponse response);
  String buildUrl();
}

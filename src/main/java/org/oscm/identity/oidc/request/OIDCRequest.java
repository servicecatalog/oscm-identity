package org.oscm.identity.oidc.request;

import lombok.Getter;

public abstract class OIDCRequest {

  @Getter protected String baseUrl;

  @Getter protected String redirectUrl;

  public abstract String buildUrl();
}

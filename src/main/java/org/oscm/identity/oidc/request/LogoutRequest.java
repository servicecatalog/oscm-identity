package org.oscm.identity.oidc.request;

public abstract class LogoutRequest extends OIDCRequest {

  public LogoutRequest baseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
    return this;
  }

  public LogoutRequest redirectUrl(String redirectUrl) {
    this.redirectUrl = redirectUrl;
    return this;
  }
}

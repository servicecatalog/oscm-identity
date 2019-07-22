/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2019
 *
 *  Creation Date: Jun 18, 2019
 *
 *******************************************************************************/
package org.oscm.identity.oidc.request;

import lombok.Getter;

@Getter
public abstract class AuthorizationRequest extends OIDCRequest {

  private String clientId;

  private String responseType;

  private String responseMode;

  private String scope;

  private String nonce;

  private String state;

  public AuthorizationRequest baseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
    return this;
  }

  public AuthorizationRequest redirectUrl(String redirectUrl) {
    this.redirectUrl = redirectUrl;
    return this;
  }

  public AuthorizationRequest clientId(String clientId) {
    this.clientId = clientId;
    return this;
  }

  public AuthorizationRequest responseType(String responseType) {
    this.responseType = responseType;
    return this;
  }

  public AuthorizationRequest scope(String scope) {
    this.scope = scope;
    return this;
  }

  public AuthorizationRequest responseMode(String responseMode) {
    this.responseMode = responseMode;
    return this;
  }

  public AuthorizationRequest nonce(String nonce) {
    this.nonce = nonce;
    return this;
  }

  public AuthorizationRequest state(String state) {
    this.state = state;
    return this;
  }
}

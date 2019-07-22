/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2019
 *
 *  Creation Date: Jul 22, 2019
 *
 *******************************************************************************/

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

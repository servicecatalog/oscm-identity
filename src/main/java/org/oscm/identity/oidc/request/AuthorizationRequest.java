/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2019
 *
 *  Creation Date: Jun 18, 2019
 *
 *******************************************************************************/

package org.oscm.identity.oidc.request;

import lombok.Data;

@Data
public abstract class AuthorizationRequest implements OIDCRedirectRequest{

  private String baseUrl;
  private String redirectUrl;
  private String clientId;
  private String responseType;
  private String responseMode;
  private String scope;
  private String nonce;
  private String state;
}

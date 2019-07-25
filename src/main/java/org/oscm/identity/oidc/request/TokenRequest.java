package org.oscm.identity.oidc.request;

import lombok.Data;

@Data
public abstract class TokenRequest implements OIDCReturnRequest {

  private String baseUrl;
  private String clientId;
  private String clientSecret;
  private String code;
  private String redirectUrl;
  private String grantType;
}

package org.oscm.identity.controller;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RefreshBody {

  @NotNull private String refreshToken;
  @NotNull private String grantType;
  private String clientId;
  private String scope;
  private String clientSecret;
  private String redirectUrl;
  private String baseUrl;
  private String idToken;
  private String tenantId;
  private String state;
}

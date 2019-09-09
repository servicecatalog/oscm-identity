/**
 * *****************************************************************************
 *
 * <p>Copyright FUJITSU LIMITED 2019
 *
 * <p>Creation Date: Sep 09, 2019
 *
 * <p>*****************************************************************************
 */
package org.oscm.identity.oidc.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

/** Object representing the http request to identity provider refresh token endpoint */
@Data
public class RefreshBody {

  @NotNull private String refreshToken;
  @NotNull private String grantType;
  @NotNull private String scope;
  private String clientId;
  private String clientSecret;
  private String redirectUrl;
  private String baseUrl;
  private String idToken;
  private String tenantId;
  private String state;
}

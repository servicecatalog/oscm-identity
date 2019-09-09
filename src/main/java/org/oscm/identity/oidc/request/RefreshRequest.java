/**
 * *****************************************************************************
 *
 * <p>Copyright FUJITSU LIMITED 2019
 *
 * <p>Creation Date: Sep 04, 2019
 *
 * <p>*****************************************************************************
 */
package org.oscm.identity.oidc.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

/** Simple class containing information about the refresh request */
@Data
public abstract class RefreshRequest implements OIDCReturnRequest {

  @NotNull private String clientId;
  @NotNull private String clientSecret;
  @NotNull private String scope;
  @NotNull private String refreshToken;
  @NotNull private String grantType;
  private String redirectUrl;
  private String baseUrl;
  private String idToken;
  private String tenantId;
  private String state;
}

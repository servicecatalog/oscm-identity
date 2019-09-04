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

/** Simple class containing information about the refresh request */
@Data
public abstract class RefreshRequest implements OIDCReturnRequest {

  private String baseUrl;
  private String clientId;
  private String clientSecret;
  private String scope;
  private String refreshToken;
  private String redirectUrl;
  private String grantType;
}

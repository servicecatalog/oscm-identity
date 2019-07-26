/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2019
 *
 *  Creation Date: Jul 25, 2019
 *
 *******************************************************************************/

package org.oscm.identity.oidc.request;

import lombok.Data;

/** Simple class containing information about the toke request */
@Data
public abstract class TokenRequest implements OIDCReturnRequest {

  private String baseUrl;
  private String clientId;
  private String clientSecret;
  private String code;
  private String redirectUrl;
  private String grantType;
}

/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2019
 *
 *  Creation Date: Jul 22, 2019
 *
 *******************************************************************************/

package org.oscm.identity.oidc.request;

import lombok.Data;

@Data
public abstract class LogoutRequest implements OIDCRedirectRequest {

  private String baseUrl;
  private String redirectUrl;
}

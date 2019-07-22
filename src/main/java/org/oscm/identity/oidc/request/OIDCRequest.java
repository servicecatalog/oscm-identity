/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2019
 *
 *  Creation Date: Jul 22, 2019
 *
 *******************************************************************************/

package org.oscm.identity.oidc.request;

import lombok.Getter;

@Getter
public abstract class OIDCRequest {

  protected String baseUrl;

  protected String redirectUrl;

  public abstract String buildUrl();
}

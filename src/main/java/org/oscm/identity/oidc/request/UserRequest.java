/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2019
 *
 *  Creation Date: Jul 26, 2019
 *
 *******************************************************************************/
package org.oscm.identity.oidc.request;

import lombok.Data;

@Data
public abstract class UserRequest implements OIDCReturnRequest {

  private String baseUrl;
  private String token;
}

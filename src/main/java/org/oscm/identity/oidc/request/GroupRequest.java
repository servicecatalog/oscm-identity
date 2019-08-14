/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2019
 *
 *  Creation Date: Aug 7, 2019
 *
 *******************************************************************************/

package org.oscm.identity.oidc.request;

import lombok.Data;

/** Object representing the http request to identity provider group endpoint */
@Data
public abstract class GroupRequest implements OIDCReturnRequest{

  private String baseUrl;
  private String token;
}

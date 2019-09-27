/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2019
 *
 *  Creation Date: Sep 11, 2019
 *
 *******************************************************************************/
package org.oscm.identity.model.json;

import lombok.Builder;
import lombok.Data;
import org.oscm.identity.commons.AccessType;

/** Object representing json with access token response */
@Data
@Builder(builderMethodName = "of")
public class AccessTokenDTO {

  private String accessToken;
  private AccessType accessType;
}

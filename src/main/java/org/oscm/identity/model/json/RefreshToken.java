/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2019
 *
 *  Creation Date: Sep 16, 2019
 *
 *******************************************************************************/
package org.oscm.identity.model.json;

import lombok.Builder;
import lombok.Data;

/** Object representing json with refresh token request/response */
@Data
@Builder(builderMethodName = "of")
public class RefreshToken {

  private String accessToken;
  private String refreshToken;
}

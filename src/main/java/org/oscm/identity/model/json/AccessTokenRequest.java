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

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/** Object representing json with access token request */
@Data
@Builder(builderMethodName = "of")
public class AccessTokenRequest {

  @NotNull @NotBlank private String clientId;
  @NotNull @NotBlank private String clientSecret;
  @NotNull @NotBlank private String scope;
}

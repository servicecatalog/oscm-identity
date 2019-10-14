/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2019
 *
 *  Creation Date: Oct 9, 2019
 *
 *******************************************************************************/
package org.oscm.identity.model.json;

import lombok.Builder;
import lombok.Data;

/** Object representing json with id token */
@Data
@Builder(builderMethodName = "of")
public class IdTokenDTO {

  private String idToken;
}

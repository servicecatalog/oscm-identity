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

/** Simple object representing json with user id */
@Data
@Builder(builderMethodName = "of")
public class UserIdDTO {

  private String userId;
}

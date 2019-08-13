/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2019
 *
 *  Creation Date: Aug 9, 2019
 *
 *******************************************************************************/

package org.oscm.identity.model.json;

import lombok.Builder;
import lombok.Data;

/** Simple object representing json with user group */
@Data
@Builder(builderMethodName = "of")
public class UserGroup {

  private String id;
  private String name;
  private String description;
}

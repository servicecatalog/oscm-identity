/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2019
 *
 *  Creation Date: Aug 14, 2019
 *
 *******************************************************************************/
package org.oscm.identity.model.json;

import lombok.Builder;
import lombok.Data;

/** Simple object representing json with error information */
@Data
@Builder(builderMethodName = "of")
public class ErrorResponse {

  private String error;
  private String errorDescription;
}

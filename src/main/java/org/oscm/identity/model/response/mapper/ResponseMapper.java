/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2019
 *
 *  Creation Date: Jul 26, 2019
 *
 *******************************************************************************/

package org.oscm.identity.model.response.mapper;

import org.json.JSONException;
import org.json.JSONObject;
import org.oscm.identity.model.response.UserInfoResponse;

/** Interface for mapping json responses to objects used in response entities */
public interface ResponseMapper {

  /**
   * Maps json object to object containing user information
   *
   * @param json object to be mapped
   * @return object containing user information
   * @throws JSONException
   */
  UserInfoResponse getUserResponse(JSONObject json) throws JSONException;
}

/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2019
 *
 *  Creation Date: Jul 26, 2019
 *
 *******************************************************************************/

package org.oscm.identity.model.response;

import org.json.JSONException;
import org.json.JSONObject;
import org.oscm.identity.model.json.UserGroup;
import org.oscm.identity.model.json.UserInfo;

/** Interface for mapping json responses to objects used in response entities */
public interface ResponseMapper {

  /**
   * Maps json object to object containing user information
   *
   * @param json object to be mapped
   * @return object containing user information
   * @throws JSONException
   */
  UserInfo getUserInfo(JSONObject json) throws JSONException;

  /**
   * Maps json object to object containing user group information
   *
   * @param json object to be mapped
   * @return object containing user group information
   * @throws JSONException
   */
  UserGroup getUserGroup(JSONObject json) throws JSONException;
}

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
import org.oscm.identity.model.json.AccessToken;
import org.oscm.identity.model.json.UserGroup;
import org.oscm.identity.model.json.UserInfo;

import java.util.Set;

/** Interface for mapping json responses to objects used in response entities */
public interface ResponseMapper {

  /**
   * Maps json object to object representing user information
   *
   * @param json object to be mapped
   * @return object representing user information
   * @throws JSONException
   */
  UserInfo getUserInfo(JSONObject json) throws JSONException;

  /**
   * Maps json object to object representing user group
   *
   * @param json object to be mapped
   * @return object representing user group
   * @throws JSONException
   */
  UserGroup getUserGroup(JSONObject json) throws JSONException;

  /**
   * Maps json object to object representing groups which user belongs to
   *
   * @param json object to be mapped
   * @return object representing groups which user belongs to
   * @throws JSONException
   */
  Set<UserGroup> getGroupsUserBelongsTo(JSONObject json) throws JSONException;

  /**
   * Maps json object to object representing group members
   *
   * @param json object to be mapped
   * @return object representing group members
   * @throws JSONException
   */
  Set<UserInfo> getGroupMembers(JSONObject json) throws JSONException;

  /**
   * Maps json object to object representing groups
   *
   * @param json object to be mapped
   * @return object representing groups
   * @throws JSONException
   */
  Set<UserGroup> getGroups(JSONObject json) throws JSONException;

  /**
   * maps json object to object representing access token
   *
   * @param json object to be mapped
   * @return object representing access token
   * @throws JSONException
   */
  AccessToken getAccessToken(JSONObject json) throws JSONException;
}

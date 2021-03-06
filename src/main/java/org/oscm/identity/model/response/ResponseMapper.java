/**
 * *****************************************************************************
 *
 * <p>Copyright FUJITSU LIMITED 2019
 *
 * <p>Creation Date: Jul 26, 2019
 *
 * <p>*****************************************************************************
 */
package org.oscm.identity.model.response;

import org.json.JSONException;
import org.json.JSONObject;
import org.oscm.identity.error.ResourceNotFoundException;
import org.oscm.identity.model.json.*;

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
  UserInfoDTO getUserInfo(JSONObject json) throws JSONException;

  /**
   * Maps json object to object representing user group
   *
   * @param json object to be mapped
   * @return object representing user group
   * @throws JSONException
   */
  UserGroupDTO getUserGroup(JSONObject json) throws JSONException;

  /**
   * Maps json object to object representing groups which user belongs to
   *
   * @param json object to be mapped
   * @return object representing groups which user belongs to
   * @throws JSONException
   */
  Set<UserGroupDTO> getGroupsUserBelongsTo(JSONObject json) throws JSONException;

  /**
   * Maps json object to object representing group members
   *
   * @param json object to be mapped
   * @return object representing group members
   * @throws JSONException
   */
  Set<UserInfoDTO> getGroupMembers(JSONObject json) throws JSONException;

  /**
   * Maps json object to object representing groups
   *
   * @param json object to be mapped
   * @return object representing groups
   * @throws JSONException
   */
  Set<UserGroupDTO> getGroups(JSONObject json) throws JSONException;

  /**
   * Searches provided JSON for group of requested name and maps it to User Group representation
   *
   * @param json object to be mapped
   * @param requestedGroupName name of the group that is requested
   * @param isLoggable determines whether errors thrown in the method should be logged
   * @return object representing User Group
   * @throws JSONException
   */
  UserGroupDTO getGroup(JSONObject json, String requestedGroupName, boolean isLoggable)
      throws ResourceNotFoundException, JSONException;

  /**
   * maps json object to object representing access token
   *
   * @param json object to be mapped
   * @return object representing access token
   * @throws JSONException
   */
  AccessTokenDTO getAccessToken(JSONObject json) throws JSONException;

  /**
   * maps json object to object representing refresh token object
   *
   * @param json object to be mapped
   * @return object representing access token
   * @throws JSONException
   */
  RefreshTokenDTO getRefreshToken(JSONObject json) throws JSONException;

  /**
   * maps json object to object representing id token object
   *
   * @param json object to be mapped
   * @return object representing id token
   * @throws JSONException
   */
  IdTokenDTO getIdToken(JSONObject json) throws JSONException;
}

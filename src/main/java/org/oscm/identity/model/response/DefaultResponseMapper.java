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

import org.apache.logging.log4j.util.Strings;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.oscm.identity.error.ResourceNotFoundException;
import org.oscm.identity.model.json.*;

import java.util.HashSet;
import java.util.Set;

public class DefaultResponseMapper implements ResponseMapper {

  @Override
  public UserInfoDTO getUserInfo(JSONObject json) throws JSONException {

    String mail = json.getString("mail");
    if (Strings.isBlank(mail) || json.isNull("mail")) {
      JSONArray otherMails = json.getJSONArray("otherMails");
      mail = getFirst(otherMails);
    }
    JSONArray phones = json.getJSONArray("businessPhones");

    return UserInfoDTO.of()
        .userId(json.getString("userPrincipalName"))
        .firstName(json.getString("givenName"))
        .lastName(json.getString("surname"))
        .email(mail)
        .phone(getFirst(phones))
        .country(json.getString("country"))
        .city(json.getString("city"))
        .address(json.getString("streetAddress"))
        .postalCode(json.getString("postalCode"))
        .build();
  }

  @Override
  public UserGroupDTO getUserGroup(JSONObject json) throws JSONException {

    return UserGroupDTO.of()
        .id(json.getString("id"))
        .description(json.getString("description"))
        .name(json.getString("displayName"))
        .build();
  }

  @Override
  public Set<UserGroupDTO> getGroupsUserBelongsTo(JSONObject json) throws JSONException {

    JSONArray jsonArray = json.getJSONArray("value");
    Set<UserGroupDTO> userGroups = new HashSet<>();

    for (int i = 0; i < jsonArray.length(); i++) {
      JSONObject jsonObject = jsonArray.getJSONObject(i);
      String dataType = jsonObject.getString("@odata.type");
      if ("#microsoft.graph.group".equals(dataType)) {
        userGroups.add(getUserGroup(jsonObject));
      }
    }
    return userGroups;
  }

  @Override
  public Set<UserInfoDTO> getGroupMembers(JSONObject json) throws JSONException {

    JSONArray jsonArray = json.getJSONArray("value");
    Set<UserInfoDTO> users = new HashSet<>();

    for (int i = 0; i < jsonArray.length(); i++) {
      JSONObject jsonObject = jsonArray.getJSONObject(i);
      String dataType = jsonObject.getString("@odata.type");
      if ("#microsoft.graph.user".equals(dataType)) {
        users.add(getUserInfo(jsonObject));
      }
    }
    return users;
  }

  @Override
  public Set<UserGroupDTO> getGroups(JSONObject json) throws JSONException {
    return convertJsonArrayToGroupSet(json);
  }

  @Override
  public UserGroupDTO getGroup(JSONObject json, String requestedGroupName, boolean isLoggable)
      throws ResourceNotFoundException, JSONException {
    return convertJsonArrayToGroupSet(json).stream()
        .filter(u -> u.getName().equals(requestedGroupName))
        .findAny()
        .orElseThrow(
            () ->
                new ResourceNotFoundException(
                    "Group of requested name has not been found", isLoggable));
  }

  @Override
  public AccessTokenDTO getAccessToken(JSONObject json) throws JSONException {
    return AccessTokenDTO.of().accessToken(json.getString("access_token")).build();
  }

  @Override
  public RefreshTokenDTO getRefreshToken(JSONObject json) throws JSONException {
    return RefreshTokenDTO.of()
        .refreshToken(json.getString("refresh_token"))
        .accessToken(json.getString("access_token"))
        .build();
  }

  @Override
  public IdTokenDTO getIdToken(JSONObject json) throws JSONException {
    return IdTokenDTO.of().idToken(json.getString("id_token")).build();
  }

  private Set<UserGroupDTO> convertJsonArrayToGroupSet(JSONObject json) throws JSONException {
    JSONArray jsonArray = json.getJSONArray("value");
    Set<UserGroupDTO> userGroups = new HashSet<>();

    for (int i = 0; i < jsonArray.length(); i++) {
      JSONObject jsonObject = jsonArray.getJSONObject(i);
      userGroups.add(getUserGroup(jsonObject));
    }
    return userGroups;
  }

  /**
   * Retrieves first element out of the json array
   *
   * @param array json array
   * @return first string element if array is not empty, null string otherwise
   */
  private String getFirst(JSONArray array) {
    if (array.length() == 0) {
      return "null";
    }
    return array.optString(array.length() - 1);
  }
}

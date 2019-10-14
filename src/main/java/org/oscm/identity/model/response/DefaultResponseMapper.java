/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2019
 *
 *  Creation Date: Jul 26, 2019
 *
 *******************************************************************************/

package org.oscm.identity.model.response;

import com.jayway.jsonpath.JsonPathException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.oscm.identity.model.json.*;

import java.util.HashSet;
import java.util.Set;

public class DefaultResponseMapper implements ResponseMapper {

  @Override
  public UserInfoDTO getUserInfo(JSONObject json) throws JSONException {

    return UserInfoDTO.of()
        .userId(json.getString("userPrincipalName"))
        .firstName(json.getString("givenName"))
        .lastName(json.getString("surname"))
        .email(json.getString("mail"))
        .phone(json.get("businessPhones").toString())
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
  public UserGroupDTO getGroup(JSONObject json, String requestedGroupId) throws JSONException {
    return convertJsonArrayToGroupSet(json).stream().filter(u -> u.getId().equals(requestedGroupId)).findAny().orElseThrow(() -> new JsonPathException("Group of requested ID has not been found"));
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

  private Set<UserGroupDTO> convertJsonArrayToGroupSet(JSONObject json)
          throws JSONException {
    JSONArray jsonArray = json.getJSONArray("value");
    Set<UserGroupDTO> userGroups = new HashSet<>();

    for (int i = 0; i < jsonArray.length(); i++) {
      JSONObject jsonObject = jsonArray.getJSONObject(i);
      userGroups.add(getUserGroup(jsonObject));
    }
    return userGroups;
  }
}

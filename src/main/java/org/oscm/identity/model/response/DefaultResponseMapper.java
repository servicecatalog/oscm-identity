/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2019
 *
 *  Creation Date: Jul 26, 2019
 *
 *******************************************************************************/

package org.oscm.identity.model.response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.oscm.identity.model.json.UserGroup;
import org.oscm.identity.model.json.UserInfo;

import java.util.HashSet;
import java.util.Set;

public class DefaultResponseMapper implements ResponseMapper {

  @Override
  public UserInfo getUserInfo(JSONObject json) throws JSONException {

    return UserInfo.of()
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
  public UserGroup getUserGroup(JSONObject json) throws JSONException {

    return UserGroup.of()
        .id(json.getString("id"))
        .description(json.getString("description"))
        .name(json.getString("displayName"))
        .build();
  }

  @Override
  public Set<UserGroup> getGroupsUserBelongsTo(JSONObject json) throws JSONException {

    JSONArray jsonArray = json.getJSONArray("value");
    Set<UserGroup> userGroups = new HashSet<>();

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
  public Set<UserInfo> getGroupMembers(JSONObject json) throws JSONException {

    JSONArray jsonArray = json.getJSONArray("value");
    Set<UserInfo> users = new HashSet<>();

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
  public Set<UserGroup> getGroups(JSONObject json) throws JSONException {

    JSONArray jsonArray = json.getJSONArray("value");
    Set<UserGroup> userGroups = new HashSet<>();

    for (int i = 0; i < jsonArray.length(); i++) {
      JSONObject jsonObject = jsonArray.getJSONObject(i);
      userGroups.add(getUserGroup(jsonObject));
    }
    return userGroups;
  }
}

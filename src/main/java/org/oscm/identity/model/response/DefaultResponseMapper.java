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

public class DefaultResponseMapper implements ResponseMapper {

  @Override
  public UserInfo getUserInfo(JSONObject json) throws JSONException {

    return UserInfo.of()
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
        .description(json.getString("description"))
        .name(json.getString("displayName"))
        .build();
  }
}

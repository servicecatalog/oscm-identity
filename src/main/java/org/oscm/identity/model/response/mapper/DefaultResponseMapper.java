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

public class DefaultResponseMapper implements ResponseMapper {

  @Override
  public UserInfoResponse getUserResponse(JSONObject json) throws JSONException {

    return UserInfoResponse.of()
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
}

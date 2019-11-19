/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2019
 *
 *  Creation Date: Aug 7, 2019
 *
 *******************************************************************************/

package org.oscm.identity.oidc.request;

import lombok.Getter;
import lombok.Setter;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

/** Object representing the http request to identity provider for creating user group */
public class DefaultCreateGroupRequest extends GroupRequest {

  @Getter @Setter private String displayName;
  @Getter @Setter private String description;

  private RestTemplate restTemplate;

  public DefaultCreateGroupRequest(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Override
  public ResponseEntity execute() throws JSONException {

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(getToken());
    headers.setContentType(MediaType.APPLICATION_JSON);

    JSONObject json = new JSONObject();
    json.put("displayName", displayName);
    json.put("description", description);
    json.put("mailEnabled", false);
    json.put("securityEnabled", true);
    json.put("mailNickname", UUID.randomUUID());

    HttpEntity entity = new HttpEntity(json.toString(), headers);
    String url = getBaseUrl();

    ResponseEntity<String> responseEntity =
        restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

    return responseEntity;
  }
}

/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2019
 *
 *  Creation Date: Aug 13, 2019
 *
 *******************************************************************************/

package org.oscm.identity.oidc.request;

import lombok.Getter;
import lombok.Setter;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

/** Object representing the http request to identity provider for adding given user to group */
public class DefaultAddGroupMemberRequest extends GroupRequest {

  @Getter @Setter private String userId;
  @Getter @Setter private String groupId;

  private RestTemplate restTemplate;

  public DefaultAddGroupMemberRequest(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Override
  public ResponseEntity execute() throws JSONException {

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(getToken());
    headers.setContentType(MediaType.APPLICATION_JSON);

    JSONObject json = new JSONObject();
    json.put("@odata.id", "https://graph.microsoft.com/v1.0/users/" + getUserId());

    HttpEntity entity = new HttpEntity(json.toString(), headers);
    String url = getBaseUrl() + "/" + getGroupId() + "/members/$ref";

    ResponseEntity<String> responseEntity =
        restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

    return responseEntity;
  }
}

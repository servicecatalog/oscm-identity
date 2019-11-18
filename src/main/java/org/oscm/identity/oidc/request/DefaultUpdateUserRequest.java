/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2019
 *
 *  Creation Date: Nov 13, 2019
 *
 *******************************************************************************/
package org.oscm.identity.oidc.request;

import lombok.Getter;
import lombok.Setter;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class DefaultUpdateUserRequest extends UserRequest {

  @Getter @Setter private String email;
  @Getter @Setter private String userId;

  private RestTemplate restTemplate;

  public DefaultUpdateUserRequest(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Override
  public ResponseEntity execute() throws JSONException {

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(getToken());
    headers.setContentType(MediaType.APPLICATION_JSON);

    JSONArray otherMails = new JSONArray();
    otherMails.put(email);
    JSONObject json = new JSONObject();
    json.put("otherMails", otherMails);

    HttpEntity entity = new HttpEntity(json.toString(), headers);
    String url = getBaseUrl() + "/" + getUserId();

    // Workaround for PATCH method
    HttpClient httpClient = HttpClientBuilder.create().build();
    restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));

    ResponseEntity<String> responseEntity =
        restTemplate.exchange(url, HttpMethod.PATCH, entity, String.class);

    return responseEntity;
  }
}

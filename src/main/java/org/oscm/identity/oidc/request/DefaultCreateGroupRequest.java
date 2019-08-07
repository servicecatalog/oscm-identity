package org.oscm.identity.oidc.request;

import lombok.Getter;
import lombok.Setter;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class DefaultCreateGroupRequest extends GroupRequest {

  @Getter @Setter private String displayName;
  @Getter @Setter private boolean mailEnabled;
  @Getter @Setter private String mailNickname;
  @Getter @Setter private boolean securityEnabled;
  private RestTemplate restTemplate;

  public DefaultCreateGroupRequest(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Override
  public ResponseEntity execute() throws JSONException {

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(getToken());

    JSONObject json = new JSONObject();
    json.put("displayName", displayName);

    HttpEntity entity = new HttpEntity(json.toString(), headers);

    String url = getBaseUrl();

    ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, entity, String.class);
    return responseEntity;
  }
}

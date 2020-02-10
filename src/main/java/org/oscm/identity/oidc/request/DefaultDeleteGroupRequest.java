/**
 * *****************************************************************************
 *
 * <p>Copyright FUJITSU LIMITED 2020
 *
 * <p>*****************************************************************************
 */
package org.oscm.identity.oidc.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class DefaultDeleteGroupRequest extends GroupRequest {

  @Getter @Setter private String groupId;

  private RestTemplate restTemplate;

  public DefaultDeleteGroupRequest(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Override
  public ResponseEntity execute() {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(getToken());
    HttpEntity entity = new HttpEntity(headers);

    String url = getBaseUrl() + "/" + groupId;

    ResponseEntity<String> responseEntity =
        restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);

    return responseEntity;
  }
}

/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2019
 *
 *  Creation Date: Jul 26, 2019
 *
 *******************************************************************************/
package org.oscm.identity.oidc.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class DefaultGetUserRequest extends UserRequest {

  @Getter @Setter private String userId;
  @Getter @Setter private String select;
  private RestTemplate restTemplate;

  public DefaultGetUserRequest(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Override
  public ResponseEntity execute() {

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(getToken());

    HttpEntity entity = new HttpEntity(headers);

    String url = getBaseUrl() + "/" + getUserId() + "?$select=" + getSelect();

    ResponseEntity<String> responseEntity =
        restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

    return responseEntity;
  }
}

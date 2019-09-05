/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2019
 *
 *  Creation Date: Sep 4, 2019
 *
 *******************************************************************************/

package org.oscm.identity.oidc.request;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class DefaultGetGroupsRequest extends GroupRequest {

  private RestTemplate restTemplate;

  public DefaultGetGroupsRequest(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Override
  public ResponseEntity execute() {

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(getToken());
    HttpEntity entity = new HttpEntity(headers);

    ResponseEntity<String> responseEntity =
        restTemplate.exchange(getBaseUrl(), HttpMethod.GET, entity, String.class);

    return responseEntity;
  }
}

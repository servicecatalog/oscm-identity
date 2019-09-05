/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2019
 *
 *  Creation Date: Sep 3, 2019
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

/**
 * Object representing http request to default identity provider for retrieving members for given
 * group
 */
public class DefaultGetGroupMembersRequest extends GroupRequest {

  @Getter @Setter private String groupId;
  @Getter @Setter private String select;

  private RestTemplate restTemplate;

  public DefaultGetGroupMembersRequest(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Override
  public ResponseEntity execute() {

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(getToken());
    HttpEntity entity = new HttpEntity(headers);

    String url = getBaseUrl() + "/" + getGroupId() + "/members?$select=" + getSelect();

    ResponseEntity<String> responseEntity =
        restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

    return responseEntity;
  }
}

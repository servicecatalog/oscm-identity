package org.oscm.identity.oidc.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * Object representing http request to default identity provider for removing a member from a group
 */
public class DefaultRemoveGroupMemberRequest extends GroupRequest {
  @Getter @Setter private String groupId;
  @Getter @Setter private String memberId;

  private RestTemplate restTemplate;

  public DefaultRemoveGroupMemberRequest(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Override
  public ResponseEntity execute() {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(getToken());
    HttpEntity entity = new HttpEntity(headers);

    final String url = getBaseUrl() + "/" + getGroupId() + "/members/" + getMemberId() + "/$ref";

    ResponseEntity<String> responseEntity =
        restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);

    return responseEntity;
  }
}

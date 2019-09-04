/**
 * *****************************************************************************
 *
 * <p>Copyright FUJITSU LIMITED 2019
 *
 * <p>Creation Date: Sep 04, 2019
 *
 * <p>*****************************************************************************
 */
package org.oscm.identity.oidc.request;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class RefreshTokenRequest extends RefreshRequest {

  private RestTemplate restTemplate;

  public RefreshTokenRequest(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Override
  public ResponseEntity<String> execute() {

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add("client_id", getClientId());
    map.add("scope", getScope());
    map.add("refresh_token", getRefreshToken());
    map.add("grant_type", getGrantType());
    map.add("client_secret", getClientSecret());

    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

    ResponseEntity<String> response =
        restTemplate.postForEntity(getBaseUrl(), request, String.class);

    return response;
  }
}

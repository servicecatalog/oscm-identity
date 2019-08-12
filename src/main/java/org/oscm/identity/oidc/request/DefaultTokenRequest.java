/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2019
 *
 *  Creation Date: Jul 25, 2019
 *
 *******************************************************************************/

package org.oscm.identity.oidc.request;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/** Implementation of access token request related to default identity provider */
public class DefaultTokenRequest extends TokenRequest {

  private RestTemplate restTemplate;

  public DefaultTokenRequest(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Override
  public ResponseEntity<String> execute() {

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add("client_id", getClientId());
    map.add("client_secret", getClientSecret());
    map.add("grant_type", getGrantType());
    map.add("redirect_uri", getRedirectUrl());
    map.add("code", getCode());

    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

    ResponseEntity<String> response =
        restTemplate.postForEntity(getBaseUrl(), request, String.class);

    return response;
  }
}

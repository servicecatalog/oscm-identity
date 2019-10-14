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
import org.springframework.util.StringUtils;
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
    addBodyParam(map, "client_id", getClientId());
    addBodyParam(map, "client_secret", getClientSecret());
    addBodyParam(map, "grant_type", getGrantType());
    addBodyParam(map, "redirect_uri", getRedirectUrl());
    addBodyParam(map, "code", getCode());
    addBodyParam(map, "scope", getScope());
    addBodyParam(map, "refresh_token", getRefreshToken());
    addBodyParam(map, "username", getUsername());
    addBodyParam(map, "password", getPassword());

    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

    ResponseEntity<String> response =
        restTemplate.postForEntity(getBaseUrl(), request, String.class);

    return response;
  }

  private void addBodyParam(MultiValueMap<String, String> map, String key, String value) {

    if (!StringUtils.isEmpty(value)) {
      map.add(key, value);
    }
  }
}

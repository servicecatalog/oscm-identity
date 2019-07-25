package org.oscm.identity.oidc.request;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.InetSocketAddress;
import java.net.Proxy;

public class DefaultTokenRequest extends TokenRequest {

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

    SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
    Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("abgproxya.abg.fsc.net", 82));
    factory.setProxy(proxy);

    RestTemplate restTemplate = new RestTemplate(factory);
    ResponseEntity<String> response =
        restTemplate.postForEntity(getBaseUrl(), request, String.class);

    return response;
  }
}

package org.oscm.identity.oidc.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class DefaultTokenRequestTest {

  @Mock private RestTemplate restTemplate;

  @Test
  public void testExecute_containsValidElements_requestIsExecuted() throws Exception {

    // given
    DefaultTokenRequest tokenRequest = new DefaultTokenRequest(restTemplate);
    tokenRequest.setBaseUrl("baseUrl");
    tokenRequest.setClientId("clientId");
    tokenRequest.setClientSecret("clientSecret");

    // when
    tokenRequest.execute();

    // then
    String expectedUrl = tokenRequest.getBaseUrl();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add("client_id", tokenRequest.getClientId());
    map.add("client_secret", tokenRequest.getClientSecret());

    HttpEntity<MultiValueMap<String, String>> expectedRequest = new HttpEntity<>(map, headers);

    verify(restTemplate, times(1))
        .postForEntity(eq(expectedUrl), eq(expectedRequest), eq(String.class));
  }
}

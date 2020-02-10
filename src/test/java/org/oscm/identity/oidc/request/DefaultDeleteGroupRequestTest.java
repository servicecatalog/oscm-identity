package org.oscm.identity.oidc.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DefaultDeleteGroupRequestTest {

  @Mock private RestTemplate restTemplate;

  @Test
  public void testExecute_containsValidElements_requestIsExecuted() {
    final String baseUrl = "baseUrl";
    final String groupId = "groupId";
    final DefaultDeleteGroupRequest request = new DefaultDeleteGroupRequest(restTemplate);
    request.setBaseUrl(baseUrl);
    request.setGroupId(groupId);

    request.execute();

    assertEquals(baseUrl, request.getBaseUrl());
    assertEquals(groupId, request.getGroupId());

    String expectedUrl = request.getBaseUrl() + "/" + request.getGroupId();

    verify(restTemplate, times(1))
        .exchange(eq(expectedUrl), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(String.class));
  }
}

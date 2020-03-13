package org.oscm.identity.oidc.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultRemoveGroupMemberRequestTest {

  @Mock private RestTemplate restTemplate;

  @Test
  public void test() {

    final String baseUrl = "baseUrl";
    final String groupId = "groupId";
    final String memberId = "memberId";
    final DefaultRemoveGroupMemberRequest request =
        new DefaultRemoveGroupMemberRequest(restTemplate);
    request.setBaseUrl(baseUrl);
    request.setGroupId(groupId);
    request.setMemberId(memberId);
    request.execute();

    assertEquals(baseUrl, request.getBaseUrl());
    assertEquals(groupId, request.getGroupId());

    String expectedUrl = request.getBaseUrl() + "/" + groupId + "/members/" + memberId + "/$ref";

    verify(restTemplate, times(1))
        .exchange(eq(expectedUrl), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(String.class));
  }
}

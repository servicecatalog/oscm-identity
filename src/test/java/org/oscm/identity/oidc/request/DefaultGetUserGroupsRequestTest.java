/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2019
 *
 *  Creation Date: Sep 3, 2019
 *
 *******************************************************************************/

package org.oscm.identity.oidc.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class DefaultGetUserGroupsRequestTest {

  @Mock private RestTemplate restTemplate;

  @Test
  public void testExecute_containsValidElements_requestIsExecuted() throws Exception {

    // given
    DefaultGetUserGroupsRequest request = new DefaultGetUserGroupsRequest(restTemplate);
    request.setBaseUrl("baseUrl");
    request.setUserId("userId");

    // when
    request.execute();

    // then
    String expectedUrl = request.getBaseUrl() + "/" + request.getUserId() + "/memberOf";

    verify(restTemplate, times(1))
        .exchange(eq(expectedUrl), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class));
  }
}

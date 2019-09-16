/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2019
 *
 *  Creation Date: Sep 16, 2019
 *
 *******************************************************************************/

package org.oscm.identity.controller;

import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.oscm.identity.model.json.AccessToken;
import org.oscm.identity.oidc.request.RequestHandler;
import org.oscm.identity.oidc.request.RequestManager;
import org.oscm.identity.oidc.request.TokenRequest;
import org.oscm.identity.oidc.tenant.TenantConfiguration;
import org.oscm.identity.service.TenantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TokenControllerTest {

  @Mock private TenantService tenantService;
  @Mock private RequestHandler requestHandler;
  @Mock private RequestManager requestManager;
  @Mock private TokenRequest tokenRequest;

  @InjectMocks private TokenController controller;

  @Test
  @SneakyThrows
  public void testGetAccessToken_validRequestSend_properResponseIsReturned() {

    // given
    String tenantId = "default";

    String accessToken = "someAccessToken";
    AccessToken tokenResponse = AccessToken.of().accessToken(accessToken).build();

    TenantConfiguration configuration = new TenantConfiguration();
    configuration.setProvider("default");

    ResponseEntity<String> retrievedToken =
        ResponseEntity.ok("{'access_token':'" + accessToken + "'}");

    when(tenantService.loadTenant(any())).thenReturn(configuration);
    when(requestHandler.getRequestManager(anyString())).thenReturn(requestManager);
    when(requestManager.initTokenRequest()).thenReturn(tokenRequest);
    when(tokenRequest.execute()).thenReturn(retrievedToken);

    // when
    ResponseEntity response = controller.getAccessToken(tenantId);

    // then
    Assertions.assertThat(response)
        .extracting(ResponseEntity::getStatusCode)
        .isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response).extracting(ResponseEntity::getBody).isEqualTo(tokenResponse);
  }
}

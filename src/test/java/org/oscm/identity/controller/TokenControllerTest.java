/**
 * *****************************************************************************
 *
 * <p>Copyright FUJITSU LIMITED 2019
 *
 * <p>Creation Date: Sep 16, 2019
 *
 * <p>*****************************************************************************
 */
package org.oscm.identity.controller;

import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.oscm.identity.model.json.AccessToken;
import org.oscm.identity.model.json.RefreshToken;
import org.oscm.identity.model.request.TokenDetails;
import org.oscm.identity.oidc.request.RequestHandler;
import org.oscm.identity.oidc.request.RequestManager;
import org.oscm.identity.oidc.request.TokenRequest;
import org.oscm.identity.oidc.tenant.TenantConfiguration;
import org.oscm.identity.oidc.validation.IdTokenValidator;
import org.oscm.identity.oidc.validation.TokenValidationFlow;
import org.oscm.identity.service.TenantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TokenControllerTest {

  @Mock private TenantService tenantService;
  @Mock private RequestHandler requestHandler;
  @Mock private RequestManager requestManager;
  @Mock private TokenRequest tokenRequest;
  @Mock private TokenValidationFlow flow;
  @Mock private IdTokenValidator tokenValidator;

  @InjectMocks private TokenController controller;

  @Test
  @SneakyThrows
  public void testGetAccessToken_validRequestSend_properResponseIsReturned() {

    // given
    String accessToken = "someAccessToken";
    AccessToken tokenResponse = AccessToken.of().accessToken(accessToken).build();

    TenantConfiguration configuration = new TenantConfiguration();
    configuration.setProvider("default");
    configuration.setUriAppId("defaultUriAppId");

    ResponseEntity<String> retrievedToken =
        ResponseEntity.ok("{'access_token':'" + accessToken + "'}");

    when(tenantService.loadTenant(any())).thenReturn(configuration);
    when(requestHandler.getRequestManager(anyString())).thenReturn(requestManager);
    when(requestManager.initTokenRequest()).thenReturn(tokenRequest);
    when(tokenRequest.execute()).thenReturn(retrievedToken);

    // when
    ResponseEntity response = controller.getAccessToken("default");

    // then
    Assertions.assertThat(response)
        .extracting(ResponseEntity::getStatusCode)
        .isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response).extracting(ResponseEntity::getBody).isEqualTo(tokenResponse);
  }

  @Test
  @SneakyThrows
  public void testRefreshAccessToken_validRequestSend_properResponseIsReturned() {

    // given
    TenantConfiguration configuration = new TenantConfiguration();
    configuration.setProvider("default");

    String accessToken = "accessToken";
    String refreshToken = "refreshToken";

    RefreshToken refreshRequest = RefreshToken.of().refreshToken(refreshToken).build();
    ResponseEntity<String> entity =
        ResponseEntity.ok(
            ("{'access_token':'" + accessToken + "', 'refresh_token':'" + refreshToken + "'}"));

    when(tenantService.loadTenant(any())).thenReturn(configuration);
    when(requestHandler.getRequestManager(anyString())).thenReturn(requestManager);
    when(requestManager.initTokenRequest()).thenReturn(tokenRequest);
    when(tokenRequest.execute()).thenReturn(entity);

    // when
    ResponseEntity response = controller.refreshAccessToken("default", refreshRequest);

    // then
    RefreshToken expectedResponse =
        RefreshToken.of().accessToken(accessToken).refreshToken(refreshToken).build();

    Assertions.assertThat(response)
        .extracting(ResponseEntity::getStatusCode)
        .isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response).extracting(ResponseEntity::getBody).isEqualTo(expectedResponse);
  }

  @Test
  @SneakyThrows
  public void shouldCallTokenValidator() {
    String tenantId = "default";
    TokenDetails tokenDetails = TokenDetails.of().build();
    doReturn(flow).when(flow).forTenantOf(anyString());
    doReturn(tokenValidator).when(flow).withTokenFrom(any());

    assertThatCode(() -> controller.verifyToken(tenantId, tokenDetails)).doesNotThrowAnyException();
  }
}

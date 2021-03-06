/**
 * *****************************************************************************
 *
 * <p>Copyright FUJITSU LIMITED 2019
 *
 * <p>Creation Date: Jul 22, 2019
 *
 * <p>*****************************************************************************
 */
package org.oscm.identity.controller;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.oscm.identity.error.IdentityProviderException;
import org.oscm.identity.oidc.request.DefaultRequestManager;
import org.oscm.identity.oidc.request.RequestHandler;
import org.oscm.identity.oidc.request.RequestManager;
import org.oscm.identity.oidc.request.TokenRequest;
import org.oscm.identity.oidc.tenant.TenantConfiguration;
import org.oscm.identity.service.TenantService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.ValidationException;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MainControllerTest {

  @Mock private TenantService tenantService;
  @Mock private HttpServletResponse response;
  @Mock private RequestHandler requestHandler;
  @Mock private RequestManager requestManager;
  @Mock private TokenRequest tokenRequest;

  @InjectMocks private MainController controller;

  @Test
  @SneakyThrows
  public void shouldRedirect_whenGetToLogin_givenNoErrors() {
    TenantConfiguration configuration = new TenantConfiguration();
    configuration.setProvider("default");
    configuration.setAuthUrl("authUrl");
    configuration.setClientId("clientId");
    configuration.setRedirectUrl("redirectUrl");

    DefaultRequestManager requestManager = new DefaultRequestManager(new RestTemplate());

    when(requestHandler.getRequestManager(anyString())).thenReturn(requestManager);
    when(tenantService.loadTenant(any())).thenReturn(configuration);
    doNothing().when(response).sendRedirect(any());

    controller.loginPage("default", "state", response);

    verify(tenantService).loadTenant(any());
    verify(response, times(1)).sendRedirect(anyString());
  }

  @Test
  @SneakyThrows
  public void shouldReturnError_whenGetToLogin_givenAnIOError() {
    TenantConfiguration configuration = new TenantConfiguration();
    configuration.setProvider("default");
    configuration.setAuthUrl("authUrl");
    configuration.setClientId("clientId");
    configuration.setRedirectUrl("redirectUrl");
    RequestManager requestManager = new DefaultRequestManager(new RestTemplate());

    when(tenantService.loadTenant(any())).thenReturn(configuration);
    when(requestHandler.getRequestManager(anyString())).thenReturn(requestManager);
    doThrow(new IOException()).when(response).sendRedirect(anyString());

    assertThatExceptionOfType(IdentityProviderException.class)
        .isThrownBy(() -> controller.loginPage("default", "state", response));
    verify(tenantService).loadTenant(any());
    verify(response, times(1)).sendRedirect(anyString());
  }

  //FIXME: Fix upon refresh token functionality refactor
  @Test
  @SneakyThrows
  @Disabled("This test should be reimplemented as a part of refresh token refactoring")
  public void shouldRedirectToHomeWithToken_whenPostToIdToken_givenNoErrors() {
    TenantConfiguration configuration = new TenantConfiguration();
    configuration.setProvider("default");
    ResponseEntity<String> entity =
        ResponseEntity.ok().body("{'access_token':'accessToken', 'refresh_token':'refreshToken'}");

    when(tenantService.loadTenant(any())).thenReturn(configuration);
    when(requestHandler.getRequestManager(anyString())).thenReturn(requestManager);
    when(requestManager.initTokenRequest()).thenReturn(tokenRequest);
    when(requestHandler.getStateWithoutTenant(anyString())).thenReturn("state");
    when(tokenRequest.execute()).thenReturn(entity);
    doNothing().when(response).sendRedirect(any());

    assertThatCode(() -> controller.callback("idToken", "code", "state", null, null, response))
        .doesNotThrowAnyException();

    verify(response, times(1)).sendRedirect(any());
  }

  @Test
  @SneakyThrows
  public void shouldReturnError_whenPostToIdToken_givenAPassedInError() {
    assertThatExceptionOfType(IdentityProviderException.class)
        .isThrownBy(
            () -> controller.callback("idToken", "code", "state", "someError", null, response));
    verify(response, never()).sendRedirect(any());
  }

  //FIXME: Fix upon refresh token functionality refactor
  @Test
  @Disabled("This test should be reimplemented as a part of refresh token refactoring")
  public void shouldReturnError_whenPostToIdToken_givenValidationError() throws Exception {

    TenantConfiguration configuration = new TenantConfiguration();
    configuration.setProvider("default");

    ResponseEntity<String> entity =
        ResponseEntity.ok().body("{'access_token':'accessToken', 'refresh_token':'refreshToken'}");

    when(tenantService.loadTenant(any())).thenReturn(configuration);
    when(requestHandler.getRequestManager(anyString())).thenReturn(requestManager);
    when(requestManager.initTokenRequest()).thenReturn(tokenRequest);
    when(tokenRequest.execute()).thenReturn(entity);

    assertThatExceptionOfType(ValidationException.class)
        .isThrownBy(() -> controller.callback("idToken", "code", "state", null, null, response));

    verifyZeroInteractions(response);
  }


  @Test
  @SneakyThrows
  public void shouldSucceed_whenGetToLogout_givenNoErrors() {
    TenantConfiguration configuration = new TenantConfiguration();
    configuration.setProvider("default");
    configuration.setLogoutUrl("logoutUrl");

    DefaultRequestManager requestManager = new DefaultRequestManager(new RestTemplate());

    when(requestHandler.getRequestManager(anyString())).thenReturn(requestManager);
    when(tenantService.loadTenant(any())).thenReturn(configuration);
    doNothing().when(response).sendRedirect(any());

    assertThatCode(() -> controller.logoutPage("tenantId", "state", response))
        .doesNotThrowAnyException();
  }

  @Test
  @SneakyThrows
  public void shouldReturnError_whenGetToLogout_givenIOException() {
    TenantConfiguration configuration = new TenantConfiguration();
    configuration.setProvider("default");
    configuration.setLogoutUrl("logoutUrl");

    DefaultRequestManager requestManager = new DefaultRequestManager(new RestTemplate());

    when(requestHandler.getRequestManager(anyString())).thenReturn(requestManager);
    when(tenantService.loadTenant(any())).thenReturn(configuration);
    doThrow(new IOException()).when(response).sendRedirect(any());

    assertThatExceptionOfType(IdentityProviderException.class)
        .isThrownBy(() -> controller.logoutPage("tenantId", "state", response));
  }
}

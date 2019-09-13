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
import org.oscm.identity.model.request.TokenValidationRequest;
import org.oscm.identity.oidc.request.*;
import org.oscm.identity.oidc.tenant.TenantConfiguration;
import org.oscm.identity.oidc.validation.AuthTokenValidator;
import org.oscm.identity.oidc.validation.TokenValidationResult;
import org.oscm.identity.service.TenantService;
import org.springframework.http.HttpStatus;
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
  @Mock private AuthTokenValidator tokenValidator;
  @Mock private HttpServletResponse response;
  @Mock private RequestHandler requestHandler;
  @Mock private RequestManager requestManager;
  @Mock private TokenRequest tokenRequest;
  @Mock private RefreshRequest refreshRequest;

  @InjectMocks private MainController controller;

  @Test
  public void shouldSucceed_whenGetToDefault() {
    String result = controller.homePage();
    assertThat(result).isNotBlank();
  }

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
    TokenValidationResult validationResult = TokenValidationResult.of().isValid(true).build();
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

    verify(tokenValidator, times(1)).validate(any());
    verify(response, times(1)).sendRedirect(any());
  }

  @Test
  @SneakyThrows
  public void shouldReturnError_whenPostToIdToken_givenAPassedInError() {
    assertThatExceptionOfType(IdentityProviderException.class)
        .isThrownBy(
            () -> controller.callback("idToken", "code", "state", "someError", null, response));
    verifyZeroInteractions(tokenValidator);
    verify(response, never()).sendRedirect(any());
  }

  //FIXME: Fix upon refresh token functionality refactor
  @Test
  @Disabled("This test should be reimplemented as a part of refresh token refactoring")
  public void shouldReturnError_whenPostToIdToken_givenValidationError() throws Exception {
    TokenValidationResult validationResult =
        TokenValidationResult.of().isValid(false).validationFailureReason("Reason").build();

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

    verify(tokenValidator, times(1)).validate(any());
    verifyZeroInteractions(response);
  }

  @Test
  @SneakyThrows
  public void shouldSucceed_whenPostToVerifyToken_givenValidToken() {
    ResponseEntity response = controller.verifyToken(any());

    assertThat(response).extracting(ResponseEntity::getStatusCode).isEqualTo(HttpStatus.OK);
  }

  @Test
  @SneakyThrows
  public void shouldReturnError_whenPostToVerifyToken_givenInvalidToken() {
    when(controller.verifyToken(any())).thenCallRealMethod();

    assertThatExceptionOfType(ValidationException.class)
        .isThrownBy(
            () ->
                controller.verifyToken(
                    TokenValidationRequest.of().idToken("thisiscetainlyincvalidtoken").build()));
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

  //FIXME: Fix upon refresh token functionality refactor
  @Test
  @SneakyThrows
  @Disabled("This test should be reimplemented as a part of refresh token refactoring")
  public void shouldRedirectToHomeWithToken_whenPostToRefreshToken_givenNoErrors() {
    TenantConfiguration configuration = new TenantConfiguration();
    configuration.setProvider("default");
    TokenValidationResult validationResult = TokenValidationResult.of().isValid(true).build();
    ResponseEntity<String> entity =
        ResponseEntity.ok()
            .body(
                "{'id_token':'idToken', 'access_token':'accessToken', 'refresh_token':'refreshToken'}");

    when(tenantService.loadTenant(any())).thenReturn(configuration);
    when(requestHandler.getRequestManager(anyString())).thenReturn(requestManager);
    when(requestManager.initRefreshRequest()).thenReturn(refreshRequest);
    when(refreshRequest.execute()).thenReturn(entity);
    doNothing().when(response).sendRedirect(any());
    assertThatCode(() -> controller.refresh(createRefreshBody(), response))
        .doesNotThrowAnyException();

    verify(tokenValidator, times(1)).validate(any());
    verify(response, times(1)).sendRedirect(any());
  }

  //FIXME: Fix upon refresh token functionality refactor
  @Test
  @Disabled("This test should be reimplemented as a part of refresh token refactoring")
  public void shouldReturnError_whenPostToRefreshToken_givenValidationError() throws Exception {
    TokenValidationResult validationResult =
        TokenValidationResult.of().isValid(false).validationFailureReason("Reason").build();

    TenantConfiguration configuration = new TenantConfiguration();
    configuration.setProvider("default");
    configuration.setTokenUrl("tokenUrl");
    configuration.setAuthUrlScope("scope");

    ResponseEntity<String> entity =
        ResponseEntity.ok()
            .body(
                "{'id_token':'idToken', 'access_token':'accessToken', 'refresh_token':'refreshToken'}");

    when(tenantService.loadTenant(any())).thenReturn(configuration);
    when(requestHandler.getRequestManager(anyString())).thenReturn(requestManager);
    when(requestManager.initRefreshRequest()).thenReturn(refreshRequest);
    when(refreshRequest.execute()).thenReturn(entity);

    assertThatExceptionOfType(ValidationException.class)
        .isThrownBy(() -> controller.refresh(createRefreshBody(), response));

    verify(tokenValidator, times(1)).validate(any());
    verifyZeroInteractions(response);
  }

  @SneakyThrows
  private RefreshBody createRefreshBody() {
    RefreshBody refreshBody = new RefreshBody();
    refreshBody.setState("null");
    refreshBody.setRefreshToken("ABC123");
    refreshBody.setGrantType("refresh_token");
    return refreshBody;
  }
}

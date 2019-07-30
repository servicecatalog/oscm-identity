/**
 * *****************************************************************************
 *
 * <p>Copyright FUJITSU LIMITED 2019
 *
 * <p>Creation Date: July 22, 2019
 *
 * <p>*****************************************************************************
 */
package org.oscm.identity.controller;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.oscm.identity.error.IdentityProviderException;
import org.oscm.identity.oidc.response.validation.AuthTokenValidator;
import org.oscm.identity.oidc.response.validation.TokenValidationResult;
import org.oscm.identity.oidc.tenant.TenantConfiguration;
import org.oscm.identity.service.TenantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
    configuration.setIdTokenRedirectUrl("redirectUrl");

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
    configuration.setIdTokenRedirectUrl("redirectUrl");

    when(tenantService.loadTenant(any())).thenReturn(configuration);
    doThrow(new IOException()).when(response).sendRedirect(anyString());

    assertThatExceptionOfType(IdentityProviderException.class)
        .isThrownBy(() -> controller.loginPage("default", "state", response));
    verify(tenantService).loadTenant(any());
    verify(response, times(1)).sendRedirect(anyString());
  }

  @Test
  @SneakyThrows
  public void shouldRedirectToHomeWithToken_whenPostToIdToken_givenNoErrors() {
    TokenValidationResult validationResult = TokenValidationResult.of().isValid(true).build();

    when(tokenValidator.validate(any())).thenReturn(validationResult);
    doNothing().when(response).sendRedirect(any());

    assertThatCode(() -> controller.idTokenCallback("idToken", "state", null, null, response))
        .doesNotThrowAnyException();

    verify(tokenValidator, times(1)).validate(any());
    verify(response, times(1)).sendRedirect(any());
  }

  @Test
  @SneakyThrows
  public void shouldReturnError_whenPostToIdToken_givenAPassedInError() {
    assertThatExceptionOfType(IdentityProviderException.class)
        .isThrownBy(
            () -> controller.idTokenCallback("idToken", "state", "someError", null, response));
    verifyZeroInteractions(tokenValidator);
    verify(response, never()).sendRedirect(any());
  }

  @Test
  public void shouldReturnError_whenPostToIdToken_givenValidationError() {
    TokenValidationResult validationResult =
        TokenValidationResult.of().isValid(false).validationFailureReason("Reason").build();
    when(tokenValidator.validate(any())).thenReturn(validationResult);

    assertThatExceptionOfType(ValidationException.class)
        .isThrownBy(() -> controller.idTokenCallback("idToken", "state", null, null, response));

    verify(tokenValidator, times(1)).validate(any());
    verifyZeroInteractions(response);
  }

  @Test
  public void shouldSucceed_whenPostToVerifyToken_givenValidToken() {
    TokenValidationResult validationResult = TokenValidationResult.of().isValid(true).build();
    when(tokenValidator.validate(any())).thenReturn(validationResult);

    ResponseEntity response = controller.verifyToken(any());

    assertThat(response).extracting(ResponseEntity::getStatusCode).isEqualTo(HttpStatus.OK);
  }

  @Test
  public void shouldReturnError_whenPostToVerifyToken_givenInvalidToken() {
    TokenValidationResult validationResult = TokenValidationResult.of().isValid(false).build();
    when(tokenValidator.validate(any())).thenReturn(validationResult);

    ResponseEntity response = controller.verifyToken(any());

    assertThat(response)
        .extracting(ResponseEntity::getStatusCode)
        .isEqualTo(HttpStatus.NOT_ACCEPTABLE);
  }

  @Test
  @SneakyThrows
  public void shouldSucceed_whenGetToLogout_givenNoErrors() {
    TenantConfiguration configuration = new TenantConfiguration();
    configuration.setProvider("default");
    configuration.setLogoutUrl("logoutUrl");

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

    when(tenantService.loadTenant(any())).thenReturn(configuration);
    doThrow(new IOException()).when(response).sendRedirect(any());

    assertThatExceptionOfType(IdentityProviderException.class)
        .isThrownBy(() -> controller.logoutPage("tenantId", "state", response));
  }
}

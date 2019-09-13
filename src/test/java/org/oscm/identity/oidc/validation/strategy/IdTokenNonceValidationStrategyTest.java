/**
 * *****************************************************************************
 *
 * <p>Copyright FUJITSU LIMITED 2019
 *
 * <p>Creation Date: July 29, 2019
 *
 * <p>*****************************************************************************
 */
package org.oscm.identity.oidc.validation.strategy;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.oscm.identity.model.request.TokenValidationRequest;
import org.oscm.identity.oidc.tenant.TenantConfiguration;
import org.oscm.identity.oidc.validation.AuthTokenValidator;
import org.oscm.identity.service.TenantService;

import javax.xml.bind.ValidationException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.assertj.core.api.AssertionsForClassTypes.fail;
import static org.assertj.core.api.Java6Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class IdTokenNonceValidationStrategyTest {

  @Mock
  public TenantService tenantService;
  @InjectMocks
  public IdTokenNonceValidationStrategy strategy;

  private TokenValidationRequest request;

  @Test
  @SneakyThrows
  public void shouldValidateRequest() {
    TenantConfiguration configuration = new TenantConfiguration();
    configuration.setNonce("testNonce");
    when(tenantService.loadTenant(any())).thenReturn(configuration);

    String token = JWT.create().withClaim("nonce", "testNonce").sign(Algorithm.none());
    request = TokenValidationRequest.of().idToken(token).build();
    request = AuthTokenValidator.decodeTokens(request);

    assertThatCode(() -> strategy.execute(request)).doesNotThrowAnyException();
  }

  @Test
  @SneakyThrows
  public void shouldNotValidateRequest() {
    TenantConfiguration configuration = new TenantConfiguration();
    configuration.setNonce("someOtherNonce");
    when(tenantService.loadTenant(any())).thenReturn(configuration);

    String token = JWT.create().withClaim("nonce", "testNonce").sign(Algorithm.none());
    request = TokenValidationRequest.of().idToken(token).build();
    request = AuthTokenValidator.decodeTokens(request);

    assertThatExceptionOfType(ValidationException.class)
        .isThrownBy(() -> strategy.execute(request));
  }
}

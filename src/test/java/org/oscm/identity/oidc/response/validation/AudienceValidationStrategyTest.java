/**
 * *****************************************************************************
 *
 * <p>Copyright FUJITSU LIMITED 2019
 *
 * <p>Creation Date: July 29, 2019
 *
 * <p>*****************************************************************************
 */
package org.oscm.identity.oidc.response.validation;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.oscm.identity.oidc.request.TokenValidationRequest;
import org.oscm.identity.oidc.tenant.TenantConfiguration;
import org.oscm.identity.service.TenantService;

import javax.xml.bind.ValidationException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.assertj.core.api.Java6Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AudienceValidationStrategyTest {

  @Mock private TenantService service;
  @InjectMocks private AudienceValidationStrategy strategy;

  private TokenValidationRequest request;

  @Test
  public void shouldValidateRequest() {
    TenantConfiguration configuration = new TenantConfiguration();
    configuration.setClientId("clientId");
    String token = JWT.create().withAudience("clientId").sign(Algorithm.none());
    when(service.loadTenant(any())).thenReturn(configuration);

    request = TokenValidationRequest.of().token(token).build();
    request.setDecodedToken(JWT.decode(request.getToken()));

    assertThatCode(() -> strategy.execute(request)).doesNotThrowAnyException();
  }

  @Test
  public void shouldNotValidateRequest() {
    TenantConfiguration configuration = new TenantConfiguration();
    configuration.setClientId("nonValidValue");
    String token = JWT.create().withAudience("clientId").sign(Algorithm.none());
    when(service.loadTenant(any())).thenReturn(configuration);

    request = TokenValidationRequest.of().token(token).build();
    request.setDecodedToken(JWT.decode(request.getToken()));

    assertThatExceptionOfType(ValidationException.class)
        .isThrownBy(() -> strategy.execute(request));
  }
}

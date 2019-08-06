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
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.oscm.identity.oidc.request.TokenValidationRequest;
import org.oscm.identity.oidc.tenant.TenantConfiguration;
import org.oscm.identity.service.TenantService;
import org.springframework.web.client.RestTemplate;

import javax.xml.bind.ValidationException;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.assertj.core.api.Java6Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ISSValidationStrategyTest {

  @Mock private RestTemplate restTemplate;
  @Mock private TenantService service;
  @InjectMocks private ISSValidationStrategy strategy;
  private TokenValidationRequest request;

  @Test
  public void shouldValidateRequest() {
    String issuerValue = "testIssuer";

    Map<String, String> jsonMap = new HashMap<>();
    jsonMap.put("issuer", issuerValue);

    TenantConfiguration configuration = new TenantConfiguration();
    configuration.setConfigurationUrl("oidConfigUrl");

    when(service.loadTenant(any())).thenReturn(configuration);
    when(restTemplate.getForObject(anyString(), any()))
        .thenReturn(new JSONObject(jsonMap).toString());

    request =
        TokenValidationRequest.of()
            .token(JWT.create().withClaim("iss", issuerValue).sign(Algorithm.none()))
            .build();
    request.setDecodedToken(JWT.decode(request.getToken()));

    assertThatCode(() -> strategy.execute(request)).doesNotThrowAnyException();
  }

  @Test
  public void shouldNotValidateRequest() {
    String issuerValue = "someinvalidvalue";

    Map<String, String> jsonMap = new HashMap<>();
    jsonMap.put("issuer", "validIssuer");

    TenantConfiguration configuration = new TenantConfiguration();
    configuration.setConfigurationUrl("http://url.com");

    when(service.loadTenant(any())).thenReturn(configuration);
    when(restTemplate.getForObject(anyString(), any()))
        .thenReturn(new JSONObject(jsonMap).toString());

    request =
        TokenValidationRequest.of()
            .token(JWT.create().withClaim("iss", issuerValue).sign(Algorithm.none()))
            .build();
    request.setDecodedToken(JWT.decode(request.getToken()));

    assertThatExceptionOfType(ValidationException.class)
        .isThrownBy(() -> strategy.execute(request));
  }
}

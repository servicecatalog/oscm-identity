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
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.SneakyThrows;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.oscm.identity.error.IdTokenValidationException;
import org.oscm.identity.oidc.tenant.TenantConfiguration;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class IdTokenISSValidationStrategyTest {

  @Mock private RestTemplate restTemplate;
  @InjectMocks private IdTokenISSValidationStrategy strategy;

  @Test
  @SneakyThrows
  public void shouldValidateRequest() {
    String issuerValue = "testIssuer";
    DecodedJWT token =
        JWT.decode(JWT.create().withClaim("iss", issuerValue).sign(Algorithm.none()));

    TenantConfiguration configuration = new TenantConfiguration();
    configuration.setConfigurationUrl("oidConfigUrl");

    Map<String, String> jsonMap = new HashMap<>();
    jsonMap.put("issuer", issuerValue);
    when(restTemplate.getForObject(anyString(), any()))
        .thenReturn(new JSONObject(jsonMap).toString());

    assertThatCode(() -> strategy.execute(token, configuration)).doesNotThrowAnyException();
  }

  @Test
  @SneakyThrows
  public void shouldNotValidateRequest() {
    DecodedJWT token = JWT.decode(JWT.create().sign(Algorithm.none()));
    TenantConfiguration configuration = new TenantConfiguration();
    configuration.setConfigurationUrl("http://url.com");

    String issuerValue = "someinvalidvalue";

    Map<String, String> jsonMap = new HashMap<>();
    jsonMap.put("issuer", "validIssuer");

    when(restTemplate.getForObject(anyString(), any()))
        .thenReturn(new JSONObject(jsonMap).toString());

    assertThatExceptionOfType(IdTokenValidationException.class)
        .isThrownBy(() -> strategy.execute(token, configuration));
  }
}

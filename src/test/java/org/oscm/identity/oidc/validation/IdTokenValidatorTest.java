/**
 * *****************************************************************************
 *
 * <p>Copyright FUJITSU LIMITED 2019
 *
 * <p>Creation Date: 20-09-2019
 *
 * <p>*****************************************************************************
 */
package org.oscm.identity.oidc.validation;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.SneakyThrows;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.oscm.identity.commons.TokenType;
import org.oscm.identity.error.TokenValidationException;
import org.oscm.identity.model.json.TokenDetailsDTO;
import org.oscm.identity.oidc.tenant.TenantConfiguration;
import org.oscm.identity.oidc.validation.strategy.*;
import org.oscm.identity.service.TenantService;
import org.springframework.web.client.RestTemplate;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class IdTokenValidatorTest {

  @Mock private TenantService service;
  @Mock private RestTemplate restTemplate;

  private IdTokenValidator validator;
  private TenantConfiguration tenantConfiguration;
  private KeyPair rsaKeys;

  @Test
  public void shouldValidateIdToken() {
    setTenantConfiguration();
    setRestTemplateResponse();
    generateRSAKeys();

    when(service.loadTenant(any())).thenReturn(tenantConfiguration);

    String token =
        JWT.create()
            .withClaim("aud", "testClient")
            .withClaim("iss", "testIssuer")
            .withExpiresAt(Date.valueOf(LocalDate.now().plusDays(1)))
            .sign(
                Algorithm.RSA256(
                    (RSAPublicKey) rsaKeys.getPublic(), (RSAPrivateKey) rsaKeys.getPrivate()));

    TokenDetailsDTO tokenDetails =
        TokenDetailsDTO.of().tokenType(TokenType.ID_TOKEN).token(token).build();
    try {
      validator = new IdTokenValidator("default", tokenDetails, service);
      setStrategies();
      validator.validate();
    } catch (TokenValidationException e) {
      fail(e);
    }
  }

  @Test
  public void shouldNotValidateIdToken() {
    TokenDetailsDTO tokenDetails =
        TokenDetailsDTO.of().token("SOMETOKEN").tokenType(TokenType.ID_TOKEN).build();
    assertThatExceptionOfType(TokenValidationException.class)
        .isThrownBy(() -> new IdTokenValidator("default", tokenDetails, service).validate());
  }

  @SneakyThrows
  private void generateRSAKeys() {
    KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
    generator.initialize(512);
    rsaKeys = generator.generateKeyPair();
  }

  private void setRestTemplateResponse() {
    Map<String, String> jsonMap = new HashMap<>();
    jsonMap.put("issuer", "testIssuer");
    when(restTemplate.getForObject(anyString(), any()))
        .thenReturn(new JSONObject(jsonMap).toString());
  }

  private void setTenantConfiguration() {
    tenantConfiguration = new TenantConfiguration();
    tenantConfiguration.setConfigurationUrl("someUrl");
    tenantConfiguration.setClientId("testClient");
  }

  private void setStrategies() {
    IdTokenAlgorithmValidationStrategy idTokenAlgorithmValidationStrategy =
        new IdTokenAlgorithmValidationStrategy();
    idTokenAlgorithmValidationStrategy.setExpectedAlgorithmType("RS256");
    validator.setIdTokenAlgorithmValidationStrategy(idTokenAlgorithmValidationStrategy);
    validator.setIdTokenAudienceValidationStrategy(new IdTokenAudienceValidationStrategy());
    validator.setIdTokenExpirationTimeValidationStrategy(
        new IdTokenExpirationTimeValidationStrategy());
    validator.setIdTokenISSValidationStrategy(new IdTokenISSValidationStrategy(restTemplate));
  }
}

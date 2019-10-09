package org.oscm.identity.oidc.validation;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.oscm.identity.commons.TokenType;
import org.oscm.identity.error.TokenValidationException;
import org.oscm.identity.model.json.TokenDetailsDTO;
import org.oscm.identity.oidc.tenant.TenantConfiguration;
import org.oscm.identity.oidc.validation.strategy.AccessTokenAlgorithmValidationStrategy;
import org.oscm.identity.oidc.validation.strategy.AccessTokenExpirationTimeValidationStrategy;
import org.oscm.identity.service.TenantService;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.sql.Date;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class IdpAccessTokenValidatorTest {

  @Mock private TenantService service;

  private KeyPair rsaKeys;
  private IdpAccessTokenValidator validator;
  private TenantConfiguration tenantConfiguration;

  @Test
  public void shouldValidateIdToken() {
    setTenantConfiguration();
    generateRSAKeys();

    when(service.loadTenant(any())).thenReturn(tenantConfiguration);

    String token =
        JWT.create()
            .withExpiresAt(Date.valueOf(LocalDate.now().plusDays(1)))
            .sign(
                Algorithm.RSA256(
                    (RSAPublicKey) rsaKeys.getPublic(), (RSAPrivateKey) rsaKeys.getPrivate()));

    TokenDetailsDTO tokenDetails = TokenDetailsDTO.of().tokenType(TokenType.ID_TOKEN).token(token).build();
    try {
      validator = new IdpAccessTokenValidator("default", tokenDetails, service);
      setStrategies();
      validator.validate();
    } catch (TokenValidationException e) {
      fail(e);
    }
  }

  private void setTenantConfiguration() {
    tenantConfiguration = new TenantConfiguration();
  }

  private void setStrategies() {
    AccessTokenAlgorithmValidationStrategy accessTokenAlgorithmValidationStrategy =
        new AccessTokenAlgorithmValidationStrategy();
    accessTokenAlgorithmValidationStrategy.setExpectedAlgorithmType("RS256");
    validator.setAccessTokenAlgorithmValidationStrategy(accessTokenAlgorithmValidationStrategy);
    validator.setAccessTokenExpirationTimeValidationStrategy(
        new AccessTokenExpirationTimeValidationStrategy());
  }

  @SneakyThrows
  private void generateRSAKeys() {
    KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
    generator.initialize(512);
    rsaKeys = generator.generateKeyPair();
  }
}

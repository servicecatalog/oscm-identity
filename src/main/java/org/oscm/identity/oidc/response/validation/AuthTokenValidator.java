/**
 * *****************************************************************************
 *
 * <p>Copyright FUJITSU LIMITED 2019
 *
 * <p>Creation Date: July 16, 2019
 *
 * <p>*****************************************************************************
 */
package org.oscm.identity.oidc.response.validation;

import com.auth0.jwt.JWT;
import lombok.extern.slf4j.Slf4j;
import org.oscm.identity.oidc.request.TokenValidationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.bind.ValidationException;

/** Validator class for OpenID Authentication Token */
@Slf4j
@Component
public class AuthTokenValidator {

  private static final String TOKEN_VALIDATED_MESSAGE = "Token validated!";

  private TokenValidationRequest request;

  private ISSValidationStrategy issValidationStrategy;
  private AudienceValidationStrategy audienceValidationStrategy;
  private AlgorithmValidationStrategy algorithmValidationStrategy;
  private ExpirationTimeValidationStrategy expirationTimeValidationStrategy;
  private NonceValidationStrategy nonceValidationStrategy;

  @Autowired
  public AuthTokenValidator(
      ISSValidationStrategy issValidationStrategy,
      AudienceValidationStrategy audienceValidationStrategy,
      AlgorithmValidationStrategy algorithmValidationStrategy,
      ExpirationTimeValidationStrategy expirationTimeValidationStrategy,
      NonceValidationStrategy nonceValidationStrategy) {
    this.issValidationStrategy = issValidationStrategy;
    this.audienceValidationStrategy = audienceValidationStrategy;
    this.algorithmValidationStrategy = algorithmValidationStrategy;
    this.expirationTimeValidationStrategy = expirationTimeValidationStrategy;
    this.nonceValidationStrategy = nonceValidationStrategy;
  }

  /**
   * Validates provided token string againts OID rules
   *
   * @param request validation request body
   * @return result of the validation
   */
  public TokenValidationResult validate(TokenValidationRequest request) {
    try {
      request.setDecodedToken(JWT.decode(request.getToken()));
      this.request = request;

      doCheckAgainst(issValidationStrategy);
      doCheckAgainst(audienceValidationStrategy);
      doCheckAgainst(algorithmValidationStrategy);
      doCheckAgainst(expirationTimeValidationStrategy);
      doCheckAgainst(nonceValidationStrategy);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return TokenValidationResult.of()
          .isValid(false)
          .validationFailureReason(e.getMessage())
          .build();
    }

    log.info(TOKEN_VALIDATED_MESSAGE);
    return TokenValidationResult.of().isValid(true).build();
  }

  /**
   * Checks the decoded token against provided validation strategy
   *
   * @param validationStrategy Strategy/method of validating the token
   * @throws ValidationException
   */
  private void doCheckAgainst(TokenValidationStrategy validationStrategy)
      throws ValidationException {
    validationStrategy.execute(request);
  }
}

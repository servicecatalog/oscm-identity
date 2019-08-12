/**
 * *****************************************************************************
 *
 * <p>Copyright FUJITSU LIMITED 2019
 *
 * <p>Creation Date: July 16, 2019
 *
 * <p>*****************************************************************************
 */
package org.oscm.identity.oidc.validation;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.oscm.identity.model.request.TokenValidationRequest;
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
   * Validates provided idToken string againts OID rules
   *
   * @param request validation request body
   * @return result of the validation
   */
  public TokenValidationResult validate(TokenValidationRequest request) {
    try {
      this.request = AuthTokenValidator.decodeTokens(request);

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
   * Checks the decoded idToken against provided validation strategy
   *
   * @param validationStrategy Strategy/method of validating the idToken
   * @throws ValidationException
   */
  private void doCheckAgainst(TokenValidationStrategy validationStrategy)
          throws ValidationException {
    validationStrategy.execute(request);
  }

  /**
   * Decodes tokens inside of the request
   * @param request Token Validation Request
   * @return Token Validation Request with decoded tokens
   * @throws JWTDecodeException
   */
  static TokenValidationRequest decodeTokens(TokenValidationRequest request)
      throws JWTDecodeException {
    if (!Strings.isNullOrEmpty(request.getIdToken()))
      request.setDecodedIdToken(JWT.decode(request.getIdToken()));
    if (!Strings.isNullOrEmpty(request.getAccessToken()))
      request.setDecodedAccessToken(JWT.decode(request.getAccessToken()));
    if (!Strings.isNullOrEmpty(request.getRefreshToken()))
      request.setDecodedRefreshToken(JWT.decode(request.getRefreshToken()));

    return request;
  }
}

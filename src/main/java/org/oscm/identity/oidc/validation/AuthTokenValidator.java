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
import org.oscm.identity.oidc.validation.strategy.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.ValidationException;
import java.util.ArrayList;
import java.util.List;

/** Validator class for OpenID Authentication Token */
@Slf4j
@Service
public class AuthTokenValidator {

  private static final String TOKEN_VALIDATED_MESSAGE = "Token validated!";

  private List<TokenValidationStrategy> validationStrategies = new ArrayList<>();
  private TokenValidationRequest request;

  @Autowired
  public AuthTokenValidator(
      AccessTokenAlgorithmValidationStrategy accessTokenAlgorithmValidationStrategy,
      AccessTokenExpirationTimeValidationStrategy accessTokenExpirationTimeValidationStrategy,
      IdTokenAlgorithmValidationStrategy idTokenAlgorithmValidationStrategy,
      IdTokenAudienceValidationStrategy idTokenAudienceValidationStrategy,
      IdTokenExpirationTimeValidationStrategy idTokenExpirationTimeValidationStrategy,
      IdTokenISSValidationStrategy idTokenISSValidationStrategy,
      IdTokenNonceValidationStrategy idTokenNonceValidationStrategy) {
    validationStrategies.add(accessTokenAlgorithmValidationStrategy);
    validationStrategies.add(accessTokenExpirationTimeValidationStrategy);
    validationStrategies.add(idTokenAlgorithmValidationStrategy);
    validationStrategies.add(idTokenAudienceValidationStrategy);
    validationStrategies.add(idTokenExpirationTimeValidationStrategy);
    validationStrategies.add(idTokenISSValidationStrategy);
    validationStrategies.add(idTokenNonceValidationStrategy);
  }

  /**
   * Validates provided idToken string againts OID rules
   *
   * @param request validation request body
   */
  public void validate(TokenValidationRequest request) throws ValidationException {
    this.request = AuthTokenValidator.decodeTokens(request);
    doCheckAgainst(validationStrategies);
    log.info(TOKEN_VALIDATED_MESSAGE);
  }

  /**
   * Checks the decoded idToken against provided validation strategy
   *
   * @param validationStrategies List strategies/methods for validating the JWT tokens
   */
  private void doCheckAgainst(List<TokenValidationStrategy> validationStrategies)
      throws ValidationException {
    for (TokenValidationStrategy strategy : validationStrategies) {
      // FIXME: instead of passing whole request, let's pass TokenDetails object
      // FIXME:(encoded token (which will be decoded with use of method from TokenValidator abstraction)
      // FIXME along with all the data needed ex. nonce).
      // FIXME:That way, decoded tokens can be removed from the validation request,
      // FIXME:and we would have one type of token per request
      strategy.execute(request);
    }
  }

  /**
   * Decodes tokens inside of the request
   *
   * @param request Token Validation Request
   * @return Token Validation Request with decoded tokens
   * @throws JWTDecodeException
   */
  public static TokenValidationRequest decodeTokens(TokenValidationRequest request)
      throws ValidationException {
    try {
      if (!Strings.isNullOrEmpty(request.getIdToken()))
        request.setDecodedIdToken(JWT.decode(request.getIdToken()));
      if (!Strings.isNullOrEmpty(request.getAccessToken()))
        request.setDecodedAccessToken(JWT.decode(request.getAccessToken()));
    } catch (JWTDecodeException e) {
      throw new ValidationException(e.getMessage(), e);
    }
    return request;
  }
}

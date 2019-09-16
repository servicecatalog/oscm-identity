/**
 * *****************************************************************************
 *
 * <p>Copyright FUJITSU LIMITED 2019
 *
 * <p>Creation Date: July 17, 2019
 *
 * <p>*****************************************************************************
 */
package org.oscm.identity.oidc.validation.strategy;

import lombok.Setter;
import org.oscm.identity.model.request.TokenValidationRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.xml.bind.ValidationException;

@Component
public class AccessTokenAlgorithmValidationStrategy extends TokenValidationStrategy {

  @Value("${auth.signing.algorithm.type}")
  @Setter
  private String expectedAlgorithmType;

  @Override
  public void execute(TokenValidationRequest request) throws ValidationException {
    if (request.getDecodedAccessToken() == null) {
      logAccessTokenNotFound(this);
      return;
    }

    if (!request.getDecodedAccessToken().getAlgorithm().equalsIgnoreCase(expectedAlgorithmType))
      throw new ValidationException(getFailureMessage());
  }

  @Override
  public String getFailureMessage() {
    return "Access token signing algorithm type does not match the expected one";
  }
}

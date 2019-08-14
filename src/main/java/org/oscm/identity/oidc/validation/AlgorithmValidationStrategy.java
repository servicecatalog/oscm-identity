/**
 * *****************************************************************************
 *
 * <p>Copyright FUJITSU LIMITED 2019
 *
 * <p>Creation Date: July 17, 2019
 *
 * <p>*****************************************************************************
 */
package org.oscm.identity.oidc.validation;

import org.oscm.identity.model.request.TokenValidationRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.xml.bind.ValidationException;

@Component
public class AlgorithmValidationStrategy implements TokenValidationStrategy {

  private String expectedAlgorithmType;

  public AlgorithmValidationStrategy(
      @Value("${auth.signing.algorithm.type}") String expectedAlgorithmType) {
    this.expectedAlgorithmType = expectedAlgorithmType;
  }

  @Override
  public void execute(TokenValidationRequest request) throws ValidationException {
    if (!request.getDecodedIdToken().getAlgorithm().equalsIgnoreCase(expectedAlgorithmType)
        || !request.getDecodedAccessToken().getAlgorithm().equalsIgnoreCase(expectedAlgorithmType))
      throw new ValidationException(getFailureMessage());
  }

  @Override
  public String getFailureMessage() {
    return "Signing algorithm type does not match the expected one";
  }
}

/**
 * *****************************************************************************
 *
 * <p>Copyright FUJITSU LIMITED 2019
 *
 * <p>Creation Date: July 17, 2019
 *
 * <p>*****************************************************************************
 */
package org.oscm.identity.oidc.response.validation;

import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.xml.bind.ValidationException;

@Component
public class AlgorithmValidationStrategy implements TokenValidationStrategy {

  private static final String VALIDATION_FAILURE_MESSAGE =
      "Signing algorithm type does not match the expected one";

  @Value("${auth.signing.algorithm.type}")
  private String expectedAlgorithmType;

  @Override
  public void execute(DecodedJWT decodedToken) throws ValidationException {
    if (!decodedToken.getAlgorithm().equalsIgnoreCase(expectedAlgorithmType))
      throw new ValidationException(VALIDATION_FAILURE_MESSAGE);
  }
}

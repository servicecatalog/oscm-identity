/**
 * *****************************************************************************
 *
 * <p>Copyright FUJITSU LIMITED 2019
 *
 * <p>Creation Date: July 19, 2019
 *
 * <p>*****************************************************************************
 */
package org.oscm.identity.oidc.response.validation;

import lombok.extern.slf4j.Slf4j;
import org.oscm.identity.oidc.request.TokenValidationRequest;
import org.springframework.stereotype.Component;

import javax.xml.bind.ValidationException;

@Slf4j
@Component
public class NonceValidationStrategy implements TokenValidationStrategy {

  private static final String VALIDATION_FAILURE_MESSAGE =
      "Nonce provided in a request does not match the one from OID Token";

  @Override
  public void execute(TokenValidationRequest request) throws ValidationException {
    if (request.getNonce() != null) {
      if (!request.getNonce().equals(request.getDecodedToken().getClaim("nonce").asString()))
        throw new ValidationException(VALIDATION_FAILURE_MESSAGE);
    }
  }
}

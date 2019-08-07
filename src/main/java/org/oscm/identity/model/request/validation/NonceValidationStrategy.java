/**
 * *****************************************************************************
 *
 * <p>Copyright FUJITSU LIMITED 2019
 *
 * <p>Creation Date: July 19, 2019
 *
 * <p>*****************************************************************************
 */
package org.oscm.identity.model.request.validation;

import lombok.extern.slf4j.Slf4j;
import org.oscm.identity.model.request.TokenValidationRequest;
import org.springframework.stereotype.Component;

import javax.xml.bind.ValidationException;

@Slf4j
@Component
public class NonceValidationStrategy implements TokenValidationStrategy {

  @Override
  public void execute(TokenValidationRequest request) throws ValidationException {
    if (request.getNonce() != null) {
      if (!request.getNonce().equals(request.getDecodedIdToken().getClaim("nonce").asString()))
        throw new ValidationException(getFailureMessage());
    }
  }

  @Override
  public String getFailureMessage() {
    return "Nonce provided in a request does not match the one from OID Token";
  }
}

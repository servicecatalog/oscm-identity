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

import org.oscm.identity.oidc.request.TokenValidationRequest;

import javax.xml.bind.ValidationException;

/** Interface for OID Token Validation Strategies */
public interface TokenValidationStrategy {

  /**
   * Runs validation for provided decoded OID token
   *
   * @param request validation request body
   * @throws ValidationException
   */
  void execute(TokenValidationRequest request) throws ValidationException;
}

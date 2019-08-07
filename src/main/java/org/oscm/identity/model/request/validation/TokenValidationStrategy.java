/**
 * *****************************************************************************
 *
 * <p>Copyright FUJITSU LIMITED 2019
 *
 * <p>Creation Date: July 16, 2019
 *
 * <p>*****************************************************************************
 */
package org.oscm.identity.model.request.validation;

import org.oscm.identity.model.request.TokenValidationRequest;

import javax.xml.bind.ValidationException;

/** Interface for OIDC tokens validation strategies */
public interface TokenValidationStrategy {

  /**
   * Runs validation for provided decoded OIDC tokens
   *
   * @param request validation request body
   * @throws ValidationException
   */
  void execute(TokenValidationRequest request) throws ValidationException;

  /**
   * Returns validation failure message
   *
   * @return failure message
   */
  String getFailureMessage();
}

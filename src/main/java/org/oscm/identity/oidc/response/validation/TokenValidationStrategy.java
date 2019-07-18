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

import com.auth0.jwt.interfaces.DecodedJWT;

import javax.xml.bind.ValidationException;

/** Interface for OID Token Validation Strategies */
public interface TokenValidationStrategy {

  /**
   * Runs validation for provided decoded OID token
   *
   * @param decodedToken
   * @throws ValidationException
   */
  void execute(DecodedJWT decodedToken) throws ValidationException;
}

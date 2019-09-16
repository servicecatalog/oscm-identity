/**
 * *****************************************************************************
 *
 * <p>Copyright FUJITSU LIMITED 2019
 *
 * <p>Creation Date: July 16, 2019
 *
 * <p>*****************************************************************************
 */
package org.oscm.identity.oidc.validation.strategy;

import lombok.extern.slf4j.Slf4j;
import org.oscm.identity.model.request.TokenValidationRequest;

import javax.xml.bind.ValidationException;

/** Abstraction class for OIDC tokens validation strategies */
@Slf4j
public abstract class TokenValidationStrategy {

  private final String ID_TOKEN_NOT_FOUND_WARNING =
      "ID Token not found on the request. %s will not be executed!";
  private final String ACCESS_TOKEN_NOT_FOUND_WARNING =
      "Access Token not found on the request. %s will not be executed!";

  /**
   * Runs validation for provided decoded OIDC tokens
   *
   * @param request validation request body
   * @throws ValidationException
   */
  public abstract void execute(TokenValidationRequest request) throws ValidationException;

  /**
   * Returns validation failure message
   *
   * @return failure message
   */
  public abstract String getFailureMessage();

  /**
   * Logs the fact that ID token is not present in a validation request
   *
   * @param strategyInstance Strategy Class instance which is about to be executed
   */
  public final void logIDTokenNotFound(Object strategyInstance) {
    log.warn(
        String.format(ID_TOKEN_NOT_FOUND_WARNING, strategyInstance.getClass().getSimpleName()));
  }

  /**
   * Logs the fact that Access token is not present in a validation request
   *
   * @param strategyInstance Strategy Class instance which is about to be executed
   */
  public final void logAccessTokenNotFound(Object strategyInstance) {
    log.warn(
        String.format(ACCESS_TOKEN_NOT_FOUND_WARNING, strategyInstance.getClass().getSimpleName()));
  }
}

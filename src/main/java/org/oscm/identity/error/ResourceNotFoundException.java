/**
 * *****************************************************************************
 *
 * <p>Copyright FUJITSU LIMITED 2019
 *
 * <p>Creation Date: 07-11-2019
 *
 * <p>*****************************************************************************
 */
package org.oscm.identity.error;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends Exception {

  private final boolean isLoggable;

  public ResourceNotFoundException(boolean isLoggable) {
    this.isLoggable = isLoggable;
  }

  public ResourceNotFoundException(String message, boolean isLoggable) {
    super(message);
    this.isLoggable = isLoggable;
  }

  public ResourceNotFoundException(String message, Throwable cause, boolean isLoggable) {
    super(message, cause);
    this.isLoggable = isLoggable;
  }

  public ResourceNotFoundException(Throwable cause, boolean isLoggable) {
    super(cause);
    this.isLoggable = isLoggable;
  }

  public ResourceNotFoundException(
      String message,
      Throwable cause,
      boolean enableSuppression,
      boolean writableStackTrace,
      boolean isLoggable) {
    super(message, cause, enableSuppression, writableStackTrace);
    this.isLoggable = isLoggable;
  }
}

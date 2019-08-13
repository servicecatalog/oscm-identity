/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2019
 *
 *  Creation Date: Aug 13, 2019
 *
 *******************************************************************************/
package org.oscm.identity.error;

/** Exception representing bad http request (400) sent to oscm-identity */
public class InvalidRequestException extends RuntimeException {

  public InvalidRequestException(String msg) {
    super(msg);
  }
}

/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2019
 *
 *  Creation Date: Jul 25, 2019
 *
 *******************************************************************************/

package org.oscm.identity.oidc.request;

import org.springframework.http.ResponseEntity;

/** Interface representing request to identity provider which returns HTTP response */
public interface OIDCReturnRequest {

  /**
   * Executes HTTP request
   *
   * @return HTTP response
   */
  ResponseEntity execute();
}

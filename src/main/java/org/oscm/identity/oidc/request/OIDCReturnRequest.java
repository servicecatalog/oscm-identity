/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2019
 *
 *  Creation Date: Jul 25, 2019
 *
 *******************************************************************************/

package org.oscm.identity.oidc.request;

import org.json.JSONException;
import org.springframework.http.ResponseEntity;

/** Interface representing http request to identity provider which returns http response */
public interface OIDCReturnRequest {

  /**
   * Executes HTTP request
   *
   * @return HTTP response
   */
  ResponseEntity execute() throws JSONException;
}

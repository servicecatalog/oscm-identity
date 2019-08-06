/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2019
 *
 *  Creation Date: Jul 25, 2019
 *
 *******************************************************************************/

package org.oscm.identity.oidc.request;

import javax.servlet.http.HttpServletResponse;

/** Interface representing request to identity provider which is then redirected */
public interface OIDCRedirectRequest {

  /**
   * Executes HTTP request
   *
   * @param response object used for redirecting the request
   */
  void execute(HttpServletResponse response);

  /**
   * Constructs the url which will be used in the HTTP request
   *
   * @return url to be executed
   */
  String buildUrl();
}

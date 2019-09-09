/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2019
 *
 *  Creation Date: Aug 14, 2019
 *
 *******************************************************************************/

package org.oscm.identity.model.response;

import org.oscm.identity.error.IdentityProviderException;

/** Simple factory class for creating proper response mapper based on given provider */
public class ResponseHandler {

  /**
   * Creates response mapper object based on given provider
   *
   * @param provider configurable name of external identity provider
   * @return response mapper implementation
   */
  public static ResponseMapper getResponseMapper(String provider) {

    ResponseMapper mapper;

    switch (provider) {
      case "default":
        mapper = new DefaultResponseMapper();
        break;
      default:
        throw new IdentityProviderException(
            "No request manager implementation for identity provider [" + provider + "]");
    }
    return mapper;
  }
}

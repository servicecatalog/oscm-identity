/**
 * *****************************************************************************
 *
 * <p>Copyright FUJITSU LIMITED 2019
 *
 * <p>Creation Date: July 22, 2019
 *
 * <p>*****************************************************************************
 */
package org.oscm.identity.oidc.tenant;

import java.util.Properties;

/** Interface for tenat property retrievers */
public interface TenantPropertyFileRetriever {

  /**
   * * Gets properties associated with provided tenant ID_TOKEN
   *
   * @param tenantId tenant id for which the properties will be loaded
   * @return tenant properties
   */
  Properties retrievePropertiesFor(String tenantId);
}

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

import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * Retrieves tenant properties from the classpath
 */
@Component
public class ClasspathTenantPropertyRetriever implements TenantPropertyFileRetriever {

  @Override
  public Properties retrievePropertiesFor(String tenantId) {
    return null;
  }
}

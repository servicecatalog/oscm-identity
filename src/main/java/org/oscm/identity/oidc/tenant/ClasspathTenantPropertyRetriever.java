/**
 * *****************************************************************************
 *
 * <p>Copyright FUJITSU LIMITED 2019
 *
 * <p>Creation Date: July 29, 2019
 *
 * <p>*****************************************************************************
 */
package org.oscm.identity.oidc.tenant;

import org.oscm.identity.error.TenantConfigurationException;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/** Retrieves tenant properties from the classpath */
@Component
@Profile({"test", "dev"})
public class ClasspathTenantPropertyRetriever implements TenantPropertyFileRetriever {

  private Properties properties;

  @Override
  public Properties retrievePropertiesFor(String tenantId) {
    properties = new Properties();

    try {
      File file = new ClassPathResource("tenant-" + tenantId + ".properties").getFile();
      InputStream inputStream = new FileInputStream(file);
      properties.load(inputStream);
    } catch (IOException exc) {
      throw new TenantConfigurationException(
          "Tenant configuration [tenantId = " + tenantId + "] could not be loaded", exc);
    }

    return properties;
  }
}

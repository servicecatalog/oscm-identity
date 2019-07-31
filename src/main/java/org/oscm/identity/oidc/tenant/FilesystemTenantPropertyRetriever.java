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

import lombok.extern.slf4j.Slf4j;
import org.oscm.identity.error.TenantConfigurationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Properties;

/** Retrieves tenant properties from directory specified in <i>application.properties</i> */
@Slf4j
@Component
@Profile("prod")
public class FilesystemTenantPropertyRetriever implements TenantPropertyFileRetriever {

  @Value("${tenant.config.directory.relative.path}")
  private String tenantConfigDirectoryRelativePath;

  private Properties properties;

  @Override
  public Properties retrievePropertiesFor(String tenantId) {
    properties = new Properties();
    String propertyFilePath = getPropertiesFilePathForTenant(tenantId);

    try {
      InputStream inputStream = getFileInputSteam(propertyFilePath);
      properties.load(inputStream);
    } catch (IOException exc) {
      TenantConfigurationException ex =
          new TenantConfigurationException(
              "Tenant configuration [tenantId = " + tenantId + "] could not be loaded", exc);

      log.error(ex.getMessage(), exc);
      throw ex;
    }

    return properties;
  }

  private String getPropertiesFilePathForTenant(String tenantId) {
    return getTenantConfigDirectoryPath() + "tenant-" + tenantId + ".properties";
  }

  private String getTenantConfigDirectoryPath() {
    File classPath = new File(System.getProperty("java.class.path"));
    String jarAbsolutePath = classPath.getAbsolutePath();

    return jarAbsolutePath.substring(0, jarAbsolutePath.length() - classPath.getName().length())
        + tenantConfigDirectoryRelativePath;
  }

  protected InputStream getFileInputSteam(String propertyFilePath) throws FileNotFoundException {
    return new FileInputStream(propertyFilePath);
  }
}

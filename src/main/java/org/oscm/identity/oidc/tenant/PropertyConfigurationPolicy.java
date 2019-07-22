package org.oscm.identity.oidc.tenant;

import lombok.extern.slf4j.Slf4j;
import org.oscm.identity.error.TenantConfigurationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Properties;

@Component
@Slf4j
public class PropertyConfigurationPolicy implements TenantConfigurationPolicy {

  @Value("${tenant.config.directory.relative.path}")
  private String tenantConfigDirectoryRelativePath;

  @Override
  public TenantConfiguration loadTenant(String tenantId) {

    Properties properties = new Properties();
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

    TenantConfiguration configuration = new TenantConfiguration();
    configuration.setTenantId(tenantId);
    configuration.setProvider(properties.getProperty("oidc.provider"));
    configuration.setAuthUrl(properties.getProperty("oidc.authUrl"));
    configuration.setClientId(properties.getProperty("oidc.clientId"));
    configuration.setIdTokenRedirectUrl(properties.getProperty("oidc.idTokenRedirectUrl"));
    configuration.setOidConfigUrl(properties.getProperty("oidc.openidConfigurationUrl"));
    configuration.setLogoutUrl((properties.getProperty("oidc.logoutUrl")));

    return configuration;
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

package org.oscm.identity.oidc.tenant;

import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Component
public class PropertyConfigurationPolicy implements TenantConfigurationPolicy {

    @Override
    public TenantConfiguration loadTenant(String tenantId) {

        Properties properties = new Properties();

        try {
            File file = ResourceUtils.getFile("classpath:tenant-" + tenantId + ".properties");
            InputStream inputStream = new FileInputStream(file);
            properties.load(inputStream);
        } catch (IOException e) {
            //TODO: add exception handling
            e.printStackTrace();
        }

        TenantConfiguration configuration = new TenantConfiguration();
        configuration.setTenantId(tenantId);
        configuration.setProvider(properties.getProperty("oidc.provider"));
        configuration.setAuthUrl(properties.getProperty("oidc.authUrl"));
        configuration.setClientId(properties.getProperty("oidc.clientId"));

        return configuration;
    }
}

/**
 * *****************************************************************************
 *
 * <p>Copyright FUJITSU LIMITED 2019
 *
 * <p>Creation Date: 04-11-2019
 *
 * <p>*****************************************************************************
 */
package org.oscm.identity.oidc.validation;

import lombok.SneakyThrows;
import org.oscm.identity.error.TenantConfigurationException;
import org.oscm.identity.oidc.tenant.TenantConfiguration;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
public class TenantConfigurationValidator {

  private static final String TENANT_CONFIG_VALIDATION_ERROR_MESSAGE =
      "Unable to acquire parameter '%s' from current tenant's config";

  private Method method;

  @SneakyThrows
  public void validate(TenantConfiguration configuration) throws TenantConfigurationException {
    if (configuration != null) {
      for (Method method : TenantConfiguration.class.getMethods()) {
        this.method = method;
        if (isConfigurationPropertyGetter()) {
          String paramName = method.getName().substring(3);
          if (method.invoke(configuration) == null) {
            throw new TenantConfigurationException(
                String.format(TENANT_CONFIG_VALIDATION_ERROR_MESSAGE, paramName), null);
          }
        }
      }
    }
  }

  private boolean isConfigurationPropertyGetter() {
    return (method.getName().startsWith("get") && isNotGetClass());
  }

  private boolean isNotGetClass() {
    return !method.getName().equals("getClass");
  }
}

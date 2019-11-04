/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2019
 *
 *  Creation Date: Jun 18, 2019
 *
 *******************************************************************************/

package org.oscm.identity.oidc.tenant;

import lombok.Data;

@Data
public class TenantConfiguration {

  private String provider;
  private String tenantId;
  private String clientId;
  private String clientSecret;
  private String authUrl;
  private String authUrlScope;
  private String logoutUrl;
  private String tokenUrl;
  private String redirectUrl;
  private String configurationUrl;
  private String usersEndpoint;
  private String groupsEndpoint;
  private String idpApiUri;
}

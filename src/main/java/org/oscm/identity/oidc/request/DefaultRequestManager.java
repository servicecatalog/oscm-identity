/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2019
 *
 *  Creation Date: Aug 6, 2019
 *
 *******************************************************************************/

package org.oscm.identity.oidc.request;

import org.springframework.web.client.RestTemplate;

public class DefaultRequestManager implements RequestManager {

  private RestTemplate restTemplate;

  public DefaultRequestManager(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Override
  public AuthorizationRequest initAuthorizationRequest() {
    return new DefaultAuthorizationRequest();
  }

  @Override
  public LogoutRequest initLogoutRequest() {
    return new DefaultLogoutRequest();
  }

  @Override
  public TokenRequest initTokenRequest() {
    return new DefaultTokenRequest(this.restTemplate);
  }

  @Override
  public UserRequest initGetUserRequest() {
    return new DefaultGetUserRequest(this.restTemplate);
  }

  @Override
  public UserRequest initGetUserGroupsRequest() {
    return new DefaultGetUserGroupsRequest(this.restTemplate);
  }

  @Override
  public GroupRequest initCreateGroupRequest() {
    return new DefaultCreateGroupRequest(this.restTemplate);
  }
}

/**
 * *****************************************************************************
 *
 * <p>Copyright FUJITSU LIMITED 2019
 *
 * <p>Creation Date: Aug 6, 2019
 *
 * <p>*****************************************************************************
 */
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
  public UserRequest initUpdateUserRequest() {
    return new DefaultUpdateUserRequest(this.restTemplate);
  }

  @Override
  public UserRequest initGetUserGroupsRequest() {
    return new DefaultGetUserGroupsRequest(this.restTemplate);
  }

  @Override
  public GroupRequest initCreateGroupRequest() {
    return new DefaultCreateGroupRequest(this.restTemplate);
  }

  @Override
  public GroupRequest initAddGroupMemberRequest() {
    return new DefaultAddGroupMemberRequest(this.restTemplate);
  }

  @Override
  public GroupRequest initGetGroupMembersRequest() {
    return new DefaultGetGroupMembersRequest(this.restTemplate);
  }

  @Override
  public GroupRequest initGetGroupsRequest() {
    return new DefaultGetGroupsRequest(this.restTemplate);
  }

  @Override
  public GroupRequest initDeleteGroupRequest() {
    return new DefaultDeleteGroupRequest(this.restTemplate);
  }

  @Override
  public GroupRequest initRemoveGroupMemberRequest() {
    return new DefaultRemoveGroupMemberRequest(this.restTemplate);
  }
}

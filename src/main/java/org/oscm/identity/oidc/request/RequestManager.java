/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2019
 *
 *  Creation Date: Aug 6, 2019
 *
 *******************************************************************************/

package org.oscm.identity.oidc.request;

/**
 * Interface defining factory for initializing the objects representing http requests to identity
 * provider
 */
public interface RequestManager {

  AuthorizationRequest initAuthorizationRequest();

  LogoutRequest initLogoutRequest();

  TokenRequest initTokenRequest();

  UserRequest initGetUserRequest();

  UserRequest initGetUserGroupsRequest();

  GroupRequest initCreateGroupRequest();

  GroupRequest initAddGroupMemberRequest();

  GroupRequest initGetGroupMembersRequest();
}

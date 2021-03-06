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

/**
 * Interface defining factory for initializing the objects representing http requests to identity
 * provider
 */
public interface RequestManager {

  /**
   * Creates authorization request representing http request for authenticating to specific identity
   * provider
   *
   * @return authorization request
   */
  AuthorizationRequest initAuthorizationRequest();

  /**
   * Creates logout request representing http request for logging out of the specific identity
   * provider
   *
   * @return logout request
   */
  LogoutRequest initLogoutRequest();

  /**
   * Creates token request representing http request for acquiring access token from specific
   * identity provider
   *
   * @return token request
   */
  TokenRequest initTokenRequest();

  /**
   * Creates user request representing http request for retrieving user information from specific
   * identity provider
   *
   * @return user request
   */
  UserRequest initGetUserRequest();

  /**
   * Creates user request representing http request for updating user information in specific
   * identity provider
   *
   * @return user request
   */
  UserRequest initUpdateUserRequest();

  /**
   * Creates user request representing http request for retrieving groups which given user belongs
   * to in specific identity provider
   *
   * @return user request
   */
  UserRequest initGetUserGroupsRequest();

  /**
   * Creates group request representing http request for creating new group in specific identity
   * provider
   *
   * @return group request
   */
  GroupRequest initCreateGroupRequest();

  /**
   * Creates group request representing http request for adding new member to group in specific
   * identity provider
   *
   * @return group request
   */
  GroupRequest initAddGroupMemberRequest();

  /**
   * Creates group request representing http request for retrieving group members in specific
   * identity provider
   *
   * @return group request
   */
  GroupRequest initGetGroupMembersRequest();

  /**
   * Creates group request representing http request for retrieving groups in specific identity
   * provider
   *
   * @return group request
   */
  GroupRequest initGetGroupsRequest();

  /**
   * Creates group request representing http request for deleting groups in specific identity
   * provider
   *
   * @return group request
   */
  GroupRequest initDeleteGroupRequest();

  /**
   * Creates group request representing http request for removing member from a group in specific identity
   * provider
   *
   * @return group request
   */
  GroupRequest initRemoveGroupMemberRequest();
}

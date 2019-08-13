/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2019
 *
 *  Creation Date: Aug 13, 2019
 *
 *******************************************************************************/

package org.oscm.identity.oidc.request;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

public class DefaultRequestManagerTest {

  private static DefaultRequestManager requestManager;

  @BeforeAll
  public static void init() {
    RestTemplate restTemplate = new RestTemplate();
    requestManager = new DefaultRequestManager(restTemplate);
  }

  @Test
  public void testInitAuthorizationRequest_invoked_DefaultAuthorizationRequestIsReturned() {

    // when
    AuthorizationRequest request = requestManager.initAuthorizationRequest();

    // then
    Assertions.assertThat(request).isInstanceOf(DefaultAuthorizationRequest.class);
  }

  @Test
  public void testInitLogoutRequest_invoked_DefaultLogoutRequestIsReturned() {

    // when
    LogoutRequest request = requestManager.initLogoutRequest();

    // then
    Assertions.assertThat(request).isInstanceOf(DefaultLogoutRequest.class);
  }

  @Test
  public void testInitTokenRequest_invoked_DefaultTokenRequestIsReturned() {

    // when
    TokenRequest request = requestManager.initTokenRequest();

    // then
    Assertions.assertThat(request).isInstanceOf(DefaultTokenRequest.class);
  }

  @Test
  public void testInitGetUserRequest_invoked_DefaultGetUserRequestIsReturned() {

    // when
    UserRequest request = requestManager.initGetUserRequest();

    // then
    Assertions.assertThat(request).isInstanceOf(DefaultGetUserRequest.class);
  }

  @Test
  public void testInitGetUserGroupsRequest_invoked_DefaultGetUserGroupsRequestIsReturned() {

    // when
    UserRequest request = requestManager.initGetUserGroupsRequest();

    // then
    Assertions.assertThat(request).isInstanceOf(DefaultGetUserGroupsRequest.class);
  }

  @Test
  public void testInitCreateGroupsRequest_invoked_DefaultCreateGroupRequestIsReturned() {

    // when
    GroupRequest request = requestManager.initCreateGroupRequest();

    // then
    Assertions.assertThat(request).isInstanceOf(DefaultCreateGroupRequest.class);
  }

  @Test
  public void testInitAddGroupMemberRequest_invoked_DefaultAddGroupMemberRequestIsReturned() {

    // when
    GroupRequest request = requestManager.initAddGroupMemberRequest();

    // then
    Assertions.assertThat(request).isInstanceOf(DefaultAddGroupMemberRequest.class);
  }
}

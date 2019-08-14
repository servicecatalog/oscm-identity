/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2019
 *
 *  Creation Date: Aug 14, 2019
 *
 *******************************************************************************/
package org.oscm.identity.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.oscm.identity.model.json.UserGroup;
import org.oscm.identity.model.json.UserInfo;
import org.oscm.identity.oidc.request.RequestHandler;
import org.oscm.identity.oidc.request.RequestManager;
import org.oscm.identity.oidc.request.UserRequest;
import org.oscm.identity.oidc.tenant.TenantConfiguration;
import org.oscm.identity.service.TenantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

  @Mock private TenantService tenantService;
  @Mock private RequestHandler requestHandler;
  @Mock private RequestManager requestManager;
  @Mock private UserRequest userRequest;

  @InjectMocks private UserController controller;

  @Test
  public void testGetUser_validInputSent_properResponseIsReturned() throws Exception {

    // given
    String tenantId = "default";
    String bearerToken = "Bearer token";
    UserInfo userInfo = givenUserInfo();
    String userInfoJson = givenUserInfoJsonFromIdP(userInfo);
    ResponseEntity<String> retrievedUser = ResponseEntity.ok(userInfoJson);

    TenantConfiguration configuration = new TenantConfiguration();
    configuration.setProvider("default");
    configuration.setUsersEndpoint("usersEndpoint");

    when(tenantService.loadTenant(any())).thenReturn(configuration);
    when(requestHandler.getRequestManager(anyString())).thenReturn(requestManager);
    when(requestManager.initGetUserRequest()).thenReturn(userRequest);
    when(userRequest.execute()).thenReturn(retrievedUser);

    // when
    ResponseEntity<UserInfo> response =
        controller.getUser(tenantId, userInfo.getUserId(), bearerToken);

    // then
    assertThat(response).extracting(ResponseEntity::getStatusCode).isEqualTo(HttpStatus.OK);
    assertThat(response).extracting(ResponseEntity::getBody).isEqualTo(userInfo);
  }

  @Test
  public void testGetGroupsUserBelongsTo_validInputSent_properResponseIsReturned()
      throws Exception {

    // given
    String tenantId = "default";
    String bearerToken = "Bearer token";
    String userId = "userId";
    UserGroup userGroup =
        UserGroup.of().id("userGroupId").name("OSCM_org").description("testGroup").build();

    HashSet<UserGroup> groups = new HashSet<>();
    groups.add(userGroup);

    ResponseEntity<String> retrievedGroups =
        ResponseEntity.ok(
            "{'value':[{'@odata.type': '#microsoft.graph.group','id':'"
                + userGroup.getId()
                + "', 'description':'"
                + userGroup.getDescription()
                + "','displayName':'"
                + userGroup.getName()
                + "'}]}");

    TenantConfiguration configuration = new TenantConfiguration();
    configuration.setProvider("default");
    configuration.setUsersEndpoint("usersEndpoint");

    when(tenantService.loadTenant(any())).thenReturn(configuration);
    when(requestHandler.getRequestManager(anyString())).thenReturn(requestManager);
    when(requestManager.initGetUserGroupsRequest()).thenReturn(userRequest);
    when(userRequest.execute()).thenReturn(retrievedGroups);

    // when
    ResponseEntity<Set<UserGroup>> response =
        controller.getGroupsUserBelongsTo(tenantId, userId, bearerToken);

    // then
    assertThat(response).extracting(ResponseEntity::getStatusCode).isEqualTo(HttpStatus.OK);
    assertThat(response).extracting(ResponseEntity::getBody).isEqualTo(groups);
  }

  private UserInfo givenUserInfo() {

    UserInfo userInfo =
        UserInfo.of()
            .userId("someUser")
            .firstName("name")
            .lastName("lastName")
            .address("address")
            .phone("phone")
            .city("city")
            .country("country")
            .postalCode("code")
            .email("email")
            .build();

    return userInfo;
  }

  private String givenUserInfoJsonFromIdP(UserInfo userInfo) {

    String userInfoJson =
        "{'userPrincipalName':'"
            + userInfo.getUserId()
            + "','postalCode':'"
            + userInfo.getPostalCode()
            + "','streetAddress':'"
            + userInfo.getAddress()
            + "','country':'"
            + userInfo.getCountry()
            + "','businessPhones':'"
            + userInfo.getPhone()
            + "','surname':'"
            + userInfo.getLastName()
            + "','givenName':'"
            + userInfo.getFirstName()
            + "','mail':'"
            + userInfo.getEmail()
            + "', 'city':'"
            + userInfo.getCity()
            + "'}";

    return userInfoJson;
  }
}

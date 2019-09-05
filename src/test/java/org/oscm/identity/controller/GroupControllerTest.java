/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2019
 *
 *  Creation Date: Aug 12, 2019
 *
 *******************************************************************************/

package org.oscm.identity.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.oscm.identity.error.InvalidRequestException;
import org.oscm.identity.model.json.UserGroup;
import org.oscm.identity.model.json.UserInfo;
import org.oscm.identity.oidc.request.GroupRequest;
import org.oscm.identity.oidc.request.RequestHandler;
import org.oscm.identity.oidc.request.RequestManager;
import org.oscm.identity.oidc.tenant.TenantConfiguration;
import org.oscm.identity.service.TenantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GroupControllerTest {

  @Mock private TenantService tenantService;
  @Mock private RequestHandler requestHandler;
  @Mock private RequestManager requestManager;
  @Mock private GroupRequest groupRequest;
  @Mock private UserController userController;

  @InjectMocks private GroupController controller;

  private static String APOSTROPHE = "'";

  @Test
  public void testCreateGroup_validInputSent_properResponseIsReturned() throws Exception {

    // given
    String tenantId = "default";
    String bearerToken = "Bearer token";
    UserGroup userGroup =
        UserGroup.of().id("userGroupId").name("OSCM_org").description("testGroup").build();

    TenantConfiguration configuration = new TenantConfiguration();
    configuration.setProvider("default");
    configuration.setGroupsEndpoint("groupEndpoint");

    ResponseEntity<String> createdUserGroup =
        ResponseEntity.ok()
            .body(
                "{'id':'"
                    + userGroup.getId()
                    + "', 'displayName':'"
                    + userGroup.getName()
                    + "', 'description':'"
                    + userGroup.getDescription()
                    + "'}");

    when(tenantService.loadTenant(any())).thenReturn(configuration);
    when(requestHandler.getRequestManager(anyString())).thenReturn(requestManager);
    when(requestManager.initCreateGroupRequest()).thenReturn(groupRequest);
    when(groupRequest.execute()).thenReturn(createdUserGroup);

    // when
    ResponseEntity response = controller.createGroup(tenantId, bearerToken, userGroup);

    // then
    assertThat(response).extracting(ResponseEntity::getStatusCode).isEqualTo(HttpStatus.CREATED);
    assertThat(response).extracting(ResponseEntity::getBody).isEqualTo(userGroup);
  }

  @Test
  public void testAddMember_validInputSent_properResponseIsReturned() throws Exception {

    // given
    String tenantId = "default";
    String groupId = "groupId";
    String bearerToken = "Bearer token";
    UserInfo user = UserInfo.of().userId("userId").build();

    TenantConfiguration configuration = new TenantConfiguration();
    configuration.setProvider("default");
    configuration.setGroupsEndpoint("groupEndpoint");

    when(userController.getGroupsUserBelongsTo(anyString(), anyString(), anyString()))
        .thenReturn(ResponseEntity.ok(Collections.emptySet()));
    when(tenantService.loadTenant(any())).thenReturn(configuration);
    when(requestHandler.getRequestManager(anyString())).thenReturn(requestManager);
    when(requestManager.initAddGroupMemberRequest()).thenReturn(groupRequest);

    // when
    ResponseEntity response = controller.addMember(tenantId, groupId, bearerToken, user);

    // then
    assertThat(response).extracting(ResponseEntity::getStatusCode).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Test
  public void testAddMember_userIsAlreadyAssigned_invalidRequestExceptionIsThrown()
      throws Exception {

    // given
    String tenantId = "default";
    String groupId = "groupId";
    String bearerToken = "Bearer token";
    UserInfo user = UserInfo.of().userId("userId").build();
    UserGroup userGroup =
        UserGroup.of().id("userGroupId").name("OSCM_org").description("testGroup").build();

    Set<UserGroup> groups = new HashSet<>();
    groups.add(userGroup);

    when(userController.getGroupsUserBelongsTo(anyString(), anyString(), anyString()))
        .thenReturn(ResponseEntity.ok(groups));

    // when and then
    assertThatExceptionOfType(InvalidRequestException.class)
        .isThrownBy(() -> controller.addMember(tenantId, groupId, bearerToken, user));
  }

  @Test
  public void testGetMembers_validInputSent_properResponseIsReturned() throws Exception {

    // given
    String tenantId = "default";
    String bearerToken = "Bearer token";
    String groupId = "groupId";

    UserInfo userInfo = givenUserInfo();
    HashSet<UserInfo> users = new HashSet<>();
    users.add(userInfo);

    String retrievedJson =
        new StringBuilder("{'value':[{")
            .append("'@odata.type': '#microsoft.graph.user'")
            .append(",'userPrincipalName':")
            .append(APOSTROPHE + userInfo.getUserId() + APOSTROPHE)
            .append(",'givenName':")
            .append(APOSTROPHE + userInfo.getFirstName() + APOSTROPHE)
            .append(",'surname':")
            .append(APOSTROPHE + userInfo.getLastName() + APOSTROPHE)
            .append(",'mail':")
            .append(APOSTROPHE + userInfo.getEmail() + APOSTROPHE)
            .append(",'country':")
            .append(APOSTROPHE + userInfo.getCountry() + APOSTROPHE)
            .append(",'city':")
            .append(APOSTROPHE + userInfo.getCity() + APOSTROPHE)
            .append(",'streetAddress':")
            .append(APOSTROPHE + userInfo.getAddress() + APOSTROPHE)
            .append(",'businessPhones':")
            .append(APOSTROPHE + userInfo.getPhone() + APOSTROPHE)
            .append(",'postalCode':")
            .append(APOSTROPHE + userInfo.getPostalCode() + APOSTROPHE)
            .append("}]}")
            .toString();

    ResponseEntity<String> retrievedUsers = ResponseEntity.ok(retrievedJson);

    TenantConfiguration configuration = new TenantConfiguration();
    configuration.setProvider("default");
    configuration.setGroupsEndpoint("groupsEndpoint");

    when(tenantService.loadTenant(any())).thenReturn(configuration);
    when(requestHandler.getRequestManager(anyString())).thenReturn(requestManager);
    when(requestManager.initGetGroupMembersRequest()).thenReturn(groupRequest);
    when(groupRequest.execute()).thenReturn(retrievedUsers);

    // when
    ResponseEntity<UserInfo> response = controller.getMembers(tenantId, groupId, bearerToken);

    // then
    assertThat(response).extracting(ResponseEntity::getStatusCode).isEqualTo(HttpStatus.OK);
    assertThat(response).extracting(ResponseEntity::getBody).isEqualTo(users);
  }

  @Test
  public void testGetGroups_validInputSent_properResponseIsReturned() throws Exception {

    // given
    String tenantId = "default";
    String bearerToken = "Bearer token";

    UserGroup userGroup = UserGroup.of().id("userGroupId").name("OSCM_org").description("testGroup").build();
    HashSet<UserGroup> groups = new HashSet<>();
    groups.add(userGroup);

    String retrievedJson =
        new StringBuilder("{'value':[{")
            .append("'id':")
            .append(APOSTROPHE + userGroup.getId() + APOSTROPHE)
            .append(",'displayName':")
            .append(APOSTROPHE + userGroup.getName() + APOSTROPHE)
            .append(",'description':")
            .append(APOSTROPHE + userGroup.getDescription() + APOSTROPHE)
            .append("}]}")
            .toString();

    ResponseEntity<String> retrievedGroups = ResponseEntity.ok(retrievedJson);

    TenantConfiguration configuration = new TenantConfiguration();
    configuration.setProvider("default");
    configuration.setGroupsEndpoint("groupsEndpoint");

    when(tenantService.loadTenant(any())).thenReturn(configuration);
    when(requestHandler.getRequestManager(anyString())).thenReturn(requestManager);
    when(requestManager.initGetGroupsRequest()).thenReturn(groupRequest);
    when(groupRequest.execute()).thenReturn(retrievedGroups);

    // when
    ResponseEntity<UserGroup> response = controller.getGroups(tenantId, bearerToken);

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
}

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
import org.oscm.identity.model.json.UserGroup;
import org.oscm.identity.oidc.request.GroupRequest;
import org.oscm.identity.oidc.request.RequestHandler;
import org.oscm.identity.oidc.request.RequestManager;
import org.oscm.identity.oidc.tenant.TenantConfiguration;
import org.oscm.identity.service.TenantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GroupControllerTest {

  @Mock private TenantService tenantService;
  @Mock private RequestHandler requestHandler;
  @Mock private RequestManager requestManager;
  @Mock private GroupRequest groupRequest;

  @InjectMocks private GroupController controller;

  @Test
  public void testCreateGroup_given_then() throws Exception {

    // given
    String tenantId = "default";
    String bearerToken = "Bearer token";
    UserGroup userGroup = UserGroup.of().id("userGroupId").name("OSCM_org").description("testGroup").build();

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
}

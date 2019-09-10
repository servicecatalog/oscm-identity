/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2019
 *
 *  Creation Date: Jul 29, 2019
 *
 *******************************************************************************/

package org.oscm.identity.oidc.request;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.oscm.identity.error.IdentityProviderException;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@ExtendWith(MockitoExtension.class)
public class RequestHandlerTest {

  @Mock
  private RestTemplate restTemplate;

  @InjectMocks
  private RequestHandler requestHandler;

  @Test
  public void testGetRequestManager_defaultProviderSet_defaultRequestHandlerIsReturned() {

    // given
    String provider = "default";

    // when
    RequestManager manager = requestHandler.getRequestManager(provider);

    // then
    Assertions.assertThat(manager).isInstanceOf(DefaultRequestManager.class);
  }

  @Test
  public void testGetRequestManager_notExistingProviderSet_exceptionIsThrown() {

    // given
    String provider = "simple_provider";

    // when
    Throwable thrown = catchThrowable(() -> requestHandler.getRequestManager(provider));

    // then
    Assertions.assertThat(thrown).isInstanceOf(IdentityProviderException.class);
  }

  @Test
  public void testGetTokenOutOfAuthHeader_validHeaderSet_tokenIsReturned(){

    //given
    String token = "tokenString";

    //when
    String returnedToken = requestHandler.getTokenOutOfAuthHeader("Bearer " + token);

    //then
    assertThat(returnedToken).isEqualTo(token);
  }

  @Test
  public void testAppendStateWithTenantId_validTenantIdPassed_stateIsAppended(){

    //given
    String tenantId = "qwerty123";
    String state = "http://url:port";

    //when
    String appendedState = requestHandler.appendStateWithTenantId(state, tenantId);

    //then
    String expectedState = new StringBuilder(state).append("?tenantId=").append(tenantId).toString();
    assertThat(appendedState).isEqualTo(expectedState);
  }

  @Test
  public void testAppendStateWithTenantId_tenantIdIsEmpty_stateIsNotChanged(){

    //given
    String tenantId = "";
    String state = "http://url:port";

    //when
    String appendedState = requestHandler.appendStateWithTenantId(state, tenantId);

    //then
    String expectedState = state;
    assertThat(appendedState).isEqualTo(expectedState);
  }

  @Test
  public void testGetTenantIdFromState_stateContainsTenant_tenantIdIsReturned(){

    //given
    String tenantId = "qwerty123";
    String state = "http://url:port?tenantId="+tenantId;

    //when
    String returnedTenantId = requestHandler.getTenantIdFromState(state);

    //then
    assertThat(returnedTenantId).isEqualTo(tenantId);
  }

  @Test
  public void testGetTenantIdFromState_stateDoesNotContainTenant_nullIsReturned(){

    //given
    String state = "http://url:port";

    //when
    String returnedTenantId = requestHandler.getTenantIdFromState(state);

    //then
    assertThat(returnedTenantId).isNull();
  }
}

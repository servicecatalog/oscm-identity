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

import static org.assertj.core.api.Assertions.catchThrowable;

@ExtendWith(MockitoExtension.class)
public class RequestHandlerTest {

  @Mock
  private RestTemplate restTemplate;

  @InjectMocks
  private RequestHandler requestHandler;

  @Test
  public void testGetRequestManager_defaultProviderSet_DefaultRequestHandlerIsReturned() {

    // given
    String provider = "default";

    // when
    RequestManager manager = requestHandler.getRequestManager(provider);

    // then
    Assertions.assertThat(manager).isInstanceOf(DefaultRequestManager.class);
  }

  @Test
  public void testGetRequestManager_notExistingProviderSet_ExceptionIsThrown() {

    // given
    String provider = "simple_provider";

    // when
    Throwable thrown = catchThrowable(() -> requestHandler.getRequestManager(provider));

    // then
    Assertions.assertThat(thrown).isInstanceOf(IdentityProviderException.class);
  }
}

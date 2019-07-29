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
import org.oscm.identity.error.IdentityProviderException;

import static org.assertj.core.api.Assertions.catchThrowable;

public class RequestHandlerTest {

  @Test
  public void testGetRequestManager_defaultProviderSet_DefaultRequestHandlerIsReturned() {

    // given
    String provider = "default";

    // when
    RequestManager manager = RequestHandler.getRequestManager(provider);

    // then
    Assertions.assertThat(manager).isInstanceOf(DefaultRequestManager.class);
  }

  @Test
  public void testGetRequestManager_notExistingProviderSet_ExceptionIsThrown() {

    // given
    String provider = "simple_provider";

    // when
    Throwable thrown = catchThrowable(() -> RequestHandler.getRequestManager(provider));

    // then
    Assertions.assertThat(thrown).isInstanceOf(IdentityProviderException.class);
  }
}

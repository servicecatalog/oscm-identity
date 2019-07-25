/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2019
 *
 *  Creation Date: Jul 22, 2019
 *
 *******************************************************************************/
package org.oscm.identity.oidc.request;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class DefaultLogoutRequestTest {

  @Test
  public void testBuildUrl_parametersSet_urlIsValid() {

    // given
    String baseUrl = "http://baseUrl";
    String redirectUrl = "http://redirectUrl";

    LogoutRequest request = new DefaultLogoutRequest();
    request.setBaseUrl(baseUrl);
    request.setRedirectUrl(redirectUrl);

    // when
    String url = request.buildUrl();

    // then
    Assertions.assertThat(url)
        .contains(baseUrl)
        .contains("post_logout_redirect_uri=" + redirectUrl);
  }
}

package org.oscm.identity.oidc.request;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class DefaultLogoutRequestTest {

  @Test
  public void testBuildUrl_parametersSet_urlIsValid() {

    // given
    String baseUrl = "http://baseUrl";
    String redirectUrl = "http://redirectUrl";

    LogoutRequest request = new DefaultLogoutRequest().baseUrl(baseUrl).redirectUrl(redirectUrl);

    // when
    String url = request.buildUrl();

    // then
    Assertions.assertThat(url)
        .contains(baseUrl)
        .contains("post_logout_redirect_uri=" + redirectUrl);
  }
}

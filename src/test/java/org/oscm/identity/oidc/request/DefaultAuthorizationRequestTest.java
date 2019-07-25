/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2019
 *
 *  Creation Date: Jun 18, 2019
 *
 *******************************************************************************/

package org.oscm.identity.oidc.request;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class DefaultAuthorizationRequestTest {

  @Test
  public void testBuildUrl_parametersSet_urlIsValid() {

    // given
    String baseUrl = "http://baseUrl";
    String redirectUrl = "http://redirectUrl";
    String clientId = "some_client-id";
    String responseType = "id_token";
    String responseMode = "form_post";
    String scope = "openid";
    String nonce = "some-nonce";

    AuthorizationRequest request =
        new DefaultAuthorizationRequest();

    request.setBaseUrl(baseUrl);
    request.setRedirectUrl(redirectUrl);
    request.setClientId(clientId);
    request.setResponseType(responseType);
    request.setResponseMode(responseMode);
    request.setScope(scope);
    request.setNonce(nonce);
    // when
    String url = request.buildUrl();

    // then
    Assertions.assertThat(url)
        .contains(baseUrl)
        .contains("redirect_uri=" + redirectUrl)
        .contains("client_id=" + clientId)
        .contains("response_type=" + responseType)
        .contains("response_mode=" + responseMode)
        .contains("scope=" + scope)
        .contains("nonce=" + nonce);
  }
}

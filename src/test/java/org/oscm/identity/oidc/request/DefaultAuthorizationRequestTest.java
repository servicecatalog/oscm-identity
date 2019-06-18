package org.oscm.identity.oidc.request;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class DefaultAuthorizationRequestTest {

    @Test
    public void testBuildUrl_parametersSet_urlIsValid() {

        //given
        String baseUrl = "http://baseUrl";
        String redirectUrl = "http://redirectUrl";
        String clientId = "some_client-id";
        String responseType = "id_token";
        String responseMode = "form_post";
        String scope = "openid";
        String nonce = "some-nonce";

        AuthorizationRequest request = new DefaultAuthorizationRequest()
                .baseUrl(baseUrl)
                .redirectUrl(redirectUrl)
                .clientId(clientId)
                .responseType(responseType)
                .responseMode(responseMode)
                .scope(scope)
                .nonce(nonce);

        //when
        String url = request.buildUrl();

        //then
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

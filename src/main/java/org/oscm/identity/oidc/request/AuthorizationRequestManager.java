package org.oscm.identity.oidc.request;

public class AuthorizationRequestManager {

    public static AuthorizationRequest buildRequest(String provider) {

        AuthorizationRequest request;

        switch (provider) {
            case "default":
                request = new DefaultAuthorizationRequest();
                break;
            default:
                //TODO: add throwing exception and its handling
                request = new DefaultAuthorizationRequest();
                break;
        }

        return request;
    }
}

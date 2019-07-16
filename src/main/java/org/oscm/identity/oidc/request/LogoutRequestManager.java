package org.oscm.identity.oidc.request;

public class LogoutRequestManager {

    public static LogoutRequest buildRequest(String provider) {

        LogoutRequest request;

        switch (provider) {
            case "default":
                request = new DefaultLogoutRequest();
                break;
            default:
                //TODO: add throwing exception and its handling
                request = new DefaultLogoutRequest();
                break;
        }

        return request;
    }
}

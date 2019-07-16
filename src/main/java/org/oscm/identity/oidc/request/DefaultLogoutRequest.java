package org.oscm.identity.oidc.request;

public class DefaultLogoutRequest extends LogoutRequest{
    @Override
    public String buildUrl() {

        return new StringBuilder(getBaseUrl())
                .append("?")
                .append("post_logout_redirect_uri=" + getRedirectUrl())
                .toString();
    }
}

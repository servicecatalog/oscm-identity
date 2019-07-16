package org.oscm.identity.oidc.request;

import lombok.Getter;

public abstract class LogoutRequest {

    @Getter
    private String baseUrl;

    @Getter
    private String redirectUrl;

    public LogoutRequest baseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    public LogoutRequest redirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
        return this;
    }

    public abstract String buildUrl();

}

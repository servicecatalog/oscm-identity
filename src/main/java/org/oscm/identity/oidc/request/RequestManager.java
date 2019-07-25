package org.oscm.identity.oidc.request;

public interface RequestManager {

  AuthorizationRequest initAuthorizationRequest();

  LogoutRequest initLogoutRequest();

  TokenRequest initTokenRequest();
}

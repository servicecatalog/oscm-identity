package org.oscm.identity.oidc.request;

public class DefaultRequestManager implements RequestManager {

  @Override
  public AuthorizationRequest initAuthorizationRequest() {
    return new DefaultAuthorizationRequest();
  }

  @Override
  public LogoutRequest initLogoutRequest() {
    return new DefaultLogoutRequest();
  }

  @Override
  public TokenRequest initTokenRequest() {
    return new DefaultTokenRequest();
  }

  @Override
  public UserRequest initGetUserRequest() {
    return new DefaultGetUserRequest();
  }
}

/**
 * *****************************************************************************
 *
 * <p>Copyright FUJITSU LIMITED 2019
 *
 * <p>Creation Date: July 30, 2019
 *
 * <p>*****************************************************************************
 */
package org.oscm.identity.oidc.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.oscm.identity.error.IdentityProviderException;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class LogoutRequestManagerTest {

  @ParameterizedTest
  @ValueSource(strings = "default")
  public void shouldBuildLogoutRequest_givenProviderOf(String providerId) {
    assertThatCode(() -> LogoutRequestManager.buildRequest(providerId)).doesNotThrowAnyException();
  }

  @Test
  public void shouldThrowAnException_givenUnsupportedProvider() {
    assertThatExceptionOfType(IdentityProviderException.class)
        .isThrownBy(() -> LogoutRequestManager.buildRequest("notsupportedprovider"));
  }
}

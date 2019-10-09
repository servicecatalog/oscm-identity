/**
 * *****************************************************************************
 *
 * <p>Copyright FUJITSU LIMITED 2019
 *
 * <p>Creation Date: 24-09-2019
 *
 * <p>*****************************************************************************
 */
package org.oscm.identity.oidc.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.oscm.identity.commons.TokenType;
import org.oscm.identity.error.TokenValidationException;
import org.oscm.identity.model.json.TokenDetailsDTO;
import org.springframework.beans.factory.BeanFactory;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TokenValidationFlowTest {

  @Mock private BeanFactory beanFactory;
  @InjectMocks TokenValidationFlow validationFlow;

  @Test
  public void shouldReturnIdTokenValidator() {
    TokenDetailsDTO tokenDetails =
        TokenDetailsDTO.of().tokenType(TokenType.ID_TOKEN).token("sometoken").build();

    assertThatCode(() -> validationFlow.forTenantOf("default").withTokenFrom(tokenDetails))
        .doesNotThrowAnyException();
    verify(beanFactory, times(1)).getBean(eq(IdTokenValidator.class), any());
  }

  @Test
  public void shouldReturnIdpAccessTokenValidator() {
    TokenDetailsDTO tokenDetails =
        TokenDetailsDTO.of().tokenType(TokenType.IDP_ACCESS_TOKEN).token("sometoken").build();

    assertThatCode(() -> validationFlow.forTenantOf("default").withTokenFrom(tokenDetails))
        .doesNotThrowAnyException();
    verify(beanFactory, times(1)).getBean(eq(IdpAccessTokenValidator.class), any());
  }

  @Test
  public void shouldReturnApplicationAccessTokenValidator() {
    TokenDetailsDTO tokenDetails =
            TokenDetailsDTO.of().tokenType(TokenType.APPLICATION_ACCESS_TOKEN).token("sometoken").build();

    assertThatCode(() -> validationFlow.forTenantOf("default").withTokenFrom(tokenDetails))
            .doesNotThrowAnyException();
    verify(beanFactory, times(1)).getBean(eq(ApplicationAccessTokenValidator.class), any());
  }

  @Test
  public void shouldThrowAnException_whenUnsupportedTokenValidatorIsRequested() {
    TokenDetailsDTO tokenDetails =
        TokenDetailsDTO.of().tokenType(TokenType.REFRESH_TOKEN).token("sometoken").build();

    assertThatExceptionOfType(TokenValidationException.class)
        .isThrownBy(() -> validationFlow.forTenantOf("default").withTokenFrom(tokenDetails));
  }
}

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

import lombok.SneakyThrows;
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
  @SneakyThrows
  public void shouldReturnIdTokenValidator() {
    TokenDetailsDTO tokenDetails =
        TokenDetailsDTO.of().tokenType(TokenType.ID_TOKEN).token("sometoken").build();

    assertThatCode(() -> validationFlow.forTenantOf("default").withTokenFrom(tokenDetails))
        .doesNotThrowAnyException();
    verify(beanFactory, times(1)).getBean(eq(IdTokenValidator.class), any());
  }

  @Test
  @SneakyThrows
  public void shouldReturnAccessTokenValidator() {
    TokenDetailsDTO tokenDetails =
        TokenDetailsDTO.of().tokenType(TokenType.ACCESS_TOKEN).token("sometoken").build();

    assertThatCode(() -> validationFlow.forTenantOf("default").withTokenFrom(tokenDetails))
        .doesNotThrowAnyException();
    verify(beanFactory, times(1)).getBean(eq(AccessTokenValidator.class), any());
  }

  @Test
  public void shouldThrowAnException_whenUnsupportedTokenValidatorIsRequested() {
    TokenDetailsDTO tokenDetails =
        TokenDetailsDTO.of().tokenType(TokenType.REFRESH_TOKEN).token("sometoken").build();

    assertThatExceptionOfType(TokenValidationException.class)
        .isThrownBy(() -> validationFlow.forTenantOf("default").withTokenFrom(tokenDetails));
  }
}

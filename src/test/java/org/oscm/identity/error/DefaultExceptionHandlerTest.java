/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2019
 *
 *  Creation Date: Aug 14, 2019
 *
 *******************************************************************************/
package org.oscm.identity.error;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.oscm.identity.model.json.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DefaultExceptionHandlerTest {

  private static DefaultExceptionHandler handler;
  @Mock HttpClientErrorException exception;

  @BeforeAll
  public static void init() {
    handler = new DefaultExceptionHandler();
  }

  @Test
  public void testHandleClientError_HttpClientErrorExceptionThrown_properResponseIsReturned()
      throws Exception {

    // given
    ErrorResponse errorResponse =
        ErrorResponse.of()
            .error("InvalidAuthenticationToken")
            .errorDescription("Access token has expired.")
            .build();

    String jsonError =
        "{'error': {'code': '"
            + errorResponse.getError()
            + "','message': '"
            + errorResponse.getErrorDescription()
            + "'}}";

    HttpStatus status = HttpStatus.UNAUTHORIZED;

    when(exception.getStatusCode()).thenReturn(status);
    when(exception.getResponseBodyAsString()).thenReturn(jsonError);

    // when
    ResponseEntity<ErrorResponse> response = handler.handleClientError(exception);

    // then
    assertThat(response).extracting(ResponseEntity::getStatusCode).isEqualTo(status);
    assertThat(response).extracting(ResponseEntity::getBody).isEqualTo(errorResponse);
  }

  @Test
  public void testHandleInvalidRequestException_InvalidExceptionThrown_properResponseIsReturned() {

    // given
    InvalidRequestException exception = new InvalidRequestException("some error message");

    // when
    ResponseEntity<ErrorResponse> response = handler.handleInvalidRequestException(exception);

    // then
    ErrorResponse errorResponse =
        ErrorResponse.of()
            .error("Invalid request")
            .errorDescription(exception.getMessage())
            .build();

    assertThat(response)
        .extracting(ResponseEntity::getStatusCode)
        .isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response).extracting(ResponseEntity::getBody).isEqualTo(errorResponse);
  }
}

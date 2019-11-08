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

import com.jayway.jsonpath.JsonPathException;

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
  public void testHandleDefaultException_ExceptionIsThrown_properResponseIsReturned() {

    // given
    String message = "null pointer exception";
    NullPointerException exception = new NullPointerException(message);

    // when
    ResponseEntity<ErrorResponse> response = handler.handleDefaultException(exception);

    // then
    ErrorResponse errorResponse =
            ErrorResponse.of().error("Internal error").errorDescription(exception.getMessage()).build();
    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

    assertThat(response).extracting(ResponseEntity::getStatusCode).isEqualTo(status);
    assertThat(response).extracting(ResponseEntity::getBody).isEqualTo(errorResponse);
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
  
  @Test
  public void testHandleRequestedResourceNotFoundException_InvalidExceptionThrown_properResponseIsReturned() {

    // given
    ResourceNotFoundException exception = new ResourceNotFoundException(true);

    // when
    ResponseEntity<ErrorResponse> response = handler.handleJsonError(exception);

    // then
    ErrorResponse errorResponse =
            ErrorResponse.of()
                    .error("Requested resource not found")
                    .errorDescription(exception.getMessage())
                    .build();

    assertThat(response)
        .extracting(ResponseEntity::getStatusCode)
        .isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(response).extracting(ResponseEntity::getBody).isEqualTo(errorResponse);
  }

  @Test
  public void testHandleTokenValidationException_TokenValidationExceptionThrown_properResponseIsReturned() {
    //Given
    TokenValidationException exception = new TokenValidationException("some message");

    //When
    ResponseEntity<ErrorResponse> response = handler.handleTokenValidationException(exception);

    //Then
    ErrorResponse errorResponse =
            ErrorResponse.of()
                    .error("Token validation failed")
                    .errorDescription(exception.getMessage())
                    .build();

    assertThat(response)
            .extracting(ResponseEntity::getStatusCode)
            .isEqualTo(HttpStatus.NOT_ACCEPTABLE);
    assertThat(response).extracting(ResponseEntity::getBody).isEqualTo(errorResponse);
  }
}

/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2019
 *
 *  Creation Date: Jun 18, 2019
 *
 *******************************************************************************/
package org.oscm.identity.error;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.oscm.identity.model.json.ErrorResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.xml.bind.ValidationException;

@ControllerAdvice
@Slf4j
public class DefaultExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleDefaultException(Exception ex) {

    log.error(ex.getMessage(), ex);
    ErrorResponse response =
        ErrorResponse.of()
            .error("Internal error")
            .errorDescription(ex.getMessage())
            .build();

    return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(HttpClientErrorException.class)
  public ResponseEntity<ErrorResponse> handleClientError(HttpClientErrorException ex)
      throws JSONException {

    log.error(ex.getMessage(), ex);
    String jsonResponse = ex.getResponseBodyAsString();

    JSONObject json = new JSONObject(jsonResponse);
    ErrorResponse response;

    try {
      JSONObject jsonError = json.getJSONObject("error");

      response =
          ErrorResponse.of()
              .error(jsonError.getString("code"))
              .errorDescription(jsonError.getString("message"))
              .build();
    } catch (Exception e) {

      response =
          ErrorResponse.of()
              .error(json.getString("error"))
              .errorDescription(json.getString("error_description"))
              .build();
    }

    return new ResponseEntity<>(response, ex.getStatusCode());
  }

  @Override
  protected ResponseEntity<Object> handleExceptionInternal(
      Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {

    log.error(ex.getMessage(), ex);

    ErrorResponse errorResponse =
        ErrorResponse.of()
            .error(status.getReasonPhrase())
            .errorDescription(ex.getMessage())
            .build();

    return new ResponseEntity<>(errorResponse, status);
  }

  @ExceptionHandler(InvalidRequestException.class)
  protected ResponseEntity<ErrorResponse> handleInvalidRequestException(
      InvalidRequestException ex) {

    log.error(ex.getMessage(), ex);

    ErrorResponse errorResponse =
        ErrorResponse.of().error("Invalid request").errorDescription(ex.getMessage()).build();

    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(TokenValidationException.class)
  protected ResponseEntity<Object> handleTokenValidationException(TokenValidationException ex) {
    log.error(ex.getMessage(), ex);


    ErrorResponse errorResponse = ErrorResponse.of().error("Token validation failed").errorDescription(ex.getMessage()).build();

    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_ACCEPTABLE);
  }
}

package org.oscm.identity.error;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.oscm.identity.oidc.response.ErrorResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class DefaultExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(Exception.class)
  public ModelAndView handleDefaultException(Exception ex) {

    log.error(ex.getMessage(), ex);

    ModelAndView view = new ModelAndView();
    view.addObject("errorMessage", ex.getMessage());
    view.setViewName("error");

    return view;
  }

  @ExceptionHandler(HttpClientErrorException.class)
  public ResponseEntity<ErrorResponse> handleClientError(HttpClientErrorException ex) throws IOException {

    log.error(ex.getMessage(), ex);
    String jsonResponse = ex.getResponseBodyAsString();

    ObjectMapper mapper = new ObjectMapper();
    Map<String, Object> response = mapper.readValue(jsonResponse, Map.class);
    Map<String, Object> error = (Map<String, Object>) response.get("error");

    ErrorResponse errorResponse = new ErrorResponse();
    errorResponse.setError(error.get("code").toString());
    errorResponse.setErrorDescription(error.get("message").toString());

    return new ResponseEntity<>(errorResponse, ex.getStatusCode());
  }

  @Override
  protected ResponseEntity<Object> handleExceptionInternal(
      Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {

    log.error(ex.getMessage(), ex);

    ErrorResponse errorResponse = new ErrorResponse();
    errorResponse.setError(status.getReasonPhrase());
    errorResponse.setErrorDescription(ex.getMessage());

    return new ResponseEntity<>(errorResponse, status);
  }
}

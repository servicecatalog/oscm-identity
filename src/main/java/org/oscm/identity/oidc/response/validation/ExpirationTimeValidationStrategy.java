/**
 * *****************************************************************************
 *
 * <p>Copyright FUJITSU LIMITED 2019
 *
 * <p>Creation Date: July 18, 2019
 *
 * <p>*****************************************************************************
 */
package org.oscm.identity.oidc.response.validation;

import lombok.extern.slf4j.Slf4j;
import org.oscm.identity.oidc.request.TokenValidationRequest;
import org.springframework.stereotype.Component;

import javax.xml.bind.ValidationException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
@Component
public class ExpirationTimeValidationStrategy implements TokenValidationStrategy {

  @Override
  public void execute(TokenValidationRequest request) throws ValidationException {
    if (LocalDateTime.now()
            .isAfter(convertDateToLocalDateTime(request.getDecodedIdToken().getExpiresAt()))
        || LocalDateTime.now()
            .isAfter(convertDateToLocalDateTime(request.getDecodedAccessToken().getExpiresAt())))
      throw new ValidationException(getFailureMessage());
  }

  @Override
  public String getFailureMessage() {
    return "Token has expired";
  }

  /**
   * Converts java.util.Date to java.Time.LocalDateTime
   *
   * @param dateToConvert Date to convert
   * @return Converted date
   */
  private LocalDateTime convertDateToLocalDateTime(Date dateToConvert) {
    return dateToConvert.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
  }
}

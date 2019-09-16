/**
 * *****************************************************************************
 *
 * <p>Copyright FUJITSU LIMITED 2019
 *
 * <p>Creation Date: July 18, 2019
 *
 * <p>*****************************************************************************
 */
package org.oscm.identity.oidc.validation.strategy;

import lombok.extern.slf4j.Slf4j;
import org.oscm.identity.model.request.TokenValidationRequest;
import org.springframework.stereotype.Component;

import javax.xml.bind.ValidationException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
@Component
public class IdTokenExpirationTimeValidationStrategy extends TokenValidationStrategy {

  @Override
  public void execute(TokenValidationRequest request) throws ValidationException {
    if (request.getDecodedIdToken() == null) {
      logIDTokenNotFound(this);
      return;
    }

    if (LocalDateTime.now()
        .isAfter(convertDateToLocalDateTime(request.getDecodedIdToken().getExpiresAt())))
      throw new ValidationException(getFailureMessage());
  }

  @Override
  public String getFailureMessage() {
    return "Id token has expired";
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

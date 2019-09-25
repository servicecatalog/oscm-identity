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

import com.auth0.jwt.interfaces.DecodedJWT;
import org.oscm.identity.error.IdTokenValidationException;
import org.oscm.identity.oidc.tenant.TenantConfiguration;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
public class IdTokenExpirationTimeValidationStrategy extends TokenValidationStrategy {

  @Override
  public void execute(DecodedJWT decodedToken, TenantConfiguration tenantConfiguration)
      throws IdTokenValidationException {
    if (LocalDateTime.now().isAfter(convertDateToLocalDateTime(decodedToken.getExpiresAt())))
      throw new IdTokenValidationException(getFailureMessage());
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

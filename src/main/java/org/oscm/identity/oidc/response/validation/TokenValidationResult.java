/**
 * *****************************************************************************
 *
 * <p>Copyright FUJITSU LIMITED 2019
 *
 * <p>Creation Date: July 17, 2019
 *
 * <p>*****************************************************************************
 */
package org.oscm.identity.oidc.response.validation;

import lombok.Builder;
import lombok.Value;

/** OID Authentication Token validation result wrapper */
@Value
@Builder(builderMethodName = "of")
public class TokenValidationResult {

  @Builder.Default private boolean isValid = false;
  private String validationFailureReason;
}

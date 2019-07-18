/**
 * *****************************************************************************
 *
 * <p>Copyright FUJITSU LIMITED 2019
 *
 * <p>Creation Date: July 19, 2019
 *
 * <p>*****************************************************************************
 */
package org.oscm.identity.oidc.request;

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/** Token validation request wrapper */
@Builder(builderMethodName = "of")
@Getter
public class TokenValidationRequest {

  private String token;
  private String nonce;
  private String tenantId;
  @Setter private DecodedJWT decodedToken;
}

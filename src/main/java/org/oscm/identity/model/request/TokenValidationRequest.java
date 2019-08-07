/**
 * *****************************************************************************
 *
 * <p>Copyright FUJITSU LIMITED 2019
 *
 * <p>Creation Date: July 19, 2019
 *
 * <p>*****************************************************************************
 */
package org.oscm.identity.model.request;

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/** Token validation request wrapper */
@Getter
@Builder(builderMethodName = "of")
public class TokenValidationRequest {

  private String idToken;
  private String accessToken;
  private String refreshToken;
  private String nonce;
  private String tenantId;
  @Setter private DecodedJWT decodedIdToken;
  @Setter private DecodedJWT decodedAccessToken;
  @Setter private DecodedJWT decodedRefreshToken;
}

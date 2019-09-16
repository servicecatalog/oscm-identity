/**
 * *****************************************************************************
 *
 * <p>Copyright FUJITSU LIMITED 2019
 *
 * <p>Creation Date: July 19, 2019
 *
 * <p>*****************************************************************************
 */
package org.oscm.identity.oidc.validation.strategy;

import lombok.extern.slf4j.Slf4j;
import org.oscm.identity.model.request.TokenValidationRequest;
import org.oscm.identity.oidc.tenant.TenantConfiguration;
import org.oscm.identity.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.bind.ValidationException;
import java.util.Optional;

@Slf4j
@Component
public class IdTokenNonceValidationStrategy extends TokenValidationStrategy {

  private final String NONCE_CLAIM_NAME = "nonce";

  private TenantService tenantService;

  @Autowired
  public IdTokenNonceValidationStrategy(
          TenantService tenantService) {
    this.tenantService = tenantService;
  }

  @Override
  public void execute(TokenValidationRequest request) throws ValidationException {
      //FIXME: This call will be moved to parent class in scope of oscm-identity#38
      TenantConfiguration tenantConfiguration =
              tenantService.loadTenant(Optional.ofNullable(request.getTenantId()));

      if (!tenantConfiguration.getNonce().equals(request.getDecodedIdToken().getClaim(NONCE_CLAIM_NAME).asString()))
        throw new ValidationException(getFailureMessage());
  }

  @Override
  public String getFailureMessage() {
    return "Nonce provided in a request does not match the one from ID` Token";
  }
}

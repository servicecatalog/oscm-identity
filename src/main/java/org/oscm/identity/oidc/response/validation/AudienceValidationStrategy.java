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

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.xml.bind.ValidationException;

@Slf4j
@Component
public class AudienceValidationStrategy implements TokenValidationStrategy {

        @Override
        public void execute(DecodedJWT decodedToken)
                throws ValidationException {
//                TenantConfiguration tenantConfiguration = tenantService.loadTenant()
//                if(decodedToken.getAudience().contains(configurationPolicy.))
        }
}

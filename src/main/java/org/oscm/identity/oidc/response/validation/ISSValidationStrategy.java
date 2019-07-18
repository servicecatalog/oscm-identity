package org.oscm.identity.oidc.response.validation;

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.xml.bind.ValidationException;

@Slf4j
@Component
public class ISSValidationStrategy implements TokenValidationStrategy {

        @Override
        public void execute(DecodedJWT decodedToken) throws ValidationException {
                log.info(">>>>>>>>>>>> VALIDATING ISS");
//                throw new ValidationException("ISS Validation failed");
        }
}

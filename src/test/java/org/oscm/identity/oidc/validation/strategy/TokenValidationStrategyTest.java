package org.oscm.identity.oidc.validation.strategy;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class TokenValidationStrategyTest {

        private TokenValidationStrategy validationStrategy;

        @AfterEach
        public void cleanUp() {
                validationStrategy = null;
        }

        @Test
        public void shouldLogIdTokenNotFound() {
                validationStrategy = new IdTokenExpirationTimeValidationStrategy();
                validationStrategy.logIDTokenNotFound(validationStrategy);
        }
        @Test
        public void shouldLogAccessTokenNotFound() {
                validationStrategy = new AccessTokenExpirationTimeValidationStrategy();
                validationStrategy.logAccessTokenNotFound(validationStrategy);
        }
}
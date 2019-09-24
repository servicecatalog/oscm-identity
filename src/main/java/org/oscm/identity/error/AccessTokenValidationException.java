package org.oscm.identity.error;

/**
 * Exception that is thrown, when validation of the Access Token fails
 */
public class AccessTokenValidationException extends TokenValidationException {

        public AccessTokenValidationException(String message) {
                super(message);
        }

        public AccessTokenValidationException(String message,
                String errorCode) {
                super(message, errorCode);
        }

        public AccessTokenValidationException(Throwable exception) {
                super(exception);
        }

        public AccessTokenValidationException(String message,
                Throwable exception) {
                super(message, exception);
        }

        public AccessTokenValidationException(String message, String errorCode,
                Throwable exception) {
                super(message, errorCode, exception);
        }
}

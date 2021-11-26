package org.oscm.identity.error;

/**
 * Exception that is thrown, when validation of the ID Token fails
 */
public class IdTokenValidationException extends TokenValidationException {

        
        public IdTokenValidationException(String message) {
                super(message);
        }

        public IdTokenValidationException(String message, String errorCode) {
                super(message, errorCode);
        }

        public IdTokenValidationException(Throwable exception) {
                super(exception);
        }

        public IdTokenValidationException(String message, Throwable exception) {
                super(message, exception);
        }

        public IdTokenValidationException(String message, String errorCode,
                Throwable exception) {
                super(message, errorCode, exception);
        }
}

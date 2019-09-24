package org.oscm.identity.error;

import javax.xml.bind.ValidationException;

public class TokenValidationException extends ValidationException {
        public TokenValidationException(String message) {
                super(message);
        }

        public TokenValidationException(String message, String errorCode) {
                super(message, errorCode);
        }

        public TokenValidationException(Throwable exception) {
                super(exception);
        }

        public TokenValidationException(String message, Throwable exception) {
                super(message, exception);
        }

        public TokenValidationException(String message, String errorCode,
                Throwable exception) {
                super(message, errorCode, exception);
        }
}

package org.oscm.identity.error;

public class IdentityProviderException extends RuntimeException{

    private String error;
    public IdentityProviderException(String msg){
        super(msg);
    }

    public IdentityProviderException(String msg, Throwable cause){
        super(msg, cause);
    }
}

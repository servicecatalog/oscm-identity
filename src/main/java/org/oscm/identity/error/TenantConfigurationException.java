package org.oscm.identity.error;

public class TenantConfigurationException extends RuntimeException{

    public TenantConfigurationException(String msg, Throwable cause){
        super(msg, cause);
    }

}

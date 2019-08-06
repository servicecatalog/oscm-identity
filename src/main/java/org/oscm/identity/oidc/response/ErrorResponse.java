package org.oscm.identity.oidc.response;

import lombok.Data;

@Data
public class ErrorResponse {

    private String error;
    private String errorDescription;
}

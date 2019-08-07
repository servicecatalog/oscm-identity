package org.oscm.identity.model.response;

import lombok.Data;

@Data
public class ErrorResponse {

    private String error;
    private String errorDescription;
}

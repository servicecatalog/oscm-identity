package org.oscm.identity.oidc.request;

import org.springframework.http.ResponseEntity;

public interface OIDCReturnRequest {

    ResponseEntity execute();
}

package org.oscm.identity.oidc.request;


import lombok.Getter;
import lombok.Setter;
import org.json.JSONException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class DefaultGetUserGroupsRequest extends UserRequest {

    @Getter
    @Setter
    private String userId;

    private RestTemplate restTemplate;

    public DefaultGetUserGroupsRequest(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public ResponseEntity execute() throws JSONException {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getToken());
        HttpEntity entity = new HttpEntity(headers);

        String url = getBaseUrl() + "/" + getUserId() + "/memberOf";

        ResponseEntity<String> responseEntity =
                restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        return responseEntity;
    }
}

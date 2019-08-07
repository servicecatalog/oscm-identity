package org.oscm.identity.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(builderMethodName = "of")
public class UserInfoResponse {

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String country;
    private String city;
    private String address;
    private String postalCode;
}

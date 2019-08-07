package org.oscm.identity.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(builderMethodName = "of")
public class UserGroupResponse {

    private String name;
}

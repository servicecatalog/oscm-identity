/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2019
 *
 *  Creation Date: Aug 7, 2019
 *
 *******************************************************************************/

package org.oscm.identity.controller;

import org.oscm.identity.model.response.UserGroupResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

/** Controller class for handling incoming group related requests */
public class GroupController {

    @PostMapping
    public ResponseEntity<UserGroupResponse> createGroup(){

        UserGroupResponse response = UserGroupResponse.of().build();
        return ResponseEntity.status(201).body(response);
    }

}

/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2019
 *
 *  Creation Date: Aug 9, 2019
 *
 *******************************************************************************/

package org.oscm.identity.model.json;

import lombok.Builder;
import lombok.Data;

/** Simple object representing json with user information */
@Data
@Builder(builderMethodName = "of")
public class UserInfo {

  private String firstName;
  private String lastName;
  private String email;
  private String phone;
  private String country;
  private String city;
  private String address;
  private String postalCode;
}

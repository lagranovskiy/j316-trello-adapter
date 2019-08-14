package org.j316.trelloplan.data;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class Person {

  String uuid;
  String forename;
  String surname;

  String gender;
  String mobilePhone;
  String email;
}

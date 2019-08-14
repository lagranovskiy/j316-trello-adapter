package org.j316.trelloplan.data;

import java.util.List;
import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class ServicePlanPosition {

  String uuid;
  String name;
  List<ServicePlanSection> sections;

}

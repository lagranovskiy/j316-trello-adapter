package org.j316.trelloplan.data;

import java.util.List;
import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class ServicePlanSection {

  String uuid;
  List<ServicePlanParticipant> participants;
  List<Boolean> besetzung;
  List<Boolean> verfuegbarkeit;

}

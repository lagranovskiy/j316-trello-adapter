package org.j316.trelloplan.data;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Builder
@ToString
@Data
public class ServicePlan {

  String uuid;
  String planName;
  List<String> eventDates;
  String planStart;
  String planEnd;
  Integer eventRecurringDays;

  List<ServicePlanPosition> positions;
}

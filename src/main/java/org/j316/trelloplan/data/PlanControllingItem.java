package org.j316.trelloplan.data;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@ToString
@Data
@Builder
public class PlanControllingItem {
  LocalDate eventDate;
  String eventName;
  List<Person> personList;
}

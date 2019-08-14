package org.j316.trelloplan.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.j316.trelloplan.data.Person;
import org.j316.trelloplan.data.PlanControllingItem;
import org.j316.trelloplan.data.ServicePlan;
import org.j316.trelloplan.data.ServicePlanPosition;
import org.j316.trelloplan.data.ServicePlanSection;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PlanControllingItemController {

  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

  public List<PlanControllingItem> preparePlacControllingItems(ServicePlan servicePlan, Map<String, Person> personHashSet) {
    LocalDate planStart = LocalDate.parse(servicePlan.getPlanStart(), FORMATTER);
    LocalDate planEnd = LocalDate.parse(servicePlan.getPlanEnd(), FORMATTER);

    boolean singleThreadedPlan = singleThreadedPlan(servicePlan);

    List<PlanControllingItem> items = new ArrayList<>();
    LocalDate iterator = planStart;
    int eventNr = 0;
    while (iterator.isBefore(planEnd)) {
      iterator = iterator.plus(servicePlan.getEventRecurringDays(), ChronoUnit.DAYS);

      for (ServicePlanPosition position : servicePlan.getPositions()) {
        int finalEventNr = eventNr;
        List<ServicePlanSection> relevantDaySections = position.getSections().stream()
            .filter(section -> section.getBesetzung().get(finalEventNr))
            .collect(Collectors.toList());

        if (!singleThreadedPlan && relevantDaySections.size() > 1) {
          log.error("Something is wrong.. please look why multiple sections of the same position are active for the same day");
        }
        if (relevantDaySections.isEmpty()) {
          continue;
        }

        List<Person> collect = relevantDaySections.stream()
            .flatMap(s -> s.getParticipants().stream().map(p -> personHashSet.get(p.getParticipantUUID()))).collect(Collectors.toList());

        items.add(PlanControllingItem.builder()
            .eventDate(iterator)
            .eventName(position.getName())
            .personList(collect)
            .build());
      }
      eventNr++;
    }

    return items;
  }

  private boolean singleThreadedPlan(ServicePlan servicePlan) {
    boolean sectionWithMoreThenOneParticipantExists = servicePlan.getPositions().stream()
        .anyMatch(p -> p.getSections().stream().anyMatch(s -> s.getParticipants().size() > 1));

    return !sectionWithMoreThenOneParticipantExists;
  }
}

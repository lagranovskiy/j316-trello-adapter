package org.j316.trelloplan.controller;

import static org.neo4j.driver.v1.Values.parameters;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.j316.trelloplan.data.Person;
import org.j316.trelloplan.data.ServicePlan;
import org.j316.trelloplan.data.ServicePlanPosition;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataFetchController {

  public static final String PLAN_JSON = "planJSON";
  private final Driver neo4jDriver;

  public Map<String, Person> fetchPlanParticipants(ServicePlan plan) {
    HashSet<String> participantSet = new HashSet<>();

    plan.getPositions().stream()
        .forEach(p -> p.getSections().stream()
            .forEach(s -> s.getParticipants().stream()
                .forEach(
                    participant -> participantSet.add(participant.getParticipantUUID())
                )));

    try (Session session = neo4jDriver.session()) {
      List<Person> personList = session.readTransaction(tx -> {
        StatementResult result = tx.run("MATCH (p:Person) where p.uuid IN $personUUIDArray RETURN p",
            parameters("personUUIDArray", participantSet));
        List<Map<String, Object>> personData = result.list(ha -> ha.get(0).asMap());

        return personData.stream().map(personMap -> Person.builder()
            .uuid(personMap.get("uuid").toString())
            .forename(personMap.get("forename").toString())
            .surname(personMap.get("surname").toString())
            .gender(personMap.get("gender").toString())
            .email(personMap.get("email").toString())
            .mobilePhone(personMap.get("mobilePhone").toString())
            .build()).collect(Collectors.toList());
      });

      HashMap<String, Person> result = new HashMap<>();
      personList.forEach(person -> result.put(person.getUuid(), person));
      return result;
    }
  }

  public ServicePlan fetchPlanData(final String planUUID) {
    try (Session session = neo4jDriver.session()) {

      return session.readTransaction(tx -> {
        StatementResult result = tx.run("MATCH (p:ServicePlan) where p.uuid=$uuid RETURN p",
            parameters("uuid", planUUID));
        Map<String, Object> stringObjectMap = result.single().get(0).asMap();

        String planJSON = stringObjectMap.get(PLAN_JSON).toString();

        Gson gson = new Gson();
        TypeToken<List<ServicePlanPosition>> positionTokens = new TypeToken<List<ServicePlanPosition>>() {
        };
        List<ServicePlanPosition> servicePlanPositions = gson.fromJson(planJSON, positionTokens.getType());

        String eventDatesJson = stringObjectMap.get("eventDates").toString();
        TypeToken<List<String>> dateTokens = new TypeToken<List<String>>() {
        };
        List<String> eventDates = gson.fromJson(eventDatesJson, dateTokens.getType());

        ServicePlan servicePlan = ServicePlan.builder()
            .uuid(stringObjectMap.get("uuid").toString())
            .planName(stringObjectMap.get("planName").toString())
            .eventDates(eventDates)
            .eventRecurringDays(Integer.valueOf(stringObjectMap.get("eventRecurringDays").toString()))
            .planStart(stringObjectMap.get("planStart").toString())
            .planEnd(stringObjectMap.get("planEnd").toString())
            .calEventName(stringObjectMap.get("calEventName").toString())
            .positions(servicePlanPositions)
            .build();

        return servicePlan;
      });

    }
  }


}

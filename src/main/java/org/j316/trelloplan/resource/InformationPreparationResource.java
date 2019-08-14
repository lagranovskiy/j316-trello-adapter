package org.j316.trelloplan.resource;

import com.google.gson.Gson;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.j316.trelloplan.controller.DataFetchController;
import org.j316.trelloplan.controller.PlanControllingItemController;
import org.j316.trelloplan.controller.TrelloCommunicationController;
import org.j316.trelloplan.data.Person;
import org.j316.trelloplan.data.PlanControllingItem;
import org.j316.trelloplan.data.ServicePlan;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/information")
@RequiredArgsConstructor
@Slf4j
public class InformationPreparationResource {

  Gson gson = new Gson();

  final DataFetchController dataFetchController;
  final PlanControllingItemController planPreparationController;
  final TrelloCommunicationController trelloCommunicationController;


  @GetMapping(value = "/{planId}")
  public ResponseEntity<String> fetchPlanInformation(@PathVariable String planId) {
    log.info("Processing a info request for plan: {}", planId);

    ServicePlan servicePlan = dataFetchController.fetchPlanData(planId);

    return ResponseEntity.ok()
        .body(servicePlan.toString());
  }


  @GetMapping(value = "/{planId}/participants")
  public ResponseEntity<String> fetchPlanParticipants(@PathVariable String planId) {
    log.info("Processing a info request for plan participants: {}", planId);

    ServicePlan servicePlan = dataFetchController.fetchPlanData(planId);
    Map<String, Person> people = dataFetchController.fetchPlanParticipants(servicePlan);

    return ResponseEntity.ok()
        .body(people.toString());
  }


  @GetMapping(value = "/{planId}/plan")
  public ResponseEntity<String> notificationPlan(@PathVariable String planId) {
    log.info("Processing a info request for plan participants: {}", planId);

    ServicePlan servicePlan = dataFetchController.fetchPlanData(planId);
    Map<String, Person> people = dataFetchController.fetchPlanParticipants(servicePlan);
    List<PlanControllingItem> items = planPreparationController.preparePlacControllingItems(servicePlan, people);

    return ResponseEntity.ok()
        .body(gson.toJson(items));
  }


  @PostMapping(value = "/{planId}/plan/trello")
  public ResponseEntity<String> trelloNotificationPlan(@PathVariable String planId) {
    log.info("Exporting data to Trello: {}", planId);

    ServicePlan servicePlan = dataFetchController.fetchPlanData(planId);
    Map<String, Person> people = dataFetchController.fetchPlanParticipants(servicePlan);
    List<PlanControllingItem> items = planPreparationController.preparePlacControllingItems(servicePlan, people);

    try {
      trelloCommunicationController.addServicePlanGroupedCheckItems(servicePlan, items);

      return ResponseEntity.ok()
          .body("Export completed");

    } catch (IOException e) {
      log.error("Cannot process trello", e);
      return ResponseEntity.unprocessableEntity()
          .body(":(");
    }

  }
}

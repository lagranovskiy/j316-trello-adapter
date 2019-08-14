package org.j316.trelloplan.controller;

import com.google.gson.Gson;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.j316.trelloplan.data.Person;
import org.j316.trelloplan.data.PlanControllingItem;
import org.j316.trelloplan.data.ServicePlan;
import org.j316.trelloplan.data.trello.TrelloBoard;
import org.j316.trelloplan.data.trello.TrelloBoardCard;
import org.j316.trelloplan.data.trello.TrelloBoardList;
import org.j316.trelloplan.data.trello.TrelloChecklist;
import org.j316.trelloplan.data.trello.TrelloChecklistItem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Data
@Slf4j
public class TrelloCommunicationController {

  public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM");
  public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

  @Value("${application.trello.boardId}")
  String boardId;

  @Value("${application.trello.url}")
  String url;

  @Value("${application.trello.apiKey}")
  String trelloKey;

  @Value("${application.trello.apiToken}")
  String trelloAccessToken;

  OkHttpClient client = new OkHttpClient();
  Gson gson = new Gson();

  
  public void addServicePlanGroupedCheckItems(ServicePlan plan, List<PlanControllingItem> items) throws IOException {
    TrelloBoard board = getBoardById(this.boardId);
    TrelloBoardList planList = createList(board, plan.getPlanName());

    Map<LocalDate, List<PlanControllingItem>> groupingMap = new HashMap<>();
    items.forEach(item -> {
      LocalDate eventDate = item.getEventDate();

      if (eventDate.isBefore(LocalDate.now())) {
        log.info("Ignoring events in the past {}", eventDate);
        return;
      }

      if (!groupingMap.containsKey(eventDate)) {
        groupingMap.put(eventDate, new ArrayList<>());
      }
      groupingMap.get(eventDate).add(item);
    });

    List<LocalDate> eventDates = groupingMap.keySet().stream().sorted().collect(Collectors.toList());
    for (LocalDate eventDate : eventDates) {
      String cardName = "Einsatz am " + eventDate.format(FORMATTER);

      String builder = plan.getPlanName()
          + " %0A------- %0A"
          + "Gültig von: **"
          + plan.getPlanStart()
          + "** "
          + "Gültig bis: **"
          + plan.getPlanEnd()
          + "**";

      TrelloBoardCard card = createCard(planList, cardName, builder, eventDate.minus(2, ChronoUnit.DAYS));

      List<PlanControllingItem> dateItemList = groupingMap.get(eventDate);
      for (PlanControllingItem item : dateItemList) {
        String checklistName = item.getEventName();
        TrelloChecklist einsatzTagList = createChecklist(card, checklistName);

        for (Person person : item.getPersonList()) {

          String b = person.getForename()
              + " "
              + person.getSurname()
              + " ( "
              + person.getMobilePhone()
              + " )";

          createChecklistItem(einsatzTagList, b);
        }
      }

    }
  }


  public TrelloBoard testCommunication() throws IOException {
    TrelloBoard board = getBoardById(this.boardId);
    return board;
  }


  private TrelloBoard getBoardById(String boardId) throws IOException {
    Request request = new Request.Builder()
        .url(url + "boards/" + boardId + "?lists=open&key=" + trelloKey + "&token=" + trelloAccessToken)
        .build();

    try (Response response = client.newCall(request).execute()) {
      String s = response.body().string();
      TrelloBoard trelloBoard = gson.fromJson(s, TrelloBoard.class);
      log.info(trelloBoard.toString());

      return trelloBoard;
    }
  }


  private TrelloBoardList createList(TrelloBoard board, String listName) throws IOException {
    Request request = new Request.Builder()
        .url(url + "lists?name=" + listName + "&idBoard=" + board.getId() + "&key=" + trelloKey + "&token=" + trelloAccessToken)
        .post(RequestBody.create(JSON, "{}"))
        .build();

    try (Response response = client.newCall(request).execute()) {
      String s = response.body().string();
      TrelloBoardList trelloBoardList = gson.fromJson(s, TrelloBoardList.class);
      log.info(trelloBoardList.toString());
      return trelloBoardList;
    }
  }


  private TrelloBoardCard createCard(TrelloBoardList list, String cardName, String cardDesc, LocalDate due) throws IOException {
    Request request = new Request.Builder()
        .url(url + "cards?name=" + cardName + "&idList=" + list.getId() + "&desc=" + cardDesc + "&due=" + due + "&key=" + trelloKey
            + "&token=" + trelloAccessToken)
        .post(RequestBody.create(JSON, "{}"))
        .build();

    try (Response response = client.newCall(request).execute()) {
      String s = response.body().string();
      TrelloBoardCard trelloBoardList = gson.fromJson(s, TrelloBoardCard.class);
      log.info(trelloBoardList.toString());
      return trelloBoardList;
    }
  }


  private TrelloChecklist createChecklist(TrelloBoardCard card, String checklistName) throws IOException {
    Request request = new Request.Builder()
        .url(url + "checklists?name=" + checklistName + "&idCard=" + card.getId() + "&key=" + trelloKey + "&token="
            + trelloAccessToken)
        .post(RequestBody.create(JSON, "{}"))
        .build();

    try (Response response = client.newCall(request).execute()) {
      String s = response.body().string();
      TrelloChecklist trelloBoardList = gson.fromJson(s, TrelloChecklist.class);

      log.info(trelloBoardList.toString());
      return trelloBoardList;
    }
  }


  private TrelloChecklistItem createChecklistItem(TrelloChecklist checklist, String itemName)
      throws IOException {
    Request request = new Request.Builder()
        .url(url + "checklists/" + checklist.getId() + "/checkItems?name=" + itemName + "&key=" + trelloKey + "&token="
            + trelloAccessToken)
        .post(RequestBody.create(JSON, "{}"))
        .build();

    try (Response response = client.newCall(request).execute()) {
      String s = response.body().string();
      TrelloChecklistItem trelloBoardList = gson.fromJson(s, TrelloChecklistItem.class);

      log.info(trelloBoardList.toString());
      return trelloBoardList;
    }
  }

}

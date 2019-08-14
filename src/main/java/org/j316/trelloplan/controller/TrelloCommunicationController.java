package org.j316.trelloplan.controller;

import com.google.gson.Gson;
import java.io.IOException;
import java.time.LocalDate;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.j316.trelloplan.data.trello.TrelloBoard;
import org.j316.trelloplan.data.trello.TrelloBoardCard;
import org.j316.trelloplan.data.trello.TrelloBoardList;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Data
@Slf4j
public class TrelloCommunicationController {

  @Value("${application.trello.url}")
  String url;

  @Value("${application.trello.apiKey}")
  String trelloKey;

  @Value("${application.trello.apiToken}")
  String trelloAccessToken;

  OkHttpClient client = new OkHttpClient();
  public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
  Gson gson = new Gson();

  public void testTrelloConnectionWorks() throws IOException {
    TrelloBoard board = getBoardById("KWeHYqWo");
    TrelloBoardList new_nice_list = createList(board.getId(), "New Nice List");
    TrelloBoardCard card = createCard(new_nice_list.getId(), "Created Card", "Description of the super card", LocalDate.now());
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


  private TrelloBoardList createList(String boardId, String listName) throws IOException {
    Request request = new Request.Builder()
        .url(url + "lists?name=" + listName + "&idBoard=" + boardId + "&key=" + trelloKey + "&token=" + trelloAccessToken)
        .post(RequestBody.create(JSON, "{}"))
        .build();

    try (Response response = client.newCall(request).execute()) {
      String s = response.body().string();
      TrelloBoardList trelloBoardList = gson.fromJson(s, TrelloBoardList.class);
      log.info(trelloBoardList.toString());
      return trelloBoardList;
    }
  }


  private TrelloBoardCard createCard(String listId, String cardName, String cardDesc, LocalDate due) throws IOException {
    Request request = new Request.Builder()
        .url(url + "cards?name=" + cardName + "&idList=" + listId + "&desc=" + cardDesc + "&due=" + due + "&key=" + trelloKey + "&token="
            + trelloAccessToken)
        .post(RequestBody.create(JSON, "{}"))
        .build();

    try (Response response = client.newCall(request).execute()) {
      String s = response.body().string();
      TrelloBoardCard trelloBoardList = gson.fromJson(s, TrelloBoardCard.class);
      log.info(trelloBoardList.toString());
      return trelloBoardList;
    }
  }

}

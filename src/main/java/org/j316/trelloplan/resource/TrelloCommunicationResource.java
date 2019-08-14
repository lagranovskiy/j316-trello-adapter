package org.j316.trelloplan.resource;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.j316.trelloplan.controller.TrelloCommunicationController;
import org.j316.trelloplan.data.trello.TrelloBoard;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/trello")
@RequiredArgsConstructor
@Slf4j
public class TrelloCommunicationResource {

  private final TrelloCommunicationController trelloCommunicationController;

  @GetMapping(value = "/test")
  public ResponseEntity<String> testTrelloCommunication() {
    try {
      TrelloBoard board = trelloCommunicationController.testCommunication();

      return ResponseEntity.ok()
          .body(board.toString());

    } catch (IOException e) {
      log.error("Cannot process trello", e);
      return ResponseEntity.unprocessableEntity()
          .body(":(");
    }


  }

}

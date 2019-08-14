package org.j316.trelloplan.data.trello;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class TrelloChecklistItem {

  String id;
  String idBoard;
  String idCard;
  String name;
  
}

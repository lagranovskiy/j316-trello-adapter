package org.j316.trelloplan.data.trello;

import java.util.List;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class TrelloChecklist {

  String id;
  String idBoard;
  String idCard;
  String name;
  List<TrelloChecklistItem> checkItems;

}

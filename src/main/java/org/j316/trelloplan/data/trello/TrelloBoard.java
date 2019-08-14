package org.j316.trelloplan.data.trello;

import java.util.List;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class TrelloBoard {

  String id;
  String name;
  String desc;
  List<TrelloBoardList> lists;
}

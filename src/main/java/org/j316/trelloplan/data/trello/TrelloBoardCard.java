package org.j316.trelloplan.data.trello;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class TrelloBoardCard {

  String id;
  String desc;
  String due;
  String idBoard;
  String idList;
  String name;
}

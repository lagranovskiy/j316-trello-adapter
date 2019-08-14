package org.j316.trelloplan.config;

import com.julienvey.trello.Trello;
import com.julienvey.trello.impl.TrelloImpl;
import com.julienvey.trello.impl.http.OkHttpTrelloHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TrelloConfiguration {
  @Value("${application.trello.url}")
  String url;

  @Value("${application.trello.apiKey}")
  String trelloKey;

  @Value("${application.trello.apiToken}")
  String trelloAccessToken;

  @Bean
  public Trello initTrelloAPI() {
    return new TrelloImpl(trelloKey, trelloAccessToken, new OkHttpTrelloHttpClient());
  }


}

package org.j316.trelloplan.config;

import lombok.RequiredArgsConstructor;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class Neo4JConnectorConfiguration {

  @Value("${application.neo4j.uri}")
  String uri;

  @Value("${application.neo4j.username}")
  String username;

  @Value("${application.neo4j.password}")
  String password;

  @Bean
  public Driver createNeo4JDriver() {
    return GraphDatabase.driver(uri, AuthTokens.basic(username, password));
  }

}

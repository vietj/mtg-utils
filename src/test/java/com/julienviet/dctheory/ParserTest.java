package com.julienviet.dctheory;

import io.vertx.core.json.JsonObject;
import org.junit.Test;
import wiremock.com.github.jknack.handlebars.internal.Files;

import java.io.File;

public class ParserTest {

  @Test
  public void testFoo() throws Exception {
    File file = new File(ParserTest.class.getClassLoader().getResource("moxfield_wll.json").toURI());
    JsonObject deck = new JsonObject(Files.read(file));
    Parser.toStruct(deck);
  }
}

package com.julienviet.castability;

import io.vertx.core.json.JsonObject;

public class Main2 {

  public static void main(String[] args) throws Exception {
    JsonObject input = new JsonObject().put("land_count", 40)
      .put("land_untapped", new JsonObject().put("R", 20));
    Main main = new Main();
    JsonObject result = main.run(input);
    System.out.println(result);

  }
}

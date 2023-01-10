package com.julienviet.castability;

import io.vertx.core.json.JsonObject;

import java.util.HashMap;

public class KTable extends HashMap<String, Castability.Result> {

  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    forEach((ccm, result) -> {
      JsonObject krow = new JsonObject();
      krow.put("successRatio", result.successRatio);
      krow.put("consistencyCutoffRatio", result.consistencyCutoffRatio);
      krow.put("onCurveRatio", result.onCurveRatio);
      json.put("successRatio", result.successRatio);
      json.put("consistencyCutoffRatio", result.consistencyCutoffRatio);
      json.put(ccm, krow);
    });
    return json;
  }
}

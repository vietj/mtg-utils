package com.julienviet.castability;

import io.vertx.core.json.JsonObject;
import picocli.CommandLine;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

@CommandLine.Command(name = "castability", mixinStandardHelpOptions = true, version = "castability 1.0",
  description = "Prints the checksum (SHA-256 by default) of a file to STDOUT.")
public class Main implements Callable<Integer> {

  public static void main(String[] args) {
    int exitCode = new CommandLine(new Main()).execute(args);
    System.exit(exitCode);
  }

  @CommandLine.Parameters(index = "0", description = "The json file containing the info.")
  public File file;

  @CommandLine.Option(names = { "--iterations"}, description = "The number of simulation iterations (default: ${DEFAULT-VALUE})")
  int iterations = 250_000;

  @CommandLine.Option(names = { "--max-mana-value"}, description = "The maximum mana value of a spell (default: ${DEFAULT-VALUE})")
  int maxManaValue = 7;

  @Override
  public Integer call() throws Exception {
    JsonObject json = new JsonObject(new String(Files.readAllBytes(file.toPath())));
    JsonObject kTable = run(json);
    System.out.println(kTable);
    return 0;
  }

  public JsonObject run(JsonObject json) throws Exception {
    int landCount = json.getInteger("land_count");
    JsonObject untapped = json.getJsonObject("land_untapped");
    final JsonObject kTable = new JsonObject();
    ExecutorService exec = Executors.newFixedThreadPool(8);
    List<Callable<Runnable>> list = new ArrayList<>();
    for (Map.Entry<String, Object> colorEntry : untapped) {
      String color = colorEntry.getKey();
      JsonObject kRow = new JsonObject();
      int count = (int)colorEntry.getValue();
      IntStream.range(1, maxManaValue + 1).forEachOrdered(manaValue -> {
        IntStream.range(1, manaValue + 1).forEachOrdered(coloredCastingCost -> {
          int colorlessCastingCost = manaValue - coloredCastingCost;
          list.add(() -> {
            Castability.Result  result = new Castability()
              .coloredSources(count)
              .colorlessSources(landCount - count)
              .other(99 - landCount)
              .generate(iterations, coloredCastingCost, colorlessCastingCost);
            JsonObject res = new JsonObject();
            res.put("successRatio", result.successRatio);
            res.put("consistencyCutoffRatio", result.consistencyCutoffRatio);
            res.put("onCurveRatio", result.onCurveRatio);
            StringBuilder castingCostString = new StringBuilder();
            if (colorlessCastingCost > 0) {
              castingCostString.append(colorlessCastingCost);
            }
            for (int k = 1;k <= coloredCastingCost;k++) {
              castingCostString.append('C');
            }
            return () -> {
              kRow.put(castingCostString.toString(), res);
            };
          });
        });
        kTable.put(color, kRow);
      });
    }
    try {
      List<Future<Runnable>> results = exec.invokeAll(list);
      for (Future<Runnable> fut : results) {
        fut.get().run();
      }
    } finally {
      exec.shutdown();
    }
    return kTable;
  }
}

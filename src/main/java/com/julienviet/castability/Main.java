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

@CommandLine.Command(name = "castability", mixinStandardHelpOptions = true, version = "castability 1.0",
  description = "Prints the checksum (SHA-256 by default) of a file to STDOUT.")
public class Main implements Callable<Integer> {

  public static void main(String[] args) {
//    Castability.Result  result = new Castability()
//      .coloredSources(10)
//      .colorlessSources(30)
//      .other(59).generate(25_000_000, 1, 7);
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
    int landCount = json.getInteger("land_count");
    JsonObject untapped = json.getJsonObject("land_untapped");
    final JsonObject glo = new JsonObject();
    ExecutorService exec = Executors.newFixedThreadPool(8);
    List<Callable<Runnable>> list = new ArrayList<>();
    for (Map.Entry<String, Object> entry : untapped) {
      String color = entry.getKey();
      JsonObject blah = new JsonObject();
      int count = (int)entry.getValue();
      for (int i = 1;i <= maxManaValue;i++) {
        for (int j = 1;j <= i;j++) {
          int a = j;
          int z = i - j;
          list.add(() -> {
            Castability.Result  result = new Castability()
              .coloredSources(count)
              .colorlessSources(landCount - count)
              .other(99 - landCount).generate(iterations, a, z);
            JsonObject res = new JsonObject();
            res.put("successRatio", result.successRatio);
            res.put("consistencyCutoffRatio", result.consistencyCutoffRatio);
            res.put("onCurveRatio", result.onCurveRatio);
            StringBuilder sb = new StringBuilder();
            if (z > 0) {
              sb.append(z);
            }
            for (int k = 1;k <= a;k++) {
              sb.append('C');
            }
            return () -> {
              blah.put(sb.toString(), res);
            };
          });
        }
        glo.put(color, blah);
      }
    }
    try {
      List<Future<Runnable>> results = exec.invokeAll(list);
      for (Future<Runnable> fut : results) {
        fut.get().run();
      }
    } finally {
      exec.shutdown();
    }
    System.out.println(glo);
    return 0;
  }


}

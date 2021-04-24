package spork.utils;

import java.util.AbstractMap;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class RunTimer {

  private static final Map<String, Long> mResultsMap = new HashMap<>();
  private static long sStartTime;
  private static long mExecutionTime;

  private static void startTiming() {
    sStartTime = System.nanoTime();
  }

  private static void stopTiming() {
    mExecutionTime = (System.nanoTime() - sStartTime) / 1_000_000;
  }

  public static <U> U timeRun(Supplier<U> supplier, String testName) {
    startTiming();
    U result = supplier.get();
    stopTiming();

    mResultsMap.put(testName, mExecutionTime);

    return result;
  }

  public static String getTimingResults() {
    StringBuilder stringBuffer = new StringBuilder();

    stringBuffer.append("\nPrinting ")
        .append(mResultsMap.entrySet().size())
        .append(" results from fastest to slowest\n");

    mResultsMap
        .entrySet()
        .stream()
        .map(entry
            -> new AbstractMap.SimpleImmutableEntry<>
            (entry.getValue(), entry.getKey()))
        .sorted(Comparator.comparing(AbstractMap.SimpleImmutableEntry::getKey))
        .forEach(entry -> stringBuffer
            .append(entry.getValue())
            .append(" executed in ")
            .append(entry.getKey())
            .append(" ms\n"));

    return stringBuffer.toString();
  }
}

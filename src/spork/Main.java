package spork;

import java.io.File;
import java.net.URISyntaxException;
import java.util.concurrent.ForkJoinPool;
import spork.utils.RunTimer;

class Main {

  public static void main(String[] args) throws URISyntaxException {
    System.out.println("Starting the file counter program");

    runFileCounterWalkFileTree();
    runFileCounterTask();
    runFileCounterParallelStream();

    System.out.println(RunTimer.getTimingResults());
    System.out.println("Ending the file counter program");
  }

  private static void runFileCounterWalkFileTree() throws URISyntaxException {
    runTest(ForkJoinPool.commonPool(),
        new FileCounterWalkFileTree(new File(ClassLoader.getSystemResource("TestDirs").toURI())),
        "spork.FileCounterWalkFileTree",
        false);
  }

  private static void runFileCounterTask() throws URISyntaxException {
    runTest(new ForkJoinPool(),
        new FileCounterTask(new File(ClassLoader.getSystemResource("TestDirs").toURI())),
        "spork.FileCounterTask",
        true);
  }

  private static void runFileCounterParallelStream() throws URISyntaxException {
    runTest(ForkJoinPool.commonPool(),
        new FileCounterParallelStream(new File(ClassLoader.getSystemResource("TestDirs").toURI())),
        "spork.FileCounterParallelStream",
        true);
  }

  private static void runTest(ForkJoinPool fJPool, AbstractFileCounter testTask, String testName,
      boolean printStats) {
    // Run the GC first to avoid perturbing the tests.
    System.gc();

    long size = RunTimer.timeRun(() -> fJPool.invoke(testTask), testName);

    System.out.println(testName
        + ": "
        + (testTask.documentCount()
        + testTask.folderCount())
        + " files ("
        + testTask.documentCount()
        + " documents and "
        + testTask.folderCount()
        + " folders) contained "
        + size // / 1_000_000)
        + " bytes");

    if (printStats) {
      System.out.println("pool size = "
          + fJPool.getPoolSize()
          + ", steal count = "
          + fJPool.getStealCount()
          + ", running thread count = "
          + fJPool.getRunningThreadCount());
    }
  }
}


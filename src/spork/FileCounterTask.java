package spork;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.atomic.AtomicLong;

public class FileCounterTask extends AbstractFileCounter {

  FileCounterTask(File file) {
    super(file);
  }

  private FileCounterTask(File file,
      AtomicLong documentCount,
      AtomicLong folderCount) {
    super(file, documentCount, folderCount);
  }

  @Override
  protected Long compute() {
    if (mFile.isFile()) {
      mDocumentCount.incrementAndGet();

      return mFile.length();
    } else {
      mFolderCount.incrementAndGet();

      List<ForkJoinTask<Long>> forks = new ArrayList<>();
      for (File file : Objects.requireNonNull(mFile.listFiles())) {
        forks.add(new FileCounterTask(file, mDocumentCount, mFolderCount).fork());
      }

      long sum = 0;
      for (ForkJoinTask<Long> task : forks) {
        sum += task.join();
      }

      return sum;
    }
  }
}


package spork;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

public class FileCounterParallelStream extends AbstractFileCounter {

  FileCounterParallelStream(File file) {
    super(file);
  }

  private FileCounterParallelStream(File file, AtomicLong documentCount, AtomicLong folderCount) {
    super(file, documentCount, folderCount);
  }

  @Override
  protected Long compute() {
    return Stream
        .of(Objects.requireNonNull(mFile.listFiles()))
        .parallel() // Convert the sequential stream to a parallel stream.
        .mapToLong(file -> {
          if (file.isFile()) {
            mDocumentCount.incrementAndGet();

            return mFile.length();
          } else {
            mFolderCount.incrementAndGet();

            return new FileCounterParallelStream(file, mDocumentCount, mFolderCount).compute();
          }
        }).sum();
  }
}


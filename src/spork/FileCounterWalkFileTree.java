package spork;

import static java.nio.file.FileVisitResult.CONTINUE;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicLong;

public class FileCounterWalkFileTree extends AbstractFileCounter {

  FileCounterWalkFileTree(File rootFile) {
    super(rootFile);
  }

  @Override
  protected Long compute() {
    AtomicLong totalSize = new AtomicLong(0);
    try {
      Files
          .walkFileTree(mFile.toPath(), new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
              mFolderCount.incrementAndGet();

              return CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
              mDocumentCount.incrementAndGet();
              totalSize.addAndGet(file.toFile().length());

              return CONTINUE;
            }
          });
    } catch (IOException e) {
      // ignore
    }

    return totalSize.get();
  }
}


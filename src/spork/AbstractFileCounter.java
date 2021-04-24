package spork;

import java.io.File;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.atomic.AtomicLong;

public abstract class AbstractFileCounter extends RecursiveTask<Long> {

  protected final File mFile;
  protected final AtomicLong mDocumentCount;
  protected final AtomicLong mFolderCount;

  AbstractFileCounter(File file) {
    mFile = file;
    mDocumentCount = new AtomicLong(0);
    mFolderCount = new AtomicLong(0);
  }

  AbstractFileCounter(File file, AtomicLong documentCount, AtomicLong folderCount) {
    mFile = file;
    mDocumentCount = documentCount;
    mFolderCount = folderCount;
  }

  public long documentCount() {
    return mDocumentCount.get();
  }

  public long folderCount() {
    return mFolderCount.get();
  }
}


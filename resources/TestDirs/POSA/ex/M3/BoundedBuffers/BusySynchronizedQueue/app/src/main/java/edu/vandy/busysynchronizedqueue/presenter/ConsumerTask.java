package edu.vandy.busysynchronizedqueue.presenter;

import android.widget.ProgressBar;
import android.widget.TextView;

import edu.vandy.busysynchronizedqueue.R;
import edu.vandy.busysynchronizedqueue.model.BusySynchronizedQueue;
import edu.vandy.busysynchronizedqueue.view.MainActivity;

/**
 * The consumer runs in a background thread and receives integers from
 * the ProducerTask via a shared BoundedQueue.
 */
public class ConsumerTask
      extends ProducerConsumerTaskBase {
    /**
     * Constructor initializes the superclass.
     */
    public ConsumerTask(BusySynchronizedQueue<Integer> queue,
                        int maxIterations,
                        MainActivity activity) {
        super("consumer percentage = ",
              maxIterations,
              queue,
              activity,
              (ProgressBar) activity.findViewById(R.id.progressConsumerBar),
              (TextView) activity.findViewById(R.id.progressConsumerCount));
    }

    /**
     * This method is called back after every runtime configuration
     * change in the Mainactivity.
     */
    public void onConfigurationChange(MainActivity activity) {
        mActivity = activity;
        mProgressBar = (ProgressBar) activity.findViewById(R.id.progressConsumerBar);
        mProgressCount = (TextView) activity.findViewById(R.id.progressConsumerCount);
        mProgressBar.setVisibility(ProgressBar.VISIBLE);
        mProgressCount.setText(mMessage + mPercentage);
    }
    
    /**
     * This method runs in a background thread and receives integers
     * sent by the ProducerTask via a shared BoundedQueue.
     */
    @Override
    protected Void doInBackground(Void... v) {
        for (int i = 1; i <= mMaxIterations; ) {
            // Break out of the loop if we're cancelled.
            if (isCancelled())
                break;

            // Try to get the next integer (this call returns null if
            // there's nothing in the queue).
            Integer integer = mQueue.poll();
                        
            // Only publish the progress if we get a non-null value
            // from poll().
            if (integer != null) {
                // Publish the progress every 10%.
                if ((i % (mMaxIterations / 10)) == 0) {
                    /*
                      Log.d(TAG,
                      "doInBackground() on iteration "
                      + i);
                    */

                    // Convert to a percentage of 100.
                    Double percentage =
                        ((double) integer / (double) mMaxIterations) * 100.00;

                    // Publish progress as a % in the UI thread.
                    publishProgress(percentage.intValue());
                }

                // Advanced the count by one.
                i++;
            }
        }

        return null;
    }

    /**
     * Runs in the UI thread after doInBackground() finishes running
     * successfully.
     */
    @Override
    public void onPostExecute(Void v) {
        // Indicate to the activity that we're done.
        mActivity.done();
    }

    /**
     * Runs in the UI thread after doInBackground() is cancelled. 
     */
    @Override
    public void onCancelled(Void v) {
        // Just forward to onPostExecute();
        onPostExecute(v);
    }
}


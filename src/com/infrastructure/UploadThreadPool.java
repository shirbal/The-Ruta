package com.infrastructure;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class UploadThreadPool {

    /*
     * Gets the number of available cores
     * (not always the same as the maximum number of cores)
     */
    private static int NUMBER_OF_CORES =
            Runtime.getRuntime().availableProcessors();

    
    private ThreadPoolExecutor mDecodeThreadPool = null;
	
    // A queue of Runnables
    private final BlockingQueue<Runnable> mDecodeWorkQueue;

    // Sets the amount of time an idle thread waits before terminating
    private static final int KEEP_ALIVE_TIME = 1;
    // Sets the Time Unit to seconds
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
    // Instance of this singleton
	private static UploadThreadPool mInstance = null;
	
	private UploadThreadPool()
	{
	    // Instantiates the queue of Runnables as a LinkedBlockingQueue
	    mDecodeWorkQueue = new LinkedBlockingQueue<Runnable>();
	    mDecodeThreadPool = new ThreadPoolExecutor(
	            NUMBER_OF_CORES,       // Initial pool size
	            NUMBER_OF_CORES,       // Max pool size
	            KEEP_ALIVE_TIME,
	            KEEP_ALIVE_TIME_UNIT,
	            mDecodeWorkQueue);
	}
	
	/**
	 * Return an instance of the singleton
	 * @return
	 */
	public static synchronized UploadThreadPool instance()
	{
		if(mInstance == null)
		{
			mInstance = new UploadThreadPool();
		}
		return mInstance;
	}
	
	/**
	 * Start new thread from the pool executing the action
	 * @param action: Runnable for the thread to run
	 */
	public void startNew(Runnable action)
	{
        mDecodeThreadPool.execute(action);
	}
	
	/**
	 * Returns how many active threads are running
	 * @return
	 */
	public int getUploadersCount()
	{
		return mDecodeThreadPool.getActiveCount();
	}

}

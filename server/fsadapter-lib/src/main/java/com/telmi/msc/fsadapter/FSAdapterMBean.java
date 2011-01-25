package com.telmi.msc.fsadapter;


/**
 *
 * @author jocke
 */
public interface FSAdapterMBean {


    /**
     * Set the port to listen on.
     *
     * @param port The port to listen on.
     */
    void setServerPort(int port);

    /**
     * Get the port the server is listening on.
     *
     * @return The port.
     */
    int getServerPort();

    /**
     * Set this to true when you want maximal performance.
     *
     * <p>
     *  Note that changes on the SCXML document will have no effect
     *  when this is set to true. If this would be a shift
     *  from true to false then will the cache also be cleared.
     * </p>
     *
     * @param use If to use caching or not on the SCXML documents.
     */
    void setScxmlCache(boolean use);

    /**
	 * If we the application at the moment uses caching
	 * for it's SCXML documents.
	 *
	 * @return true if cashing is on false otherwise.
	 */
	boolean isScxmlCache();

	int getNumberOfThreadsForApplicationPool();

	/**
	 * Set the  number of threads this application can use as most.
	 *
	 * <p>
	 * The server uses an internal application pool that runs all
	 * the state machines. As this pool is a fixed size pool setting this
	 * number will also affect the number of state machines this application
	 * can run.
	 * </p>
	 *
	 * @param nThreads Number of threads for the application pool.
	 */
	void setNumberOfThreadsForApplicationPool(int nThreads);
	
	/**
     * Empty the internal map from StateMachines
     * <p>
     *  Note: this will have a negative performance impact
     *  on the application, so use wisely.
     * </p>
     */
    void clearScxmlCache();


    /**
     * This will start monitored services.
     */
    void startFSAdapter();

    /**
     * Stop monitored services.
     *
     */
    void stopFSAdapter();

    /**
     * This will reload the server.
     *
     */
    void reloadFSAdapter();

    /**
     * Get the status of the monitor.
     */
    void statusFSAdapter();





}

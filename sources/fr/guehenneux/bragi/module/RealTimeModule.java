package fr.guehenneux.bragi.module;

/**
 * @author Jonathan Guéhenneux
 */
public abstract class RealTimeModule extends Module {

	/**
	 * @param name
	 */
	public RealTimeModule(String name) {
		super(name);
	}

	@Override
	protected void start() {

		super.start();
	}

	@Override
	protected final void compute() throws InterruptedException {

		long startTime = System.currentTimeMillis();
		double chunkTime = realTimeCompute();
		long endTime = System.currentTimeMillis();

		long chunkTimeMilliseconds = (long) (chunkTime * 1000);
		long sleepTime = chunkTimeMilliseconds - endTime + startTime;

		if (sleepTime > 0) {
			//Thread.sleep(sleepTime);
		}
	}

	/**
	 * @throws InterruptedException
	 */
	protected abstract double realTimeCompute() throws InterruptedException;
}
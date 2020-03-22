package fr.guehenneux.bragi.module.model;

import fr.guehenneux.bragi.Settings;

/**
 * @author Jonathan Guéhenneux
 */
public abstract class RealTimeModule extends Module {

	private long startTime;
	private long totalSampleCount;

	/**
	 * @param name module name
	 */
	public RealTimeModule(String name) {
		super(name);
	}

	@Override
	protected void start() {

		startTime = System.currentTimeMillis();
		totalSampleCount = 0;

		super.start();
	}

	@Override
	protected final void compute() throws InterruptedException {

		totalSampleCount += realTimeCompute();

		double realTime = totalSampleCount / Settings.INSTANCE.getFrameRate();
		long realTimeMilliseconds = Math.round(realTime * 1000);
		long expectedComputeEndTime = startTime + realTimeMilliseconds;
		long currentTime = System.currentTimeMillis();

		if (currentTime < expectedComputeEndTime) {
			Thread.sleep(expectedComputeEndTime - currentTime);
		}
	}

	/**
	 * @return processed sample count
	 */
	protected abstract int realTimeCompute() throws InterruptedException;
}
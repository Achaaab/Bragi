package com.github.achaaab.bragi.module;

import com.github.achaaab.bragi.gui.module.PlayerView;

import static javax.swing.SwingUtilities.invokeLater;

/**
 * @author Jonathan Guéhenneux
 * @since 0.1.0
 */
public abstract class Player extends Module {

	protected double time;
	protected double duration;
	protected boolean playing;

	protected PlayerView view;

	/**
	 * @param name name of the player to create
	 * @since 0.1.4
	 */
	public Player(String name) {

		super(name);

		time = 0.0f;
		playing = true;

		invokeLater(() -> view = new PlayerView(this));
	}

	@Override
	protected int compute() throws InterruptedException {

		synchronized (this) {

			while (!playing) {
				wait();
			}
		}

		return playChunk();
	}

	/**
	 * @return number of played frames
	 * @throws InterruptedException if interrupted while writing on outputs
	 */
	protected abstract int playChunk() throws InterruptedException;

	/**
	 * Starts or resumes playback.
	 */
	public synchronized void play() {

		playing = true;
		notifyAll();
	}

	/**
	 * Pauses playback.
	 */
	public void pause() {
		playing = false;
	}

	/**
	 * Stops playback and set time to {@code 0.0}.
	 */
	public void stop() {

		playing = false;

		time = 0.0;
		duration = 0.0;

		updateView();
	}

	/**
	 * @return current playback time in seconds
	 */
	public double getTime() {
		return time;
	}

	/**
	 * @return track duration in seconds
	 */
	public double getDuration() {
		return duration;
	}

	/**
	 * This method is designed to be called by the view.
	 * Seek the position corresponding to specified time.
	 *
	 * @param time time to seek in seconds
	 */
	public void seek(double time) {
		this.time = time;
	}

	/**
	 * Updates the view, if there is one.
	 */
	protected void updateView() {

		if (view != null) {
			invokeLater(() -> view.updateTime());
		}
	}
}
package com.github.achaaab.bragi.core.module.player;

import com.github.achaaab.bragi.common.Interpolator;
import com.github.achaaab.bragi.common.Settings;
import com.github.achaaab.bragi.core.module.Module;
import com.github.achaaab.bragi.core.module.ModuleCreationException;
import com.github.achaaab.bragi.core.module.ModuleExecutionException;
import com.github.achaaab.bragi.file.AudioFile;
import com.github.achaaab.bragi.file.AudioFileException;
import com.github.achaaab.bragi.gui.module.PlayerView;
import org.slf4j.Logger;

import java.lang.reflect.InvocationTargetException;

import static com.github.achaaab.bragi.common.Interpolator.CUBIC_HERMITE_SPLINE;
import static javax.swing.SwingUtilities.invokeAndWait;
import static javax.swing.SwingUtilities.invokeLater;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Jonathan Guéhenneux
 * @since 0.1.0
 */
public abstract class Player extends Module {

	private static final Logger LOGGER = getLogger(Player.class);

	private static final Interpolator INTERPOLATOR = CUBIC_HERMITE_SPLINE;

	protected AudioFile file;
	protected boolean playing;
	protected PlayerView playerView;

	/**
	 * @param name name of the player to create
	 * @param file audio file to play
	 * @since 0.1.6
	 */
	public Player(String name, AudioFile file) {

		super(name);

		this.file = file;

		addPrimaryOutput(name + "_output_" + outputs.size());

		while (outputs.size() < Settings.INSTANCE.channelCount()) {
			addSecondaryOutput(name + "_output_" + outputs.size());
		}

		try {
			file.open();
		} catch (AudioFileException cause) {
			throw new ModuleCreationException(cause);
		}

		try {
			invokeAndWait(() -> view = playerView = new PlayerView(this));
		} catch (InterruptedException | InvocationTargetException cause) {
			throw new ModuleCreationException(cause);
		}
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
	private int playChunk() throws InterruptedException {

		try {

			var frameCount = 0;
			float[][] chunk;

			synchronized (this) {
				chunk = file.readChunk();
			}

			if (chunk == null) {

				stop();

			} else {

				updateView();

				var channelCount = chunk.length;
				var sourceSampleRate = file.sampleRate();
				var targetSampleRate = Settings.INSTANCE.frameRate();

				for (var channelIndex = 0; channelIndex < channelCount; channelIndex++) {

					outputs.get(channelIndex).write(sourceSampleRate == targetSampleRate ?
							chunk[channelIndex] :
							INTERPOLATOR.interpolate(chunk[channelIndex], sourceSampleRate, targetSampleRate));
				}
			}

			return frameCount;

		} catch (AudioFileException cause) {

			throw new ModuleExecutionException(cause);
		}
	}

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

		synchronized (this) {

			try {

				file.close();
				file.open();

			} catch (AudioFileException cause) {

				throw new ModuleExecutionException(cause);
			}
		}

		playing = false;

		updateView();
	}

	/**
	 * @return current playback time in seconds
	 */
	public double getTime() {
		return file.time();
	}

	/**
	 * @return track duration in seconds
	 */
	public double getDuration() {
		return file.duration();
	}

	/**
	 * This method is designed to be called by the view.
	 * Seek the position corresponding to specified time.
	 *
	 * @param time time to seek in seconds
	 */
	public synchronized void seek(double time) {

		try {
			file.seekTime(time);
		} catch (AudioFileException cause) {
			throw new ModuleCreationException(cause);
		}
	}

	/**
	 * Updates the view, if there is one.
	 */
	protected void updateView() {

		if (playerView != null) {
			invokeLater(() -> playerView.updateTime());
		}
	}
}
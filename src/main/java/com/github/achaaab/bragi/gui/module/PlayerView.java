package com.github.achaaab.bragi.gui.module;

import com.github.achaaab.bragi.core.module.player.Player;
import com.github.achaaab.bragi.gui.component.TimeSlider;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import java.awt.Dimension;
import java.awt.FlowLayout;

import static com.github.achaaab.bragi.gui.common.ViewScale.scale;
import static java.awt.FlowLayout.LEFT;
import static java.lang.ClassLoader.getSystemResource;
import static java.lang.Math.round;
import static java.time.Duration.ofSeconds;

/**
 * player Swing view
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.0
 */
public class PlayerView extends JPanel {

	private static final Dimension SLIDERS_SIZE = scale(new Dimension(250, 50));

	private static final Icon PLAY_ICON;
	private static final Icon PAUSE_ICON;
	private static final Icon STOP_ICON;

	static {

		PLAY_ICON = new ImageIcon(getSystemResource("icons/play.png"));
		PAUSE_ICON = new ImageIcon(getSystemResource("icons/pause.png"));
		STOP_ICON = new ImageIcon(getSystemResource("icons/stop.png"));
	}

	/**
	 * @param time time in seconds (s)
	 * @return formatted time
	 */
	private static String formatTime(double time) {

		var duration = ofSeconds(round(time));

		var hours = duration.toHours();
		var minutes = duration.toMinutesPart();
		var seconds = duration.toSecondsPart();

		return hours +
				(minutes < 10 ? ":0" : ":") + minutes +
				(seconds < 10 ? ":0" : ":") + seconds;
	}

	private final Player model;
	private final JLabel timeLabel;
	private final TimeSlider timeSlider;

	private boolean modelChange;

	/**
	 * @param model player model
	 */
	public PlayerView(Player model) {

		super(new FlowLayout(LEFT));

		this.model = model;

		var playButton = new JButton(PLAY_ICON);
		var pauseButton = new JButton(PAUSE_ICON);
		var stopButton = new JButton(STOP_ICON);
		timeLabel = new JLabel();
		timeSlider = new TimeSlider();

		updateTimeLabel(model.getTime(), model.getDuration());

		timeSlider.setPreferredSize(SLIDERS_SIZE);

		playButton.addActionListener(event -> model.play());
		pauseButton.addActionListener(event -> model.pause());
		stopButton.addActionListener(event -> model.stop());

		modelChange = false;
		timeSlider.addChangeListener(this::timeChanged);

		add(stopButton);
		add(playButton);
		add(pauseButton);
		add(timeLabel);
		add(timeSlider);
	}

	/**
	 * This method is designed to be registered as the listener callback for the time slider.
	 * It the {@link #modelChange} flag is set, does nothing.
	 * If the the value of {@link #timeSlider} is adjusting, does nothing.
	 * Otherwise, calls the model to seek the new time position.
	 *
	 * @param event time changed event
	 * @since 0.1.6
	 */
	private void timeChanged(ChangeEvent event) {

		if (!modelChange) {

			if (timeSlider.getValueIsAdjusting()) {

				var time = timeSlider.getDecimalValue();
				var duration = model.getDuration();

				updateTimeLabel(time, duration);

			} else {

				model.seek(timeSlider.getDecimalValue());
			}
		}
	}

	/**
	 * Updates the time label with new time and duration.
	 *
	 * @param time     time in seconds (s)
	 * @param duration duration in seconds (s)
	 */
	private void updateTimeLabel(double time, double duration) {

		var formattedTime = formatTime(time);
		var formattedDuration = formatTime(duration);

		timeLabel.setText(formattedTime + " / " + formattedDuration);
	}

	/**
	 * This method is designed to be called by the model. To avoid infinite event loop, we set
	 * a flag to indicate that this update comes from the model, which is already up to date.
	 * Does nothing if the value if the time slider is adjusting (to let the user seek a time position).
	 *
	 * @since 0.1.6
	 */
	public void updateTime() {

		if (!timeSlider.getValueIsAdjusting()) {

			var time = model.getTime();
			var duration = model.getDuration();

			updateTimeLabel(time, duration);

			modelChange = true;
			timeSlider.setMaximal(duration);
			timeSlider.setDecimalValue(time);
			modelChange = false;
		}
	}
}
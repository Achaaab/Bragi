package com.github.achaaab.bragi.gui.module;

import com.github.achaaab.bragi.gui.component.TimeSlider;
import com.github.achaaab.bragi.module.Player;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

/**
 * player Swing view
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.0
 */
public class PlayerView extends JPanel {

	private TimeSlider timeSlider;

	/**
	 * @param model player model
	 */
	public PlayerView(Player model) {

		var play = new JButton("Play");
		var pause = new JButton("Pause");
		var stop = new JButton("Stop");
		timeSlider = new TimeSlider();

		play.addActionListener(event -> model.play());
		pause.addActionListener(event -> model.pause());
		stop.addActionListener(event -> model.stop());
		timeSlider.setEnabled(false);
		//timeSlider.addChangeListener(event -> model.seek(timeSlider.getDecimalValue()));

		add(stop);
		add(play);
		add(pause);
		add(timeSlider);

		var frame = new JFrame(model.getName());
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.setContentPane(this);
		frame.pack();
		frame.setVisible(true);
	}

	/**
	 * @param time current playback time in seconds
	 */
	public void setTime(double time) {
		timeSlider.setDecimalValue(time);
	}
}
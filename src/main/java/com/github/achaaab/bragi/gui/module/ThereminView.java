package com.github.achaaab.bragi.gui.module;

import com.github.achaaab.bragi.core.module.producer.Theremin;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import static com.github.achaaab.bragi.gui.common.ViewScale.scale;

/**
 * theremin Swing view
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.4
 */
public class ThereminView extends JPanel {

	private static final Dimension SIZE = scale(new Dimension(300, 300));
	private static final Color BACKGROUND_COLOR = new Color(64, 64, 64);

	private static final float MINIMAL_PITCH = -2.5f;
	private static final float MAXIMAL_PITCH = 2.5f;
	private static final float PITCH_AMPLITUDE = MAXIMAL_PITCH - MINIMAL_PITCH;

	private static final float MINIMAL_VOLUME = -3.0f;
	private static final float MAXIMAL_VOLUME = 0.0f;
	private static final float VOLUME_AMPLITUDE = MAXIMAL_VOLUME - MINIMAL_VOLUME;

	private final Theremin model;

	/**
	 * @param model model
	 */
	public ThereminView(Theremin model) {

		this.model = model;

		setPreferredSize(SIZE);
		setBackground(BACKGROUND_COLOR);

		addMouseMotionListener(new MouseMotionAdapter() {

			@Override
			public void mouseMoved(MouseEvent mouseEvent) {
				setPitchAndVolume(mouseEvent);
			}
		});

		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseEntered(MouseEvent mouseEvent) {
				setPitchAndVolume(mouseEvent);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				model.setVolumeSample(MINIMAL_VOLUME);
			}
		});
	}

	/**
	 * Set pitch and volume according to mouse cursor position.
	 *
	 * @param mouseEvent mouse event
	 */
	private void setPitchAndVolume(MouseEvent mouseEvent) {

		var x = mouseEvent.getX();
		var y = mouseEvent.getY();

		model.setPitch(getPitch(x));
		model.setVolumeSample(getVolume(y));
	}

	/**
	 * @param x horizontal position of the mouse cursor on the theremin view
	 * @return corresponding pitch in volts
	 */
	private float getPitch(float x) {
		return MINIMAL_PITCH + PITCH_AMPLITUDE * (x / getWidth());
	}

	/**
	 * @param y vertical position of the mouse cursor on the theremin view
	 * @return corresponding volume in volts
	 */
	private float getVolume(float y) {
		return MAXIMAL_VOLUME - VOLUME_AMPLITUDE * (y / getHeight());
	}
}
package com.github.achaaab.bragi.gui.component;

import com.github.achaaab.bragi.gui.common.ViewScale;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.LinearGradientPaint;

public class KeyButton extends JComponent {

	public static void main(String[] args) {

		JFrame window = new JFrame("test");
		var panel = new JPanel();
		panel.setLayout(new GridLayout(1, 4));
		panel.add(new KeyButton());
		panel.add(new KeyButton());
		panel.add(new KeyButton());
		panel.add(new KeyButton());
		window.setContentPane(panel);
		window.pack();
		window.setVisible(true);
	}

	public KeyButton()  {
		setPreferredSize(ViewScale.scale(new Dimension(128, 512)));
	}

	public void paint(Graphics graphics) {

		var graphics2d = (Graphics2D) graphics;

		graphics2d.setColor(new Color(220, 220, 220));
		graphics2d.fillRect(0, 0, 128, 512);

		var leftLight = new LinearGradientPaint(0, 0, 20, 0,
				new float[] { 0.0f, 1.0f},
				new Color[] { new Color(255, 255, 255, 255), new Color(255, 255, 255, 0)});

		graphics2d.setPaint(leftLight);
		graphics2d.fillRect(0, 0, 20, 512);

		var topLight = new LinearGradientPaint(0, 0, 0, 20,
				new float[] { 0.0f, 1.0f},
				new Color[] { new Color(255, 255, 255, 255), new Color(255, 255, 255, 0)});

		graphics2d.setPaint(topLight);
		graphics2d.fillRect(0, 0, 128, 20);

		var rightShadow = new LinearGradientPaint(107, 0, 127, 0,
				new float[] { 0.0f, 1.0f},
				new Color[] { new Color(96, 96, 96, 0), new Color(96, 96, 96, 255)});

		graphics2d.setPaint(rightShadow);
		graphics2d.fillRect(107, 0, 20, 512);

		var bottomShadow = new LinearGradientPaint(0, 491, 0, 511,
				new float[] { 0.0f, 1.0f},
				new Color[] { new Color(96, 96, 96, 0), new Color(96, 96, 96, 255)});

		graphics2d.setPaint(bottomShadow);
		graphics2d.fillRect(0, 491, 128, 20);

		var leftShadow = new LinearGradientPaint(0, 0, 5, 0,
				new float[] { 0.0f, 1.0f},
				new Color[] { new Color(96, 96, 96, 255), new Color(96, 96, 96, 0)});

		graphics2d.setPaint(leftShadow);
		graphics2d.fillRect(0, 0, 5, 512);

		var topShadow = new LinearGradientPaint(0, 0, 0, 5,
				new float[] { 0.0f, 1.0f},
				new Color[] { new Color(96, 96, 96, 255), new Color(96, 96, 96, 0)});

		graphics2d.setPaint(topShadow);
		graphics2d.fillRect(0, 0, 128, 5);
	}
}

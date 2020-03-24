package fr.guehenneux.bragi.util;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import static java.awt.BorderLayout.CENTER;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class KeyCodeUtil {

	public static void main(String[] args) {

		var frame = new JFrame("Key code displayer");
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		var panel = new JPanel();
		frame.add(panel, CENTER);
		var keyCodeLabel = new JLabel();
		var extendedKeyCodeLabel = new JLabel();
		var keyCharLabel = new JLabel();
		panel.setLayout(new GridLayout(3, 1));
		panel.add(keyCodeLabel);
		panel.add(extendedKeyCodeLabel);
		panel.add(keyCharLabel);
		frame.pack();
		frame.setVisible(true);

		frame.setFocusTraversalKeysEnabled(false);

		frame.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent keyEvent) {

				panel.setBackground(Color.GREEN);
				keyCodeLabel.setText("key code: " + keyEvent.getKeyCode());
				extendedKeyCodeLabel.setText("extended key code: " + keyEvent.getExtendedKeyCode());
				keyCharLabel.setText("key char: [" + keyEvent.getKeyChar() + "]");
			}

			@Override
			public void keyReleased(KeyEvent keyEvent) {

				panel.setBackground(Color.WHITE);
				keyCodeLabel.setText("key code: " + keyEvent.getKeyCode());
				extendedKeyCodeLabel.setText("extended key code: " + keyEvent.getExtendedKeyCode());
				keyCharLabel.setText("key char: [" + keyEvent.getKeyChar() + "]");
			}
		});
	}
}

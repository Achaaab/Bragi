package com.github.achaaab.bragi.mml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.SOUTH;
import static java.awt.Color.GREEN;
import static java.lang.ClassLoader.getSystemResource;
import static javax.swing.text.StyleContext.getDefaultStyleContext;

/**
 * @author Jonathan Guéhenneux
 * @since 0.1.8
 */
public class MmlPlayerView extends JPanel {

	private static final Logger LOGGER = LoggerFactory.getLogger(MmlPlayerView.class);

	private static final String DEFAULT_MML = """
			l8<b->c+f4d+c+d+f4fd+c+cc+4<a+4>fd+c+cc+4<a+4g+a+>c+2&c+<b->c+f4d+c+d+f4fg+fd+f4f2.&fa2.<aa+4.a+>c+f4d+
			c+d+f4fd+c+cc+4<a+4>fd+c+cc+4<a+4g+a+>c+2&c+<b->c+f4d+c+d+f4fg+fd+f4f2.&f<a1a+2.&a+.&a+64
			""";

	private static final String OBSOLETE_COMMAND_MESSAGE = "MML source does not contain the command \"{}\" anymore.";

	private static final String CURRENT_COMMAND_STYLE_NAME = "current_command";
	private static final Style DEFAULT_STYLE = getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);

	private static final Icon PLAY_ICON;
	private static final Icon STOP_ICON;

	static {

		PLAY_ICON = new ImageIcon(getSystemResource("icons/play.png"));
		STOP_ICON = new ImageIcon(getSystemResource("icons/stop.png"));
	}

	private final MmlPlayer model;
	private final StyledDocument mmlDocument;
	private final Style currentCommandStyle;

	private Command previousCommand;

	/**
	 * Creates a MML player view.
	 *
	 * @param model MML player
	 */
	public MmlPlayerView(MmlPlayer model) {

		this.model = model;

		var mmlPane = new JTextPane();
		mmlDocument = mmlPane.getStyledDocument();

		currentCommandStyle = mmlDocument.addStyle(CURRENT_COMMAND_STYLE_NAME, DEFAULT_STYLE);
		StyleConstants.setBackground(currentCommandStyle, GREEN);

		try {
			mmlDocument.insertString(0, DEFAULT_MML, DEFAULT_STYLE);
		} catch (BadLocationException cause) {
			throw new MmlException(cause);
		}

		var buttonsPanel = new JPanel();

		var playButton = new JButton(PLAY_ICON);
		var stopButton = new JButton(STOP_ICON);

		playButton.addActionListener(this::play);
		stopButton.addActionListener(event -> model.end());

		buttonsPanel.add(playButton);
		buttonsPanel.add(stopButton);

		var layout = new BorderLayout();
		setLayout(layout);

		add(new JScrollPane(mmlPane), CENTER);
		add(buttonsPanel, SOUTH);

		previousCommand = null;
	}

	/**
	 * @param currentCommand command currently being executed
	 */
	public void showCurrentCommand(Command currentCommand) {

		if (previousCommand != null) {

			var previousStart = previousCommand.getStart();
			var previousEnd = previousCommand.getEnd();
			var previousLength = previousEnd - previousStart;

			mmlDocument.setCharacterAttributes(previousStart, previousLength, DEFAULT_STYLE, true);
		}

		var start = currentCommand.getStart();
		var end = currentCommand.getEnd();
		var length = end - start;
		var commandString = currentCommand.getString();

		if (end < mmlDocument.getLength()) {

			try {

				var string = mmlDocument.getText(start, length);

				if (string.equals(commandString)) {
					mmlDocument.setCharacterAttributes(start, length, currentCommandStyle, true);
				} else {
					LOGGER.debug(OBSOLETE_COMMAND_MESSAGE, commandString);
				}

			} catch (BadLocationException badLocation) {
				LOGGER.debug(OBSOLETE_COMMAND_MESSAGE, commandString);
			}
		}

		previousCommand = currentCommand;
	}

	/**
	 * @param event play button event
	 */
	private void play(ActionEvent event) {
		new Thread(() -> model.play(mml())).start();
	}

	/**
	 * @return MML text
	 */
	private String mml() {

		try {

			var length = mmlDocument.getLength();
			return mmlDocument.getText(0, length);

		} catch (BadLocationException cause) {

			throw new MmlException(cause);
		}
	}
}
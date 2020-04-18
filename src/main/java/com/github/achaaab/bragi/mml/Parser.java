package com.github.achaaab.bragi.mml;

import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Character.toUpperCase;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Music Macro Language parser
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.8
 */
public class Parser {

	private static final Logger LOGGER = getLogger(Parser.class);

	private final String mml;

	private int position;

	/**
	 * @param mml MML to parse
	 */
	public Parser(String mml) {

		this.mml = mml;

		position = 0;
	}

	/**
	 * @return next command, or {@code null} if there is no more command
	 */
	public Command nextCommand() {

		Command command;

		skipWhiteSpaces();

		var start = position;

		if (end()) {

			command = new End();

		} else {

			var character = toUpperCase(read());

			command = switch (character) {

				case 'A', 'B', 'C', 'D', 'E', 'F', 'G' -> readPlay(character);
				case 'R' -> readRest();
				case '<' -> new ShiftDown();
				case '>' -> new ShiftUp();
				case 'O' -> new Octave(readInteger());
				case 'T' -> new Tempo(readInteger());
				case 'L' -> new SetLength(readLength());
				case 'V' -> new Volume(readInteger());

				default -> throw new MmlException("unexpected character: " + character +
						" at position " + position + context(position - 1));
			};
		}

		var end = position;

		command.setStart(start);
		command.setEnd(end);
		command.setString(mml.substring(start, end));

		return command;
	}

	/**
	 * @param center center position
	 * @return context around the given center position
	 */
	private String context(int center) {

		var before = max(0, center - 10);
		var after = min(mml.length(), center + 10);

		return "\n" + mml.substring(before, after) + "\n" + " ".repeat(center - before) + "^\n";
	}

	/**
	 * Increments the current position.
	 */
	private void increment() {
		position++;
	}

	/**
	 * @return current character
	 */
	private char character() {
		return mml.charAt(position);
	}

	/**
	 * @return reads the current character and increments the position
	 */
	private char read() {

		var character = character();
		increment();
		return character;
	}

	/**
	 * Reads the next characters and throws a {@link MmlException} if they don't form the given {@code string}.
	 * This operation is not case sensitive.
	 *
	 * @param expectedString expected string
	 */
	private void read(String expectedString) {
		expectedString.chars().forEach(this::read);
	}

	/**
	 * Reads the current character and throws a {@link MmlException} if it is not the given {@code expectedCharacter}.
	 *
	 * @param expectedCharacter expected character
	 */
	private void read(int expectedCharacter) {

		var character = toUpperCase(read());

		if (character != expectedCharacter) {

			throw new MmlException("unexpected character: " + character + " at position " + position +
					", was expecting: " + expectedCharacter + context(position - 1));
		}
	}

	/**
	 * @param baseNote base note (without modifier)
	 * @return command to play the note with read fractions
	 */
	private Play readPlay(char baseNote) {

		var note = baseNote + readModifier();
		return new Play(note, readLengths(note));
	}

	/**
	 * @return rest command with read fractions
	 */
	private Rest readRest() {
		return new Rest(readLengths("R"));
	}

	/**
	 * @param note note with modifier
	 * @return note lengths as fractions of a whole note (dotted or not)
	 */
	private List<Length> readLengths(String note) {

		var fractions = new ArrayList<Length>();

		fractions.add(readLength());

		while (character() == '&') {

			increment();
			read(note);
			fractions.add(readLength());
		}

		return fractions;
	}

	/**
	 * @return note fraction as a fraction of a whole note (dotted or not)
	 */
	private Length readLength() {

		var length = readInteger();
		var dotted = false;

		if (character() == '.') {

			increment();
			dotted = true;
		}

		return new Length(length, dotted);
	}

	/**
	 * Reads characters while they are digits and build an integer from them.
	 *
	 * @return read integer
	 */
	private int readInteger() {

		var integer = 0;

		while (digit()) {

			integer = 10 * integer + character() - '0';
			increment();
		}

		return integer;
	}

	/**
	 * Reads a note modifier.
	 *
	 * @return read note modifier, empty string if there is no note modifier
	 */
	private String readModifier() {

		var modifier = "";
		var character = character();

		if (character == '-' || character == '+' || character == '#') {

			modifier = Character.toString(character);
			increment();
		}

		return modifier;
	}

	/**
	 * @return whether the MML is fully parsed
	 */
	private boolean end() {
		return position == mml.length();
	}

	/**
	 * @return whether the current character is a whitespace
	 */
	private boolean whitespace() {
		return Character.isWhitespace(character());
	}

	/**
	 * @return whether the current character is a digit
	 */
	private boolean digit() {

		var character = character();
		return character >= '0' && character <= '9';
	}

	/**
	 * Skips all whitespaces.
	 */
	private void skipWhiteSpaces() {

		while (!end() && whitespace()) {
			increment();
		}
	}
}
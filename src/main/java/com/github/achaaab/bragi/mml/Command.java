package com.github.achaaab.bragi.mml;

/**
 * Music Macro Language command
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.8
 */
public interface Command {

	/**
	 * Executes this command.
	 *
	 * @param player player on which to execute this command
	 */
	void execute(MmlPlayer player);

	/**
	 * @return start position (included) of this command
	 */
	int getStart();

	/**
	 * @param start start position (included) of this command
	 */
	void setStart(int start);

	/**
	 * @return end position (excluded) of this command
	 */
	int getEnd();

	/**
	 * @param end end position (excluded) of this command
	 */
	void setEnd(int end);

	/**
	 * @return string source of this command
	 */
	String getString();

	/**
	 * @param string string source of this command
	 */
	void setString(String string);
}
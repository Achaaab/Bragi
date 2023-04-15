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
	 * @since 0.2.0
	 */
	void execute(MmlPlayer player);

	/**
	 * @return start position (included) of this command
	 * @since 0.2.0
	 */
	int getStart();

	/**
	 * @param start start position (included) of this command
	 * @since 0.2.0
	 */
	void setStart(int start);

	/**
	 * @return end position (excluded) of this command
	 * @since 0.2.0
	 */
	int getEnd();

	/**
	 * @param end end position (excluded) of this command
	 * @since 0.2.0
	 */
	void setEnd(int end);

	/**
	 * @return string source of this command
	 * @since 0.2.0
	 */
	String getString();

	/**
	 * @param string string source of this command
	 * @since 0.2.0
	 */
	void setString(String string);
}

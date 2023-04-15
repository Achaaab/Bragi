package com.github.achaaab.bragi.mml;

/**
 * @author Jonathan Guéhenneux
 * @since 0.1.8
 */
public abstract class AbstractCommand implements Command {

	private int start;
	private int end;
	private String string;

	@Override
	public int getStart() {
		return start;
	}

	@Override
	public void setStart(int start) {
		this.start = start;
	}

	@Override
	public int getEnd() {
		return end;
	}

	@Override
	public void setEnd(int end) {
		this.end = end;
	}

	@Override
	public String getString() {
		return string;
	}

	@Override
	public void setString(String string) {
		this.string = string;
	}
}

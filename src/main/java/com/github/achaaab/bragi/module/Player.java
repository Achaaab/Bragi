package com.github.achaaab.bragi.module;

/**
 * @author Jonathan Guéhenneux
 * @since 0.1.0
 */
public interface Player {

	void play();

	void pause();

	void stop();

	void setTime(double time);
}
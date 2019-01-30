package fr.guehenneux.bragi.module.model;

/**
 * @author Jonathan Guéhenneux
 */
public interface Player {


	void play();

	void pause();

	void stop();

	void setTime(double time);
}
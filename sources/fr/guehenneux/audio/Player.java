package fr.guehenneux.audio;

/**
 * @author Jonathan Guéhenneux
 */
public interface Player {


	void play();

	void pause();

	void stop();

	void setTime(double time);
}
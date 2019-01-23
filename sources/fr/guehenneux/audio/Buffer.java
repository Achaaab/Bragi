package fr.guehenneux.audio;

import java.util.LinkedList;

public class Buffer<Type> {

	private LinkedList<Type> datas;

	private int tailleMaximum;

	public Buffer(int tailleMaximum) {

		datas = new LinkedList<Type>();
		this.tailleMaximum = tailleMaximum;

	}

	/**
	 * 
	 * @return
	 */
	public boolean isEmpty() {
		return datas.isEmpty();
	}

	public synchronized Type get() {

		while (datas.isEmpty()) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}

		Type donnee = datas.removeFirst();

		notify();

		return donnee;

	}

	public synchronized void ajouter(Type donnee) {

		while (datas.size() >= tailleMaximum) {

			try {
				wait();
			} catch (InterruptedException e) {
			}
		}

		datas.addLast(donnee);

		notify();
	}
}
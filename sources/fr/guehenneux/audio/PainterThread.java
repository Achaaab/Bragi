package fr.guehenneux.audio;

import java.awt.Component;

/**
 * 
 * @author GUEHENNEUX
 *
 */
public class PainterThread extends Thread {
    
    private Component composant;
    private int fps;

    /**
     * @param composant
     */
    public PainterThread(Component composant, int fps) {
        this.composant = composant;
        this.fps = fps;
    }
    
    public void run() {
        
        while (true) {
            
            composant.repaint();
            
            try {
                sleep(1000 / fps);
            } catch (InterruptedException e) {
            }
            
        }
        
    }

}

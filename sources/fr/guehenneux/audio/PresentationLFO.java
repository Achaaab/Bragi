package fr.guehenneux.audio;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 * 
 * @author GUEHENNEUX
 *
 */
public class PresentationLFO extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 5223257961962194357L;
    
    private LFO lfo;
    private JSlider frequencySlider;
    
    /**
     * 
     *
     */
    public PresentationLFO(LFO lfo) {
       
        this.lfo = lfo;
        frequencySlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 30);
        frequencySlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                getLfo().setFrequency(0.02 + frequencySlider.getValue() / 5);
            }
        });
        
        add(frequencySlider);
        
        JFrame container = new JFrame("LFO");
        container.setSize(400, 300);
        container.add(this);
        container.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        container.setVisible(true);
        
    }

    
    /**
     * @return lfo
     */
    public LFO getLfo() {
        return lfo;
    }
    
}

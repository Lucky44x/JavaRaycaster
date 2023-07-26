package de.lucky44.raycasting.engine.architecture;

import de.lucky44.raycasting.engine.Const;

import javax.swing.*;

/**
 * The Main Game-Window
 * @author Nick Balischewski
 */
public class RaycasterWindow extends JFrame{
    /**
     * Constructor initializes all window-related options and sets the Windows panel to a specified RayCasterPanel
     * @param raycasterPanel The RaycasterPanel Instance which will be used to handle Scenes and rendering
     */
    public RaycasterWindow(RaycasterPanel raycasterPanel){
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(Const.WIDTH, Const.HEIGHT);
        add(raycasterPanel);
        setVisible(true);
        setName("Lucky-Raycaster v4");
        setTitle("Lucky-Raycaster v4");
    }
}

package de.lucky44.raycasting.engine.scenes;

import de.lucky44.raycasting.engine.architecture.RaycasterPanel;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Parent class for all Scene-Objects
 * @author Nick Balischewski
 */
public abstract class Scene {

    /**
     * The RaycasterPanel Instance which holds this Scene
     */
    public RaycasterPanel parentPanel;

    /**
     * Gets called when the Scene is loaded
     */
    public abstract void OnLoad();

    /**
     * gets called by the TimeManager on every Update tick
     */
    public abstract void Update();

    /**
     * gets called by the TimeManager on every Draw tick
     * @param g the Graphics2D instance to draw on
     */
    public abstract void Draw(Graphics2D g);

    /**
     * Gets called by Input when a key is pressed
     * @param e the Pressed Key Event
     */
    public void OnKeyDown(KeyEvent e){

    }

    /**
     * Gets called by Input when a key is released
     * @param e the Released Key Event
     */
    public void OnKeyUp(KeyEvent e){

    }

    /**
     * Gets called by Input when a key is typed (dunno what that means, but yeah)
     * @param e the Typed Key Event
     */
    public void OnKeyTyped(KeyEvent e){

    }
}

package de.lucky44.raycasting.engine.architecture;

import de.lucky44.raycasting.engine.input.Input;
import de.lucky44.raycasting.engine.scenes.Scene;

import javax.swing.*;
import java.awt.*;

/**
 * The Graphics-Panel for the Game
 * @author Nick Balischewski
 */
public class RaycasterPanel extends JPanel{

    /**
     * The currently loaded Scene
     */
    private Scene loadedScene = null;

    /**
     * The Constructor initializes a new InputManager Instance
     * (Creates Singleton)
     */
    public RaycasterPanel(){
        Input inputManager = new Input();

        addKeyListener(inputManager);
    }

    /**
     * Loads the specified Scene
     * @param scene the Scene to be loaded by the Game
     */
    public void loadScene(Scene scene){

        if(loadedScene != null)
            Input.unregister(loadedScene);

        scene.parentPanel = this;
        loadedScene = scene;

        Input.register(scene);

        scene.OnLoad();
    }

    /**
     * Basically renders a frame
     * @param g the <code>Graphics</code> object to protect
     */
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);

        if(loadedScene == null)
            return;

        loadedScene.Draw((Graphics2D) g);
    }

    /**
     * Updates the currently loaded Scene-Instance
     */
    public void updateScene() {
        if(loadedScene == null)
            return;

        loadedScene.Update();
    }
}

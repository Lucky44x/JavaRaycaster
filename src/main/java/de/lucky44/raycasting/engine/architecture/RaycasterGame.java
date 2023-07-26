package de.lucky44.raycasting.engine.architecture;

import de.lucky44.raycasting.engine.time.Time;
import de.lucky44.raycasting.engine.Const;
import de.lucky44.raycasting.scenes.SinglePlayerScene;

/**
 * The Main-Game class handling timing and Initialization
 * @author Nick Balischewski
 */
public class RaycasterGame{
    /**
     * The Timing-Manager for timing updates and frames
     */
    private final Time timeManager;

    /**
     * Constructor also initializes almost everything else in the Application
     */
    public RaycasterGame(){
        Const.generateConstantString();

        RaycasterPanel panel = new RaycasterPanel();
        new RaycasterWindow(panel);
        panel.loadScene(new SinglePlayerScene(System.getProperty("user.dir") + "/world"));
        panel.requestFocus();

        timeManager = new Time(panel);

        startGameLoop();
    }

    /**
     * Starts the frame and update threads
     */
    public void startGameLoop(){
        Thread gameLoop = new Thread(timeManager);
        gameLoop.start();
    }
}

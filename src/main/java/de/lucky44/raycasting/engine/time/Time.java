package de.lucky44.raycasting.engine.time;

import de.lucky44.raycasting.engine.Const;
import de.lucky44.raycasting.engine.architecture.RaycasterPanel;

/**
 * The time-manager responsible for managing all the update and draw calls
 *
 * Note: this class is fucked beyond belief...
 *
 * @author Nick Balischewski
 */
public class Time implements Runnable{
    /**
     * The Panel Instance which is using this time instance
     */
    private final RaycasterPanel panel;

    /**
     * LIES
     */
    public static int FPS;

    /**
     * MORE LIES
     */
    public static long INTERPOLATED_FPS;

    /**
     * EVEN MORE LIES
     */
    public static int UPS;

    /**
     * behaves a bit weird
     */
    public static double deltaTime;

    /**
     * No lies
     * When false, the threads will try to exit
     */
    public static boolean running = true;

    /**
     * Initializes the Panel instance
     * @param panel the panel which uses this time instance
     */
    public Time(RaycasterPanel panel){
        this.panel = panel;
    }

    /**
     * Gets called when the Thread is running
     */
    @Override
    public void run() {
        //FPS
        double timePerFrame = 1000000000.0 / Const.FPS_CAP;
        int frames = 0;
        double deltaF = 0;

        //UPS
        double timePerUpdate = 1000000000.0 / Const.UPS_CAP;
        int updates = 0;
        double deltaU = 0;

        long lastCheck = System.currentTimeMillis();
        long previousTime = System.nanoTime();

        long previousFrameTime = System.currentTimeMillis();

        while(running){
            long currentTime = System.nanoTime();
            deltaU += (currentTime - previousTime) / timePerUpdate;
            deltaF += (currentTime - previousTime) / timePerFrame;
            previousTime = currentTime;

            if(deltaU >= 1){
                panel.updateScene();
                updates++;
                deltaU--;
            }

            if(deltaF >= 1){
                panel.repaint();
                frames++;
                deltaF--;

                deltaTime = (System.currentTimeMillis() - previousFrameTime) * Const.deltaTimeMultiplier;
                //INTERPOLATED_FPS = 1 / (System.currentTimeMillis() - previousFrameTime);
                previousFrameTime = System.currentTimeMillis();
            }

            if(System.currentTimeMillis() - lastCheck >= 1000){
                lastCheck = System.currentTimeMillis();
                Time.FPS = frames;
                Time.UPS = updates;
                frames = 0;
                updates = 0;
            }
        }

    }
}

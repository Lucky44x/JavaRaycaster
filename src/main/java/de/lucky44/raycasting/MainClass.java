package de.lucky44.raycasting;

import de.lucky44.raycasting.engine.architecture.RaycasterGame;

/**
 * The Main-Class used for initializing EVERYTHING else
 */
public class MainClass {
    /**
     * Gets called by the System, initializes a new RayCasterGame instance and thus starts the Game
     * @param args the arguments passed by the JVM
     */
    public static void main(String[] args){
        new RaycasterGame();
    }
}
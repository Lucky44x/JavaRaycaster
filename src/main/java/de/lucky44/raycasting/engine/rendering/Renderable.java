package de.lucky44.raycasting.engine.rendering;

import java.awt.*;

/**
 * The parent class for every "renderable" Object
 * @author Nick Balischewski
 */
public abstract class Renderable {
    /**
     * The Distance of this Object to the Player/Camera
     */
    public double distanceToPlayer;

    /**
     * Gets called by the renderer when the Object should render itself into the screen-buffer
     * @param rend the Renderer which called this method
     * @param textureManager the TextureManager Instance which cached all the textures
     */
    public abstract void render(Renderer rend, TextureManager textureManager);

    /**
     * Kind of DEBUG method used for displaying information onto the screen outside the main renderer
     * @param g the Graphics2D instance to draw on
     */
    public void renderToMap(Graphics2D g){

    }
}

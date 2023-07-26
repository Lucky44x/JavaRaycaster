package de.lucky44.raycasting.engine.entities;

import de.lucky44.raycasting.engine.math.vec2D;
import de.lucky44.raycasting.rendering.renderables.Sprite;
import de.lucky44.raycasting.engine.scenes.Scene;
import de.lucky44.raycasting.scenes.SinglePlayerScene;
import lombok.Getter;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Simple Entity used to represent non-static Objects like Sprites or NPCs
 * @author Nick Balischewski
 */
public class Entity {
    /**
     * The Name of the Entity
     */
    @Getter
    private String entityName;
    /**
     * The position of the Entity (0-1)
     */
    public vec2D position = new vec2D(0,0);
    /**
     * The SpriteRenderer which renders the Sprite of the Entity
     */
    public Sprite spriteRenderer;

    /**
     * @param entityName The Name of the Entity
     * @param position The Position of the Entity
     * @param renderer The Sprite{@link Sprite} Instance used for rendering
     */
    public Entity(String entityName, vec2D position, Sprite renderer){
        this.position = position.copy();
        this.entityName = entityName;

        this.spriteRenderer = renderer;
    }

    /**
     * Calculates the necessary data for rendering to screen
     * @param startPos the StartPosition of the Entity
     * @param normal the normalized direction to the Player/Camera
     * @param g the Graphics2D Instance to draw on (for DEBUG drawing)
     * @param parent the Scene which this Entity is currently in
     * @param rotation the rotation of the Player/Camera
     */
    public void calculateRender(vec2D startPos, vec2D normal, Graphics2D g, Scene parent, double rotation) {
        spriteRenderer.calculateSprite(position, startPos, normal, g, (SinglePlayerScene) parent, rotation);
    }

    /**
     * Returns all relevant data in a String[]
     * @return relevant data for DEBUG info
     */
    public String[] debugInfo() {
        String[] renderInfo = spriteRenderer.spitOutInfo();
        String[] ret = new String[renderInfo.length+1];
        ret[0] = position.toString();
        System.arraycopy(renderInfo, 0, ret, 1, renderInfo.length);
        return ret;
    }
}

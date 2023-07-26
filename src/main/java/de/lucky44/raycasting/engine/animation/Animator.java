package de.lucky44.raycasting.engine.animation;

import de.lucky44.raycasting.engine.rendering.Renderer;
import de.lucky44.raycasting.engine.time.Time;
import lombok.Getter;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * The Animator Object for displaying Animations on the Screen
 * @author Nick Balischewski
 */
public class Animator {

    //Animation-Object handling
    /**
     * The Animation which is currently playing
     */
    @Getter
    private Animation currentAnimation;
    /**
     * The Array of Animations which this Animator uses
     */
    private Animation[] animations;

    //Animation maths
    /**
     * The time since the last frame
     */
    @Getter
    private double elapsedTime = 0;
    /**
     * The index of the current frame
     */
    @Getter
    private int currentFrame = 0;

    /**
     * @param animations ALL Animations this Animator is going to use
     */
    public Animator(Animation[] animations){
        this.animations = animations;
        switchAnimation(0);
    }

    /**
     * Switches the currently playing Animation
     * @param animationID the index of the animation which should be played
     */
    public void switchAnimation(int animationID){
        currentAnimation = animations[animationID];
        elapsedTime = 0;
        currentFrame = 0;
    }

    /**
     * Renders the Animation through the specified Renderer
     * @param r The Renderer which is going to render to the screen
     * @param screenX X Coordinate of the upper left corner
     * @param screenY Y Coordinate of the upper left corner
     * @param spriteW The Width of the sprite
     * @param spriteH The Height of the sprite
     * @param dist The distance to the "Player"/"Camera"
     */
    public void render(Renderer r, int screenX, int screenY, int spriteW, int spriteH, double dist){
        if(currentAnimation == null)
            return;

        elapsedTime += Time.deltaTime;

        r.drawImage(currentAnimation.frames()[currentFrame], screenX, screenY, spriteW, spriteH, dist);

        if(elapsedTime < currentAnimation.frameTime())
            return;

        elapsedTime = 0;
        currentFrame ++;
        if(currentFrame >= currentAnimation.frames().length)
            currentFrame = 0;
    }

    /**
     * Gets the frame with the index i1 of the animation with index i
     * @param i the index of the animation
     * @param i1 the index of the frame
     * @return the frame{@link BufferedImage} with index i1 of the animation with index i
     */
    public BufferedImage getFrame(int i, int i1) {
        if(animations.length == 0)
            return null;

        return animations[i].frames()[i1];
    }
}

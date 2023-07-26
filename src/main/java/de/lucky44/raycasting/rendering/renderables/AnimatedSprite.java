package de.lucky44.raycasting.rendering.renderables;

import de.lucky44.raycasting.engine.animation.Animator;
import de.lucky44.raycasting.engine.math.vec2D;
import de.lucky44.raycasting.engine.rendering.Renderer;
import de.lucky44.raycasting.engine.rendering.TextureManager;

import java.awt.*;

/**
 * A Sprite but Animated
 * @author Nick Balischewski
 */
public class AnimatedSprite extends Sprite{

    /**
     * The Animator instance animating this Sprite
     */
    private Animator animator;

    /**
     * Initializes all important references
     * @param animator the Animator
     * @param size the size of the Sprite
     * @param yOffset the yOffset of the sprite (moving up and down)
     */
    public AnimatedSprite(Animator animator, vec2D size, int yOffset) {
        super(animator.getFrame(0,0), size, yOffset);
        this.animator = animator;
    }

    /**
     * renders the current frame of the sprite to the buffer
     * @param r the Renderer which called this method
     * @param textureManager the TextureManager Instance which cached all the textures
     */
    @Override
    public void render(Renderer r, TextureManager textureManager){
        if(!onScreen)
            return;

        animator.render(r, (int)screenPos.x, (int)screenPos.y - yOffset, (int)screenSize.x, (int)screenSize.y, distanceToPlayer);
    }

    /**
     * Formats all important DEBUG info
     * @return the DEBUG-info
     */
    @Override
    public String[] spitOutInfo(){
        return new String[]{
                "Sprite Renderer: ANIMATED",
                "ScreenPos: " + screenPos,
                "ScreenSize: " + screenSize,
                "OnScreen: " + onScreen,
                "- Animation -",
                "CurrentAnimation: " + animator.getCurrentAnimation().name(),
                "ElapsedTime: " + animator.getElapsedTime(),
                "CurrentFrame: " + animator.getCurrentFrame()
        };
    }
}

package de.lucky44.raycasting.engine.animation;

import java.awt.image.BufferedImage;

/**
 * Simple immutable data holder for passing animation data
 * @param name The name of the animation
 * @param frames The frames of the animation
 * @param frameTime The time each frame should stay on-screen
 * @author Nick Balischewski
 */
public record Animation (String name, BufferedImage[] frames, double frameTime) {

}

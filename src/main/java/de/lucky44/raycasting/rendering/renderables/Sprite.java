package de.lucky44.raycasting.rendering.renderables;

import de.lucky44.raycasting.engine.Const;
import de.lucky44.raycasting.engine.math.vec2D;
import de.lucky44.raycasting.engine.rendering.Renderable;
import de.lucky44.raycasting.engine.rendering.Renderer;
import de.lucky44.raycasting.engine.rendering.TextureManager;
import de.lucky44.raycasting.scenes.SinglePlayerScene;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * A normal Sprite
 * @author Nick Balischewski
 */
public class Sprite extends Renderable {

    /**
     * The Screen Position of the Sprite
     */
    protected final vec2D screenPos;
    /**
     * The Screen size of the Sprite
     */
    protected final vec2D screenSize;

    /**
     * The original/start Size of the Sprite
     */
    protected final vec2D originalSize;
    /**
     * The y Offset of the sprite
     */
    protected final int yOffset;
    /**
     * is the Sprite even on the screen?
     */
    protected boolean onScreen = false;

    /**
     * the Sprite-texture
     */
    private BufferedImage sprite;

    /**
     * Initializes the Sprite Instance
     * @param sprite the textures
     * @param size the size
     * @param yOffset the yOffset
     */
    public Sprite(BufferedImage sprite, vec2D size, int yOffset){
        this.sprite = sprite;
        this.originalSize = size;
        this.yOffset = yOffset;

        screenSize = new vec2D(45,45);
        screenPos = new vec2D(0,0);
    }

    /**
     * Renders the Sprite to the screen
     * @param r the Renderer which called this method
     * @param textureManager the TextureManager Instance which cached all the textures
     */
    @Override
    public void render(Renderer r, TextureManager textureManager) {

        if(!onScreen)
            return;

        if(screenPos.x - Const.VIEWPORT_WIDTH < 0 || screenPos.x - Const.VIEWPORT_WIDTH > Const.VIEWPORT_WIDTH)
            return;

        if(screenPos.y < 0 || screenPos.y > Const.VIEWPORT_HEIGHT)
            return;

        if(screenSize.x > Const.VIEWPORT_WIDTH || screenSize.x < 0 || screenSize.y > Const.VIEWPORT_HEIGHT || screenSize.y < 0)
            return;

        r.drawImage(sprite, (int)screenPos.x, (int)screenPos.y - yOffset, (int)screenSize.x, (int)screenSize.y, distanceToPlayer);
    }

    /**
     * Calculates all the data the Sprite needs to be rendered to the Screen
     * @param entityPos the position of the Entity/Sprite
     * @param startPos the Player/Camera Position
     * @param viewDirection the Direction the Player/Camera is facing (vectorized)
     * @param g the Graphics2D instance to draw on
     * @param parent the Scene
     * @param rotation the rotation of the Player
     */
    public void calculateSprite(vec2D entityPos, vec2D startPos, vec2D viewDirection, Graphics2D g, SinglePlayerScene parent, double rotation){

        //first we must determine if the sprite is even on-screen

        onScreen = true;

        double step = (double) (Const.FOV) / Const.RAYCASTER_RESOLUTION;
        double rAngle = rotation - Const.DR * (step * ((double) Const.RAYCASTER_RESOLUTION / 2));
        vec2D dirVector = new vec2D(Math.cos(rAngle), Math.sin(rAngle));
        dirVector.normalize();

        vec2D direction = entityPos.copy().subtract(startPos);
        distanceToPlayer = direction.magnitude();

        /*
        g.setPaint(Color.green);
        g.drawLine((int) (startPos.x * parent.CELL_WIDTH), (int) (startPos.y * parent.CELL_HEIGHT), (int) ((startPos.x + dirVector.x) * parent.CELL_WIDTH), (int) ((startPos.y + dirVector.y) * parent.CELL_HEIGHT));
        g.setPaint(Color.ORANGE);
        g.drawLine((int)(startPos.x * parent.CELL_WIDTH), (int)(startPos.y * parent.CELL_HEIGHT), (int)((startPos.x + direction.x) * parent.CELL_WIDTH), (int) ((startPos.y + direction.y) * parent.CELL_HEIGHT));
        */

        //calculate angle between the players view direction and the angle from the player to the sprite
        direction.normalize();
        double angle = dirVector.angleBetween(direction);

        double dot = dirVector.x * -direction.y + dirVector.y * direction.x;

        //if the previously calculated angle is not within the FOV, don't draw the sprite
        if(dot > 0 || Math.toDegrees(angle) > (double) (Const.FOV)){
            onScreen = false;
            return;
        }

        screenSize.set(originalSize.copy().divide(distanceToPlayer/ parent.MAP_CELL_RESOLUTION));

        double FOVDiv = Math.toDegrees(angle) / (Const.FOV);

        //with the previously calculated angle we can also get the Sprites Screen-X position (generalized: by dividing the angle with our FOV we can get a value from 0 to one)
        screenPos.x = Const.VIEWPORT_WIDTH + (Const.VIEWPORT_WIDTH * FOVDiv) - (screenSize.x/2);
        screenPos.y = (double) Const.HEIGHT / 2 - screenSize.y / 2 + screenSize.y / 10;

        //System.out.println("Angle: " + angle + " toDEG: " + Math.toDegrees(angle) + " DOT: " + dot + "ScreenPos: " + screenPos);

        /*
        OLD

        double angleToView = direction.angleBetween(viewDirection);

        onScreen = true;

        int mult = -1;
        double dot = viewDirection.x * -direction.y + viewDirection.y * direction.x;

        if(dot < 0)
            mult = 1;


        screenSize.set(originalSize.copy().divide(distanceToPlayer/ parent.MAP_CELL_RESOLUTION));

        double screenX = Const.VIEWPORT_WIDTH + ((double) Const.VIEWPORT_WIDTH /2);

        screenX += mult * ((double) Const.VIEWPORT_WIDTH / 2)  * ( angleToView * (4 * Math.pow(angleToView, 2) + 1)); //Nicht hinterfragen, einfach n graph der mir eingefallen ist (y = -4x^2 + 1)

        if(screenX > Const.WIDTH || screenX < Const.VIEWPORT_WIDTH){
            onScreen = false;
            return;
        }

        screenPos.x = screenX;
        screenPos.x -= screenSize.x / 2;
        screenPos.y = (double) Const.HEIGHT / 2 - screenSize.y / 2 + screenSize.y / 10;

        /*
        OLDER
        double sX = entityPos.x * parent.CELL_WIDTH - startPos.x * parent.CELL_WIDTH;
        double sY = entityPos.y * parent.CELL_HEIGHT - startPos.y * parent.CELL_HEIGHT;
        double sZ = 5;

        double CS = Math.cos(rotation), SN = Math.sin(rotation);
        double a = sY * CS + sX * SN;
        double b = sX * CS - sY * SN;
        sX = a;
        sY = b;

        screenPos.x = (sX*60/sY) + ((double) Const.VIEWPORT_WIDTH /2);
        screenPos.y = (sZ * 60/sY) + ((double) Const.VIEWPORT_HEIGHT /2);

        screenSize.set(originalSize.copy().divide(distanceToPlayer));
         */
    }

    /**
     * @return all relevant DEBUG-info in readable form
     */
    public String[] spitOutInfo(){
        return new String[]{
                "Sprite Renderer: STATIC",
                "ScreenPos: " + screenPos,
                "ScreenSize: " + screenSize,
                "OnScreen: " + onScreen
        };
    }
}

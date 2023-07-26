package de.lucky44.raycasting.engine.rendering;

import de.lucky44.raycasting.engine.Const;
import de.lucky44.raycasting.scenes.SinglePlayerScene;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;

/**
 * The Renderer class, which draws every changed pixel into a BufferedImage and the end of each frame, draws that image on screen
 * @author Nick Balischewski
 */
public class Renderer {
    /**
     * Screen Buffer
     */
    private final BufferedImage screenBuffer;
    /**
     * Cached Graphics Instance ? I really don't know anymore
     */
    private final Graphics2D bufferG;
    /**
     * The Depth-Buffer for keeping track of the Depth of each Pixel
     */
    private final double[][] depthBuffer;
    /**
     * A SinglePlayerScene{@link SinglePlayerScene} Instance (Terrible practice, as this is inside the engine package and the SinglePlayerScene is inside the game package)
     */
    private SinglePlayerScene parent;

    /**
     * Initializes all important references
     * @param scene the SinglePlayerScene instance currently in use
     */
    public Renderer(SinglePlayerScene scene){
        this.parent = scene;
        screenBuffer = new BufferedImage(Const.VIEWPORT_WIDTH, Const.VIEWPORT_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        bufferG = screenBuffer.createGraphics();
        depthBuffer = new double[Const.VIEWPORT_WIDTH][Const.VIEWPORT_HEIGHT];
    }

    /**
     * Fills a rect on the screen buffer
     * @param startX the X coordinate of the upper left corner of the rect
     * @param startY the Y coordinate of the upper left corner of the rect
     * @param w the width of the rect
     * @param h the height of the rect
     * @param argb the argb color of the rect
     * @param dist the distance of the rect to the player
     */
    public void fillRect(int startX, int startY, int w, int h, int argb, double dist){
        for(int x = startX; x < startX + w; x++){
            for(int y = startY; y < startY + h; y++){
                drawPixel(x,y,argb,dist);
            }
        }
    }

    /**
     * Fills an infinitely far away rect on the screen buffer
     * @param startX the X coordinate of the upper left corner of the rect
     * @param startY the Y coordinate of the upper left corner of the rect
     * @param w the width of the rect
     * @param h the height of the rect
     * @param argb the argb color of the rect
     */
    public void fillRect(int startX, int startY, int w, int h, int argb){
        for(int x = startX; x < startX + w; x++){
            for(int y = startY; y < startY + h; y++){
                drawPixel(x,y,argb);
            }
        }
    }

    /**
     * Sets a single pixel inside the screen buffer
     * @param x the X coordinate of the pixel
     * @param y the Y coordinate of the pixel
     * @param argb the argb color of the pixel
     * @param dist the depth of the pixel
     */
    public void drawPixel(int x, int y, int argb, double dist){

        if(dist == -1){
            drawPixel(x,y,argb);
            return;
        }

        if(x - Const.VIEWPORT_WIDTH < 0 || x - Const.VIEWPORT_WIDTH >= Const.VIEWPORT_WIDTH)
            return;

        if(y < 0 || y >= Const.VIEWPORT_HEIGHT)
            return;

        if(depthBuffer[x-Const.VIEWPORT_WIDTH][y] <= dist)
            return;

        depthBuffer[x-Const.VIEWPORT_WIDTH][y] = dist;
        screenBuffer.setRGB(x-Const.VIEWPORT_WIDTH,y,argb);
    }

    /**
     * Sets a single, infinitely far away pixel inside the screen buffer
     * @param x the X coordinate of the pixel
     * @param y the Y coordinate of the pixel
     * @param argb the argb color of the pixel
     */
    public void drawPixel(int x, int y, int argb){

        if(x - Const.VIEWPORT_WIDTH < 0 || x - Const.VIEWPORT_WIDTH >= Const.VIEWPORT_WIDTH)
            return;

        if(y < 0 || y >= Const.VIEWPORT_HEIGHT)
            return;

        screenBuffer.setRGB(x-Const.VIEWPORT_WIDTH,y,argb);
    }

    /**
     * flushes/clears the depth buffer
     */
    public void flushBuffers(){
        for(int x = 0; x < Const.VIEWPORT_WIDTH; x ++){
            for(int y = 0; y < Const.VIEWPORT_HEIGHT; y++){
                depthBuffer[x][y] = parent.MAP_CELL_RESOLUTION;
            }
        }
    }

    /**
     * Draws the screen buffer to the Screen
     * @param g the Graphics2D Instance to draw on
     */
    public void render(Graphics2D g){
        g.drawImage(screenBuffer, Const.VIEWPORT_WIDTH, 0, Const.VIEWPORT_WIDTH, Const.VIEWPORT_HEIGHT, null);
    }

    /**
     * Draws the depth buffer in color
     * @param g the Graphics2D Instance to draw on
     */
    public void renderDepthBuffer(Graphics2D g){

        render(g);

        for(int x = 0; x < Const.VIEWPORT_WIDTH-1; x++){
            for(int y = 0; y < Const.VIEWPORT_HEIGHT-1; y++){
                screenBuffer.setRGB(x,y,new Color((float) depthBuffer[x][y]/parent.MAP_CELL_RESOLUTION, (float) depthBuffer[x][y]/parent.MAP_CELL_RESOLUTION, (float) depthBuffer[x][y]/parent.MAP_CELL_RESOLUTION).getRGB());
            }
        }

        g.drawImage(screenBuffer, Const.VIEWPORT_WIDTH, 0, Const.VIEWPORT_WIDTH, Const.VIEWPORT_HEIGHT, null);
    }

    /**
     * Draws an image on the screen buffer
     * @param sprite the Image to draw
     * @param startX the X coordinate of the upper left corner of the image
     * @param startY the Y coordinate of the upper left corner of the image
     * @param w the width of the image
     * @param h the height of the image
     * @param dist the distance from the image to the player/camera
     */
    public void drawImage(BufferedImage sprite, int startX, int startY, int w, int h, double dist) {

        if(startX - Const.VIEWPORT_WIDTH < 0 || startX - Const.VIEWPORT_WIDTH >= Const.VIEWPORT_WIDTH)
            return;

        if(startY < 0 || startY >= Const.VIEWPORT_HEIGHT)
            return;

        if(w > Const.VIEWPORT_WIDTH || w < 0 || h > Const.VIEWPORT_HEIGHT || h < 0)
            return;

        double stepX = (double) (sprite.getWidth()-1) / w;
        double stepY = (double) (sprite.getHeight()-1) / h;

        double yRead = 0;
        double xRead = 0;

        for(int x = startX; x < startX+w; x++){

            yRead = 0;
            if((int)xRead >= sprite.getWidth())
                break;

            for(int y = startY; y < startY+h; y++){

                if((int)yRead >= sprite.getHeight())
                    break;

                //System.out.println((int)xRead + " " + (int)yRead + " -- " + sprite.getWidth() + " " + sprite.getHeight());
                if(new Color(sprite.getRGB((int)xRead, (int)yRead), true).getAlpha() > Const.ALPHA_CLIP){
                    drawPixel(x, y, sprite.getRGB((int)xRead, (int)yRead), dist);
                }
                yRead += stepY;
            }
            xRead += stepX;
        }
    }
}

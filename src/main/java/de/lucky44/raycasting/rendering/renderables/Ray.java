package de.lucky44.raycasting.rendering.renderables;

import de.lucky44.raycasting.engine.Const;
import de.lucky44.raycasting.engine.math.vec2D;
import de.lucky44.raycasting.engine.rendering.Renderable;
import de.lucky44.raycasting.engine.rendering.Renderer;
import de.lucky44.raycasting.engine.rendering.TextureManager;
import de.lucky44.raycasting.engine.scenes.Scene;
import de.lucky44.raycasting.engine.util.Util;
import de.lucky44.raycasting.engine.world.World;
import de.lucky44.raycasting.scenes.SinglePlayerScene;

import java.awt.*;

/**
 * The ray class
 * @author Nick Balischewski
 */
public class Ray extends Renderable {

    /**
     * The index of the ray
     */
    private final int index;

    /**
     * hitX and hitY coordinates of the Ray
     */
    private int hitX, hitY;
    /**
     * StartPosition of the Ray
     */
    private vec2D startPos;
    /**
     * The world the ray was fired in
     */
    private World world;
    /**
     * The scene the world lies in
     */
    private SinglePlayerScene parent;
    /**
     * The color of the ray
     */
    private Color color;
    /**
     * The rotation the ray is rendered at
     */
    private double renderRotation = 0;
    /**
     * The rotation the ray was fired at
     */
    private double rayRotation = 0;
    /**
     * The texture-X coordinate the ray obtained after hit
     */
    private double texX = 0;
    /**
     * The wallTextureId the ray obtained after hit
     */
    private int wallTexID = 0;
    /**
     * The renderType of the Ray
     */
    private int renderType = 0;

    /**
     * the floorX coordinate and floorYCoordinate
     * CURRENTLY: NOT USED
     */
    private double floorWallX, floorWallY;

    /**
     * Initializes all important references
     * @param parent the Scene
     * @param index the index of the ray
     */
    public Ray(Scene parent, int index){
        this.parent = (SinglePlayerScene) parent;
        this.index = index;
    }

    /**
     * Calculates the ray's intersections
     * @param world the world the ray is fired in
     * @param startPos the startPosition of the ray
     * @param dirVector the vectorized direction of the ray
     * @param rotation the rotation/angle from the player at which the ray is fired at
     * @param playerRotation the rotation of the camera/player
     */
    public void calculateRay(World world, vec2D startPos, vec2D dirVector, double rotation, double playerRotation){

        renderType = 0;
        distanceToPlayer = -1;

        if(world == null)
            return;

        rayRotation = rotation;
        renderRotation = playerRotation;

        this.world = world;
        this.startPos = startPos;

        dirVector = dirVector.copy().normalize();

        vec2D vRayUnitStepSize = new vec2D(
                Math.sqrt(1 + (dirVector.y / dirVector.x) * (dirVector.y / dirVector.x)),
                Math.sqrt(1 + (dirVector.x / dirVector.y) * (dirVector.x / dirVector.y))
        );

        vec2D vMapCheck = new vec2D((int)startPos.x, (int)startPos.y);
        vec2D vRayLength1D = vec2D.ZERO.copy();
        vec2D vStep = vec2D.ZERO.copy();

        if(dirVector.x < 0){
            vStep.x = -1;
            vRayLength1D.x = (startPos.x - (float)vMapCheck.x) * vRayUnitStepSize.x;
        }
        else{
            vStep.x = 1;
            vRayLength1D.x = ((float)(vMapCheck.x + 1) - startPos.x) * vRayUnitStepSize.x;
        }

        if(dirVector.y < 0){
            vStep.y = -1;
            vRayLength1D.y = (startPos.y - (float)vMapCheck.y) * vRayUnitStepSize.y;
        }
        else{
            vStep.y = 1;
            vRayLength1D.y = ((float)(vMapCheck.y + 1) - startPos.y) * vRayUnitStepSize.y;
        }

        boolean bTileFound = false;
        double fDistance = 0;
        while(!bTileFound && fDistance < Const.MAX_RAY_LENGTH){
            if(vRayLength1D.x < vRayLength1D.y){
                vMapCheck.x += vStep.x;
                fDistance = vRayLength1D.x;
                vRayLength1D.x += vRayUnitStepSize.x;
                color = Const.WALL_HIGHLIGHT;
            }
            else{
                vMapCheck.y += vStep.y;
                fDistance = vRayLength1D.y;
                vRayLength1D.y += vRayUnitStepSize.y;
                color = Const.WALL_SHADOW;
            }

            if(vMapCheck.x < 0 || vMapCheck.x > world.Width() || vMapCheck.y < 0 || vMapCheck.y > world.Height())
                continue;

            if(world.getCellWall((int)vMapCheck.x, (int)vMapCheck.y) != 0){
                wallTexID = world.getCellWall((int)vMapCheck.x, (int)vMapCheck.y)-1;
                bTileFound = true;
            }
        }

        vec2D vIntersection;
        if(bTileFound){
            vIntersection = startPos.copy().add(dirVector.copy().multiply(fDistance));
            hitX = (int)(vIntersection.x * parent.CELL_WIDTH);
            hitY = (int)(vIntersection.y * parent.CELL_HEIGHT);

            if(color == Const.WALL_HIGHLIGHT){
                texX = (double) (hitY - (int) (vMapCheck.y * parent.CELL_HEIGHT)) / parent.CELL_HEIGHT;
                //System.out.println("Ray texture calclulation: (" + hitX + " - " + vMapCheck.y * parent.CELL_HEIGHT + " = " + (hitY - (int)(vMapCheck.y * parent.CELL_HEIGHT)) + " ) / " + parent.CELL_HEIGHT + " = " + texX);
            }
            else{
                texX = (double) (hitX - (int) (vMapCheck.x * parent.CELL_WIDTH)) / parent.CELL_WIDTH;
                //System.out.println("Ray texture calclulation: (" + hitX + " - " + vMapCheck.x * parent.CELL_WIDTH + " = " + (hitX - (int)(vMapCheck.x * parent.CELL_WIDTH)) + " ) / " + parent.CELL_WIDTH + " = " + texX);
            }
            texX = Math.abs(texX);
            distanceToPlayer = fDistance;
        }

        if(color == Const.WALL_SHADOW && dirVector.x > 0){
            floorWallX = vMapCheck.x;
            floorWallY = vMapCheck.y + hitX;
        }
        else if(color == Const.WALL_SHADOW && dirVector.x < 0){
            floorWallX = vMapCheck.x + 1;
            floorWallY = vMapCheck.y + texX;
        }
        else if(color == Const.WALL_HIGHLIGHT && dirVector.y > 0){
            floorWallX = vMapCheck.x + texX;
            floorWallY = vMapCheck.y;
        }
        else if(color == Const.WALL_HIGHLIGHT && dirVector.y < 0){
            floorWallX = vMapCheck.x + texX;
            floorWallY = vMapCheck.y + 1;
        }

        for(PolyWall wall : world.getPolyWalls()){
            vec2D localIntersection = new vec2D(0,0);
            vec2D lineSegment = wall.intersects(startPos.x, startPos.y, dirVector.x, dirVector.y, localIntersection);
            if(lineSegment.x != -1 && lineSegment.y != -1){
                double polyWallDist = localIntersection.distance(startPos);
                if(polyWallDist <= fDistance){
                    //System.out.println("Found intersection with PolyWall at " + localIntersection);
                    texX = wall.getTextureXCoordinate(localIntersection, lineSegment, parent);
                    hitX = (int)(localIntersection.x * parent.CELL_WIDTH);
                    hitY = (int)(localIntersection.y * parent.CELL_HEIGHT);
                    distanceToPlayer = polyWallDist;
                    renderType = 1;
                    wallTexID = wall.getTexture();
                }
            }
        }
    }

    /**
     * Renders the calculated ray data to the screen in one textureStripe
     * @param r the Renderer which called this method
     * @param textureManager the TextureManager Instance which cached all the textures
     */
    @Override
    public void render(Renderer r, TextureManager textureManager) {
        if(world == null)
            return;

        drawTexWall(r, textureManager);

        /*
        double ca = renderRotation - rayRotation;
        if(ca < 0)
            ca += 2 * Math.PI;
        if(ca > 2*Math.PI)
            ca -= 2*Math.PI;

        distanceToPlayer = distanceToPlayer*Math.cos(ca);

        int height = (int)(Const.HEIGHT/20 * (world.Height() / distanceToPlayer));
        int xPos = (Const.WIDTH / 2) + index * Const.RAYCASTER_LINE_WIDTH;
        int yPos = Const.HEIGHT/2 - height/2;

        g.fillRect(xPos, yPos, Const.RAYCASTER_LINE_WIDTH, height);
        */
    }

    /**
     * Draws the textureStripe of the hit coordinates and wall to the renderers buffer
     * @param r the renderer
     * @param tex the TextureManager which has all the cached textures
     */
    private void drawTexWall(Renderer r, TextureManager tex){
        double ca = renderRotation - rayRotation;
        if(ca < 0)
            ca += 2 * Math.PI;
        if(ca > 2*Math.PI)
            ca -= 2*Math.PI;

        distanceToPlayer = distanceToPlayer*Math.cos(ca);

        int height = (int)(Const.HEIGHT/20 * (world.Height() / distanceToPlayer));

        if(height >  (4d * Const.VIEWPORT_HEIGHT))
            height = (int)(4d * Const.VIEWPORT_HEIGHT);

        if(height < 0)
            return;

        int xPos = (Const.WIDTH / 2) + index * Const.RAYCASTER_LINE_WIDTH;
        int yPos = Const.HEIGHT/2 - height/2;

        for(int x = 0; x < Const.RAYCASTER_LINE_WIDTH; x++){
            int[] wallColors = tex.getTextureStripe(wallTexID, texX, height, renderType == 1 ? 0 : color == Const.WALL_HIGHLIGHT ? 0 : 1);
            int[] fcColors = tex.getFloorStripe(distanceToPlayer, floorWallX, floorWallY, Const.VIEWPORT_HEIGHT - (yPos + height), startPos.x, startPos.y, world);
            //System.out.println("Drawing " + Const.RAYCASTER_LINE_WIDTH + " width * " + colors.length + " height pixels");
            for(int y = 0; y < wallColors.length; y++){
                r.drawPixel(xPos+x, yPos + y, wallColors[y], distanceToPlayer);
            }

             /* Floors be brokey
            for(int y = 0; y < fcColors.length; y++) {
                r.drawPixel(xPos+x, yPos + height + y, fcColors[y]);
            }
              */
        }
    }

    /**
     * Renders the Ray as Debug Info to the map
     * @param g the Graphics2D instance to draw on
     */
    @Override
    public void renderToMap(Graphics2D g){

        if(distanceToPlayer == -1)
            return;

        g.setPaint(Color.green);
        g.drawLine((int)(startPos.x * parent.CELL_WIDTH), (int)(startPos.y * parent.CELL_HEIGHT), hitX, hitY);
    }
}

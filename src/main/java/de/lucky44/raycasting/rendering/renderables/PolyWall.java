package de.lucky44.raycasting.rendering.renderables;

import de.lucky44.raycasting.engine.Const;
import de.lucky44.raycasting.engine.math.vec2D;
import de.lucky44.raycasting.engine.rendering.Renderable;
import de.lucky44.raycasting.engine.rendering.Renderer;
import de.lucky44.raycasting.engine.rendering.TextureManager;
import de.lucky44.raycasting.engine.scenes.Scene;
import de.lucky44.raycasting.scenes.SinglePlayerScene;
import lombok.Getter;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A wall which is not fixed to the world-grid and can be transformed freely
 * @author Nick Balischewski
 */
public class PolyWall extends Renderable {

    /**
     * The list of Points which make up the wall
     */
    private final vec2D[] points;
    /**
     * The texture of the wall
     */
    @Getter
    private final int texture;

    /**
     * The texture offset of the wall
     */
    private final double texOffset;

    /**
     * Should the texture be tiled or stretched
     */
    private final boolean tile;

    /**
     * Initializes the wall
     * @param points the points making up the wall
     * @param texture the texture of the wall
     * @param tile should the texture be tiled or stretched
     * @param texOffset the offset of the texture
     */
    public PolyWall(vec2D[] points, int texture, boolean tile, double texOffset) {
        this.points = points;
        this.texture = texture;
        this.tile = tile;
        this.texOffset = texOffset;
    }

    /**
     * NOOP
     * @param rend the Renderer which called this method
     * @param textureManager the TextureManager Instance which cached all the textures
     */
    @Override
    public void render(Renderer rend, TextureManager textureManager) {

    }

    /**
     * Renders the PolyWall to the DEBUG-MAP
     * @param g the Graphics2D instance to draw on
     * @param scene the Scene in which the PolyWall lies
     */
    public void renderToMap(Graphics2D g, SinglePlayerScene scene){
        g.setColor(Color.black);
        for(int i = 1; i < points.length; i++){
            g.drawLine((int)(points[i-1].x * scene.CELL_WIDTH), (int)(points[i-1].y * scene.CELL_HEIGHT), (int)(points[i].x * scene.CELL_WIDTH), (int)(points[i].y * scene.CELL_HEIGHT));
        }
    }

    /**
     * Calculates the texture Coordinate for the given point on the polyWall
     * @param intersectionPoint the intersection-point
     * @param lineSegment the segment of the polyWall between vertices e.g. vec2D(0,1) for the segment between vertices 0 and 1
     * @param scene the Scene which this PolyWall lies in
     * @return the TextureCoordinate (0-1) to be used for drawing
     */
    public double getTextureXCoordinate(vec2D intersectionPoint, vec2D lineSegment,Scene scene){

        // If not tiling, texture will be stretched over entire wall length

        double interSectDist = points[(int)lineSegment.x].distance(intersectionPoint);
        double wallLength = points[(int)lineSegment.x].distance(points[(int)lineSegment.y]);

        double texCoord = tile ? interSectDist * ((SinglePlayerScene)scene).CELL_WIDTH / ((SinglePlayerScene)scene).CELL_WIDTH : interSectDist / wallLength;

        texCoord += texOffset;

        if(texCoord > 1)
            texCoord -= (int)texCoord;

        if(texCoord < 0)
            texCoord = -texCoord;

        //System.out.println(texCoord);
        return texCoord;
    }

    /*
    startX = x3
    startY = y3
    endX = x4
    endY = y4
     */

    /**
     * Calculates the intersection-point and line-segment of ray-data and this PolyWall
     * @param x3 the StartPosition X of the Ray
     * @param y3 the StartPosition Y of the Ray
     * @param dirX the direction X of the Ray
     * @param dirY the direction Y of the Ray
     * @param intersection_out the vec2D reference which should be updated with the intersection-point data
     * @return the line segment of the hit (the intersection point is put in intersection_out)
     */
    public vec2D intersects(double x3, double y3, double dirX, double dirY, vec2D intersection_out) {

        vec2D intersection = new vec2D(0,0);
        vec2D closestIntersect = null;
        vec2D lineSegment = new vec2D(-1,-1);
        double closestDist = 999999999;

        for(int i = 1; i < points.length; i++) {
            //line segment 1
            double x1 = points[i - 1].x;
            double y1 = points[i - 1].y;
            double x2 = points[i].x;
            double y2 = points[i].y;

            //line segment 2
            double x4 = x3 + dirX * Const.MAX_RAY_LENGTH;
            double y4 = y3 + dirY * Const.MAX_RAY_LENGTH;

            double den = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);

            if(den == 0)
                continue;

            double t = ((x1 - x3) * (y3 - y4) - (y1 - y3) * (x3 - x4)) / den;
            double u = -((x1 -x2) * (y1 - y3) - (y1 - y2) * (x1 -x3)) / den;

            if(t > 0 && t < 1 && u > 0){
                intersection.x = x1 + t * (x2 - x1);
                intersection.y = y1 + t * (y2 - y1);

                if(closestIntersect == null){
                    closestIntersect = new vec2D(intersection.x, intersection.y);
                    closestDist = closestIntersect.distance(x3, y3);
                    lineSegment.x = i-1;
                    lineSegment.y = i;
                    continue;
                }

                if(intersection.distance(x3, y3) <= closestDist){
                    closestIntersect.set(intersection);
                    closestDist = closestIntersect.distance(x3, y3);
                    lineSegment.x = i-1;
                    lineSegment.y = i;
                }
            }
        }

        if(closestIntersect != null){
            intersection_out.set(closestIntersect);
        }

        return lineSegment;
    }

    /**
     * @return all the important info in readable form
     */
    public String[] debugInfo(){
        List<String> debugInfo = new ArrayList();
        for(vec2D p : points){
            debugInfo.add(p.toString());
        }
        return debugInfo.toArray(String[]::new);
    }
}

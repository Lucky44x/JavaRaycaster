package de.lucky44.raycasting.rendering;

import de.lucky44.raycasting.engine.Const;
import de.lucky44.raycasting.engine.entities.Entity;
import de.lucky44.raycasting.engine.math.vec2D;
import de.lucky44.raycasting.engine.rendering.Renderable;
import de.lucky44.raycasting.engine.rendering.Renderer;
import de.lucky44.raycasting.rendering.renderables.Ray;
import de.lucky44.raycasting.engine.scenes.Scene;
import de.lucky44.raycasting.engine.world.World;
import de.lucky44.raycasting.scenes.SinglePlayerScene;
import lombok.Setter;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.*;
import java.util.List;

/**
 * Implementation of the Renderer specifically for Raycasting
 * @author Nick Balischewski
 */
public class RaycasterRenderer extends Renderer {

    /**
     * Ray instances for raycasting (keeps the instances, creating new ones is too heavy on performance)
     */
    private final Ray[] rays;
    /**
     * The World which is supposed to be rendered
     */
    @Setter
    private World world;
    /**
     * The Scene which holds this renderer
     */
    private SinglePlayerScene parent;
    /**
     * The avgRayPerformance to be used by DEBUG
     */
    public long avgPerformance = 0;

    /**
     * All Renderables currently awaiting rendering
     */
    private final List<Renderable> renderables = new ArrayList<>();

    /**
     * Initializes all important references
     * @param parent the Scene which holds this Renderer
     */
    public RaycasterRenderer(Scene parent){
        super((SinglePlayerScene) parent);
        rays = new Ray[Const.RAYCASTER_RESOLUTION];
        for(int i = 0; i < rays.length; i++){
            rays[i] = new Ray(parent, i);
            this.parent = (SinglePlayerScene) parent;
        }
    }

    /**
     * Calculates all rays
     * @param g the Graphics2D instance to be used for DEBUG drawing
     * @param startPos the StartPosition of the Rays
     * @param rotation the rotation of the Camera/Player
     * @param DEBUG_MODE which DEBUG_MODE are we in?
     */
    public void rayCast(Graphics2D g, vec2D startPos, double rotation, int DEBUG_MODE){

        double step = (double) Const.FOV / Const.RAYCASTER_RESOLUTION;
        double rAngle = rotation - Const.DR * step * ((double) Const.RAYCASTER_RESOLUTION /2);
        vec2D dirVector = new vec2D(Math.cos(rAngle), Math.sin(rAngle));
        dirVector.normalize();

        for (Ray ray : rays) {
            long startTime = System.nanoTime();
            ray.calculateRay(world, startPos, dirVector, rAngle, rotation);

            if (DEBUG_MODE == KeyEvent.VK_4)
                ray.renderToMap(g);

            avgPerformance = (avgPerformance + (System.nanoTime() - startTime)) / 2;

            rAngle += Const.DR * step;
            dirVector.x = Math.cos(rAngle);
            dirVector.y = Math.sin(rAngle);
            dirVector.normalize();

            renderables.add(ray);
        }

    }

    /**
     * Clears all renderables
     */
    public void flushRenderCache(){
        renderables.clear();
    }

    /**
     * Calculates all entities which are to be rendered
     * @param g the Graphics2D instance for DEBUG drawing
     * @param startPos the player/camera position
     * @param rotation the player/camera rotation
     * @param normal the normal direction of the Player ?
     */
    public void calculateEntities(Graphics2D g, vec2D startPos, double rotation, vec2D normal){
        if(world == null)
            return;

        for(Entity e : world.getEntities()){
            e.calculateRender(startPos, normal, g, parent, rotation);
            renderables.add(e.spriteRenderer);
        }
    }

    /**
     * Renders a single Frame
     * @param g the Graphics2D Instance to draw on
     */
    @Override
    public void render(Graphics2D g){

        if(world == null)
            return;

        //Draw sky and floor
        fillRect(Const.VIEWPORT_WIDTH, 0, Const.VIEWPORT_WIDTH, Const.VIEWPORT_HEIGHT/2, Const.SKY_COLOR.getRGB());
        fillRect(Const.VIEWPORT_WIDTH, Const.VIEWPORT_HEIGHT/2, Const.VIEWPORT_WIDTH, Const.VIEWPORT_HEIGHT/2, Const.FLOOR_COLOR.getRGB());


        //renderables.sort(Comparator.comparingDouble(o -> o.distanceToPlayer));
        //Collections.reverse(renderables);

        for(Renderable r : renderables){
            r.render(this, world.getTextureManager());
        }

        super.render(g);
    }
}

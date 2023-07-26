package de.lucky44.raycasting.scenes;

import de.lucky44.raycasting.engine.entities.Entity;
import de.lucky44.raycasting.engine.input.Input;
import de.lucky44.raycasting.engine.math.vec2D;
import de.lucky44.raycasting.engine.scenes.Scene;
import de.lucky44.raycasting.engine.time.Time;
import de.lucky44.raycasting.engine.Const;
import de.lucky44.raycasting.engine.world.World;
import de.lucky44.raycasting.rendering.RaycasterRenderer;
import de.lucky44.raycasting.rendering.renderables.PolyWall;
import lombok.Getter;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * The Main-Scene class for this Game
 * @author Nick Balischewski
 */
public class SinglePlayerScene extends Scene {

    /**
     * The world
     */
    private World world;
    /**
     * Cell width and height
     */
    public int CELL_WIDTH = 40, CELL_HEIGHT = 40;
    /**
     * The Map-Cell Resolution
     */
    public int MAP_CELL_RESOLUTION;

    //Player Data
    /**
     * Position of the Player
     */
    @Getter
    private vec2D playerPos = new vec2D(0,0);
    /**
     * Rotation of the player
     */
    @Getter
    private double playerRotation;
    /**
     * Player's rotation but vectorized
     */
    private final vec2D playerDelta = new vec2D(0,0);

    //DEBUG
    /**
     * DEBUG_MODE, used for different debug menus
     */
    private int DEBUG_MODE = 0;
    /**
     * Should the depth Buffer be drawn
     */
    private boolean depthBufferDraw = false;
    /**
     * The path of the worldData
     */
    private String worldPath = "";

    /**
     * The Renderer
     */
    private final RaycasterRenderer renderer = new RaycasterRenderer(this);

    /**
     * Initializes the Scene
     * @param worldPath the path of the worldData
     */
    public SinglePlayerScene(String worldPath){
        this.worldPath = worldPath;
    }

    /**
     * Loads the Scene and loads the World from the path
     */
    @Override
    public void OnLoad() {
        switchWorld(new World(worldPath));
    }

    /**
     * Updates the scene and playerInput
     */
    @Override
    public void Update() {
        playerInput();
    }

    /**
     * Draws/renders the Scene + DEBUG-Draw
     * @param g the Graphics2D instance to draw on
     */
    @Override
    public void Draw(Graphics2D g){

        if(world == null)
            return;

        renderer.flushBuffers();

        //Map Rendering
        renderMap(g);
        renderEntities(g);
        renderPlayer(g);

        renderer.flushRenderCache();

        renderer.rayCast(g, playerPos, playerRotation, DEBUG_MODE);
        renderer.calculateEntities(g, playerPos, playerRotation, playerDelta);

        if(depthBufferDraw)
            renderer.renderDepthBuffer(g);
        else
            renderer.render(g);

        if(DEBUG_MODE != 0)
            renderDebugData(g);
    }

    //region rendering

    /**
     * Renders the Player to the DEBUG-Map
     * @param g the Graphics2D Instance to draw on
     */
    private void renderPlayer(Graphics2D g){

        if(world == null)
            return;

        int playerWorldPosX = (int)(playerPos.x * CELL_WIDTH);
        int playerWorldPosY = (int)(playerPos.y * CELL_HEIGHT);

        g.setPaint(Color.RED);
        g.fillOval(playerWorldPosX - (CELL_WIDTH/4)/2, playerWorldPosY - (CELL_HEIGHT/4)/2, CELL_WIDTH/4, CELL_HEIGHT/4);
        g.drawLine(playerWorldPosX, playerWorldPosY, playerWorldPosX + (int)(playerDelta.x * CELL_WIDTH/2), playerWorldPosY + (int)(playerDelta.y * CELL_HEIGHT/2));
    }

    /**
     * Renders the Entities to the DEBUG-Map
     * @param g the Graphics2D Instance to draw on
     */
    private void renderEntities(Graphics2D g){

        if(world == null)
            return;

        int entityWidth = (CELL_WIDTH/5);
        int entityHeight = (CELL_HEIGHT/5);

        for(Entity e : world.getEntities()){
            int entityWorldPosX = (int)(e.position.x * CELL_WIDTH) + entityWidth/2;
            int entityWorldPosY = (int)(e.position.y * CELL_HEIGHT) + entityHeight/2;

            g.setPaint(Color.CYAN);
            g.fillOval(entityWorldPosX - entityWidth/2, entityWorldPosY - entityHeight/2, entityWidth, entityHeight);

            if(DEBUG_MODE == KeyEvent.VK_3){
                g.drawString(e.getEntityName(), (int)(e.position.x*CELL_WIDTH - e.getEntityName().length()*2), (int)(e.position.y*CELL_HEIGHT - e.getEntityName().length()*1.5));
                g.drawString(e.getClass().getSimpleName(), (int)(e.position.x*CELL_WIDTH - e.getEntityName().length()*2), (int)(e.position.y*CELL_HEIGHT - e.getEntityName().length()/2));
            }
            //g.drawLine(entityWorldPosX, entityWorldPosY, entityWorldPosX + (int)(playerDelta.x * CELL_WIDTH/2), entityWorldPosY + (int)(playerDelta.y * CELL_HEIGHT/2));
        }
    }

    /**
     * Renders the DEBUG-Map
     * @param g the Graphics2D Instance to draw on
     */
    private void renderMap(Graphics2D g){

        if(world == null)
            return;

        for(int x = 0; x < MAP_CELL_RESOLUTION; x++) {
            for(int y = 0; y < MAP_CELL_RESOLUTION; y++){

                g.setPaint(Color.white);
                if(world.getCellWall(x,y) != 0)
                    g.setPaint(Color.black);
                if(DEBUG_MODE != 0 && (int)playerPos.x == x && (int)playerPos.y == y)
                    g.setPaint(Color.green);

                g.fillRect(CELL_WIDTH * x, CELL_HEIGHT * y, CELL_WIDTH, CELL_HEIGHT);

                g.setPaint(Color.gray);
                Rectangle r = new Rectangle(CELL_WIDTH * x, CELL_HEIGHT * y, CELL_WIDTH, CELL_HEIGHT);
                g.setStroke(new BasicStroke(1));
                g.draw(r);
            }
        }

        for(PolyWall p : world.getPolyWalls()){
            if(p == null)
                continue;

            p.renderToMap(g, this);
        }
    }

    /**
     * Renders the different DEBUG-menus
     * @param g the Graphics2D Instance to draw on
     */
    private void renderDebugData(Graphics2D g){
        g.setPaint(new Color(.6f,.6f,.6f, .85f));
        g.fillRect(0,0,CELL_WIDTH * 10,Const.HEIGHT);

        g.setPaint(Color.WHITE);

        g.drawString("FPS: " + Time.FPS, 8, 15);
        g.drawString("UPS: " + Time.UPS, 8, 30);
        g.drawString("DeltaTime: " + Time.deltaTime, 8, 45);

        switch(DEBUG_MODE){
            case(KeyEvent.VK_1) -> {
                g.drawString("--== ENGINE INFO ==--", 8, 75);
                g.drawString("Version: " + Const.VERSION, 8, 90);
                g.drawString("CONSTANTS: ", 8, 105);

                for(int i = 0; i < Const.constantsText.length; i++){
                    g.drawString(Const.constantsText[i], 16, 120 + i*15);
                }
            }
            case(KeyEvent.VK_2) -> {
                g.drawString("--== PLAYER INFO ==--", 8, 75);
                g.drawString("PlayerPos: " + playerPos, 8, 90);
                g.drawString("PlayerGridPos: " + "(" + (int)playerPos.x + "," + (int)playerPos.y + ")", 8, 105);
                g.drawString("PlayerRot: " + playerRotation, 8, 120);
                g.drawString("PlayerDelta: " + playerDelta, 8, 135);
            }
            case(KeyEvent.VK_3) -> {
                g.drawString("--== MAP INFO ==--", 8, 75);
                if(world == null){
                    g.setPaint(Color.RED);
                    g.drawString("! NO MAP LOADED ! PRESS F8 TO RELOAD", 8, 90);
                    return;
                }
                g.drawString("Map:" + world.getName(), 8, 90);
                g.drawString("MapSize: " + world.Width() + " x " + world.Height(), 8, 105);
                g.drawString("Entities:" + world.getEntities().length, 8, 120);

                int startY = 150;
                if(world.getEntities().length > 0){
                    g.drawString("-- Entities --", 8 , startY);
                    startY += 15;
                    for(Entity e : world.getEntities()){
                        g.drawString(e.getEntityName(), 16, startY);
                        startY += 15;
                        for(String s : e.debugInfo()){
                            g.drawString(s, 24, startY);
                            startY += 15;
                        }
                    }
                }

                if(world.getPolyWalls().length > 0){
                    g.drawString("-- Poly Walls --", 8, startY);
                    startY += 15;
                    g.drawString("Num: " + world.getPolyWalls().length, 8, startY);
                    startY += 15;
                    for(int i = 0; i < world.getPolyWalls().length; i++){
                        g.drawString("pw" + i + ":", 16, startY);
                        startY += 15;
                        for(String s : world.getPolyWalls()[i].debugInfo()){
                            g.drawString(s, 24, startY);
                            startY += 15;
                        }
                    }
                }
            }
            case(KeyEvent.VK_4) -> {
                g.drawString("--== RENDERER INFO ==--", 8, 75);
                g.drawString("Rays: " + Const.RAYCASTER_RESOLUTION, 8, 90);
                g.drawString("Average Ray-Time: " + renderer.avgPerformance, 8 ,105);
                g.drawString("DepthBufferDraw: " + depthBufferDraw, 8, 120);
                g.drawString("Interpolated FPS: " + Time.INTERPOLATED_FPS, 8, 135);
            }
        }
    }
    //endregion

    //region outside control

    /**
     * Switches the currently loaded world
     * @param world the word which is meant to be loaded into the scene
     */
    private void switchWorld(World world){
        System.out.println("Switching to new World...");
        this.world = world;

        MAP_CELL_RESOLUTION = (world.Width() + world.Height())/2;
        CELL_WIDTH = Math.min(Const.WIDTH, Const.HEIGHT) / MAP_CELL_RESOLUTION-2;
        CELL_HEIGHT = Math.min(Const.WIDTH, Const.HEIGHT) / MAP_CELL_RESOLUTION-2;

        playerPos = world.getPlayerStartPos().copy();
        playerRotation = world.getStartRotation();

        renderer.setWorld(world);
    }
    //endregion

    //region Input Handling

    /**
     * Handles Player-Input
     */
    public void playerInput(){

        if(world == null)
            return;

        if(Input.isKeyPressed(KeyEvent.VK_W)){
            playerPos.add(playerDelta.copy().multiply(CELL_WIDTH * Const.PLAYER_WALK_SPEED * Time.deltaTime));

            if(world.getCellWall((int)playerPos.x, (int)playerPos.y) != 0){
                playerPos.subtract(playerDelta.copy().multiply(CELL_WIDTH * Const.PLAYER_WALK_SPEED * Time.deltaTime));
            }
        }
        else if(Input.isKeyPressed(KeyEvent.VK_S)){
            playerPos.subtract(playerDelta.copy().multiply(CELL_WIDTH * Const.PLAYER_WALK_SPEED * Time.deltaTime));

            if(world.getCellWall((int)playerPos.x, (int)playerPos.y) != 0){
                playerPos.add(playerDelta.copy().multiply(CELL_WIDTH * Const.PLAYER_WALK_SPEED * Time.deltaTime));
            }
        }

        if(Input.isKeyPressed(KeyEvent.VK_A)){
            playerRotation = playerRotation < 0 ? 2 * Math.PI : playerRotation - Const.PLAYER_TURN_SPEED * Time.deltaTime;
            playerDelta.x = Math.cos(playerRotation) * 5;
            playerDelta.y = Math.sin(playerRotation) * 5;
            playerDelta.normalize();
        }
        else if(Input.isKeyPressed(KeyEvent.VK_D)){
            playerRotation = playerRotation > 2 * Math.PI ? 0 : playerRotation + Const.PLAYER_TURN_SPEED * Time.deltaTime;
            playerDelta.x = Math.cos(playerRotation) * 5;
            playerDelta.y = Math.sin(playerRotation) * 5;
            playerDelta.normalize();
        }
    }

    /**
     * {@inheritDoc}
     * @param e the Pressed Key Event
     */
    @Override
    public void OnKeyDown(KeyEvent e){
        int keyCode = e.getKeyCode();

        if(keyCode == KeyEvent.VK_8){
            switchWorld(new World(worldPath));
        }
        else if(keyCode == KeyEvent.VK_7){
            depthBufferDraw = !depthBufferDraw;
        }

        if(keyCode < KeyEvent.VK_1 || keyCode > KeyEvent.VK_4)
            return;

        if(DEBUG_MODE == keyCode)
            DEBUG_MODE = 0;
        else
            DEBUG_MODE = keyCode;
    }
    //endregion
}

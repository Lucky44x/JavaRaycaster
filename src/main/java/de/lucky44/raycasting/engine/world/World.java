package de.lucky44.raycasting.engine.world;

import de.lucky44.raycasting.engine.animation.Animation;
import de.lucky44.raycasting.engine.animation.Animator;
import de.lucky44.raycasting.engine.math.vec2D;
import de.lucky44.raycasting.engine.entities.Entity;
import de.lucky44.raycasting.engine.rendering.TextureManager;
import de.lucky44.raycasting.rendering.renderables.AnimatedSprite;
import de.lucky44.raycasting.engine.time.Time;
import de.lucky44.raycasting.rendering.renderables.PolyWall;
import de.lucky44.raycasting.rendering.renderables.Sprite;
import lombok.Getter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A World class responsible for bringing all parts of the Application together
 * @author Nick Balischewski
 */
public class World {

    //mapData for game
    /**
     * The map in a threeDimension array ([i][x][y] -> i = 0: walls; i = 1: floor-cells; i = 2: ceiling-cells)
     */
    private int[][][] mapData;

    /**
     * The name of this world
     */
    @Getter
    private String name;

    /**
     * The poly-walls in this world/map
     */
    @Getter
    private PolyWall[] polyWalls;

    /**
     * The Entities in this Map
     */
    @Getter
    private Entity[] entities;

    /**
     * The startposition of the player
     */
    @Getter
    private vec2D playerStartPos;
    /**
     * The starting-rotation of the player
     */
    @Getter
    private double startRotation;

    /**
     * All animations that are used in this world
     */
    private final HashMap<String, Animation> animationBuffer = new HashMap<>();

    /**
     * The TextureManager to cache all textures used in this world
     */
    @Getter
    private final TextureManager textureManager = new TextureManager();

    /**
     * The directory of the data
     */
    private final String gameDirectory;

    /**
     * Initializes the mapdata and everything else
     * @param directory the directory of this map
     */
    public World(String directory){
        gameDirectory = directory;
        try{
            loadMap(directory);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * The width of the Map
     * @return width of the map
     */
    public int Width(){
        return mapData[0].length;
    }
    /**
     * The height of the Map
     * @return height of the map
     */
    public int Height(){
        return mapData[0][0].length;
    }

    /**
     * returns the wall at x and y
     * @param x the x coordinate of the cell
     * @param y the y coordinate of the cell
     * @return wall-cell at (x,y)
     */
    public int getCellWall(int x, int y){
        try{
            return mapData[0][x][y];
        }
        catch (IndexOutOfBoundsException e){
            e.printStackTrace();

            System.out.println("deltaTime: " + Time.deltaTime);
        }
        return 0;
    }

    /**
     * returns the floor-cell at x and y
     * @param x the x coordinate of the cell
     * @param y the y coordinate of the cell
     * @return floor-cell at (x,y)
     */
    public int getCellFloor(int x, int y){
        try{
            return mapData[1][x][y];
        }
        catch (IndexOutOfBoundsException e){
            e.printStackTrace();

            System.out.println("deltaTime: " + Time.deltaTime);
        }
        return 0;
    }

    /**
     * returns the ceiling-cell at x and y
     * @param x the x coordinate of the cell
     * @param y the y coordinate of the cell
     * @return ceiling-cell at (x,y)
     */
    public int getCellRoof(int x, int y){
        try{
            return mapData[2][x][y];
        }
        catch (IndexOutOfBoundsException e){
            e.printStackTrace();

            System.out.println("deltaTime: " + Time.deltaTime);
        }
        return 0;
    }

    /**
     * Loads ALL the MapData (including entities and polywalls) from the given directory
     * @param directory the directory containing the data
     * @throws IOException gets thrown when the map couldn't be loaded
     */
    private void loadMap(String directory) throws IOException{
        loadMapData(directory);
        loadEntities(directory);
        loadPolyWalls(directory);
    }

    /**
     * Loads the mapData from the given directory
     * @param directoryPath the directory
     * @throws IOException gets thrown when the no data.m file was found
     */
    private void loadMapData(String directoryPath) throws IOException {
        File saveFile = new File(directoryPath + "/data.m");
        BufferedReader reader = new BufferedReader(new FileReader(saveFile));
        String line = reader.readLine();
        boolean readingMapData = false;
        int lineCounter = 0;
        int mapToRead = 0;

        List<BufferedImage> textures = new ArrayList<>();

        while(line != null){

            String[] parts = line.split(" ");

            if(!readingMapData){
                switch(parts[0].toLowerCase()){
                    case("name:") -> {
                        StringBuilder nameBuilder = new StringBuilder();
                        for(int i = 1; i < parts.length; i++){
                            if(i != 1)
                                nameBuilder.append(" ");
                            nameBuilder.append(parts[i]);
                        }
                        name = nameBuilder.toString();
                    }
                    case("size:") -> mapData = new int[3][Integer.parseInt(parts[1])][Integer.parseInt(parts[2])];
                    case("playerpos:") -> playerStartPos = new vec2D(Double.parseDouble(parts[1]), Double.parseDouble(parts[2]));
                    case("playerrot:") -> startRotation = Double.parseDouble(parts[1]);
                    case("entities:") -> entities = new Entity[Integer.parseInt(parts[1])];
                    case("polywalls:") -> polyWalls = new PolyWall[Integer.parseInt(parts[1])];
                    case("texture:") -> {
                        if(parts[1].equalsIgnoreCase("tilemap")){
                            BufferedImage tile = textureManager.getTile(parts[2], parts[3]);

                            if(tile == null){
                                System.out.println("[ERROR] Could not load tile " + parts[3] + " from tilemap " + parts[2]);
                                break;
                            }

                            textures.add(tile);
                            break;
                        }

                        File imageFile = new File(directoryPath + "/t/" + parts[1]);
                        if(!imageFile.exists()){System.out.println("[ERROR] Could not load " + imageFile.getName()); break;}

                        textures.add(ImageIO.read(imageFile));
                        System.out.println("Loaded texture: " + imageFile.getName());
                    }
                    case("tilemap:") -> textureManager.loadTileMap(parts[1], directoryPath + "/t/tilemaps/" + parts[1].split("\\.")[0] + "/");
                    case("wallmap:") -> {
                        System.out.println("Reading wall-data");
                        readingMapData = true;
                        mapToRead = 0;
                    }
                    case("floormap:") -> {
                        System.out.println("Reading floor-data");
                        readingMapData = true;
                        mapToRead = 1;
                    }
                    case("roofmap:") -> {
                        System.out.println("Reading roof-data");
                        readingMapData = true;
                        mapToRead = 2;
                    }
                }
            }
            else{
                for(int x = 0; x < parts.length; x++){
                    mapData[mapToRead][x][lineCounter] = Integer.parseInt(parts[x]);
                }
                lineCounter++;

                if(lineCounter > mapData[0][0].length-1){
                    System.out.println(lineCounter);
                    readingMapData = false;
                    lineCounter = 0;
                }
            }

            line = reader.readLine();
        }

        textureManager.loadTextures(textures.toArray(BufferedImage[]::new));
        System.out.println("Done reading map data...");
    }

    /**
     * Loads the entities of the given world
     * @param directory the directory
     * @throws IOException gets thrown when the entities couldn't be loaded
     */
    private void loadEntities(String directory) throws IOException{
        String entityDirectory = directory + "/e/";

        for(int i = 0; i < entities.length; i++){
            File entityFile = new File(entityDirectory + "e" + i + ".e");
            BufferedReader reader = new BufferedReader(new FileReader(entityFile));
            String line = reader.readLine();

            String eName = "";
            vec2D ePos = vec2D.ZERO.copy();

            int eType = 0;
            Sprite renderer = null;
            ArrayList<Animation> animations = new ArrayList<>();

            BufferedImage sprite = null;
            vec2D size = vec2D.ZERO.copy();
            int yOffset = 0;

            while(line != null){
                String[] parts = line.split(" ");

                switch(parts[0].toLowerCase()){
                    case("type:") -> eType = parts[1].equalsIgnoreCase("static") ? 0 : 1;
                    case("position:") -> ePos = new vec2D(Double.parseDouble(parts[1]), Double.parseDouble(parts[2]));
                    case("name:") -> {
                        StringBuilder nameBuilder = new StringBuilder();
                        for(int j = 1; j < parts.length; j++){
                            if(j != 1)
                                nameBuilder.append(" ");
                            nameBuilder.append(parts[j]);
                        }
                        eName = nameBuilder.toString();
                    }

                    case("animation:") -> animations.add(loadAnimation(parts[1], entityDirectory));
                    case("sprite:") -> {

                        if(parts[1].equalsIgnoreCase("tilemap")){
                            BufferedImage tile = textureManager.getTile(parts[2], parts[3]);

                            if(tile == null){
                                System.out.println("[ERROR] Could not load tile " + parts[3] + " from tilemap " + parts[2]);
                                break;
                            }

                            sprite = tile;
                            break;
                        }

                        File imageFile = new File(entityDirectory + "sprites/" + parts[1]);
                        if(!imageFile.exists()){System.out.println("[ERROR] Could not load " + imageFile.getName()); continue;}

                        sprite = ImageIO.read(imageFile);
                    }
                    case("size:") -> size = new vec2D(Double.parseDouble(parts[1]), Double.parseDouble(parts[2]));
                    case("yoffset:") -> yOffset = Integer.parseInt(parts[1]);
                }
                line = reader.readLine();
            }

            renderer = new Sprite(sprite, size,yOffset);
            if(eType == 1){
                Animator animator = new Animator(animations.toArray(Animation[]::new));
                renderer = new AnimatedSprite(animator, size, yOffset);
            }

            entities[i] = new Entity(eName, ePos, renderer);
        }

        System.out.println("Done reading entity data...");
    }

    /**
     * Loads the polywalls of the given World
     * @param directory the directory
     * @throws IOException gets thrown when the polywalls couldn't be loaded
     */
    private void loadPolyWalls(String directory) throws IOException{
        String entityDirectory = directory + "/pw/";

        for(int i = 0; i < polyWalls.length; i++){
            File entityFile = new File(entityDirectory + "pw" + i + ".pw");
            BufferedReader reader = new BufferedReader(new FileReader(entityFile));
            String line = reader.readLine();

            List<vec2D> points = new ArrayList<>();
            int texture = 0;
            boolean tile = true;
            double texOffset = 0;

            while(line != null){
                String[] parts = line.split(" ");

                switch(parts[0].toLowerCase()){
                    case("point:") -> points.add(new vec2D(Double.parseDouble(parts[1]), Double.parseDouble(parts[2])));
                    case("texture:") -> texture = Integer.parseInt(parts[1]);
                    case("texture_tile:") -> tile = Boolean.parseBoolean(parts[1]);
                    case("texture_offset:") -> texOffset = Double.parseDouble(parts[1]);
                }
                line = reader.readLine();
            }

            if(points.size() < 2){
                System.out.println("Could not load polyWall " + "pw" + i + ".pw, since it has less than 2 points");
                continue;
            }

            polyWalls[i] = new PolyWall(points.toArray(vec2D[]::new), texture, tile, texOffset);
        }

        System.out.println("Done reading PolyWall data...");
    }

    /**
     * Loads the animation with the given name
     * @param animationName the animation-name
     * @param directory the world directory
     * @return the loaded Animation
     * @throws IOException gets thrown when the animation couldn't be loaded
     */
    private Animation loadAnimation(String animationName, String directory) throws IOException {
        if(animationBuffer.containsKey(animationName))
            return animationBuffer.get(animationName);

        String animDirectory = directory + "sprites/anim/" + animationName + "/";

        List<BufferedImage> frames = new ArrayList<>();
        String name = "TMP";
        double frameTime = 0.25d;

        File animationFile = new File(animDirectory + animationName + ".a");
        BufferedReader reader = new BufferedReader(new FileReader(animationFile));
        String line = reader.readLine();

        //Types => 0 -> individual | 1 -> singleAnimSpritesheet | 2 -> multiple anim spritesheet
        int frameType = 0;
        int numOfFrames = 0;

        String spriteSheetName = "";
        String fileType = "";

        while(line != null){
            String[] parts = line.split(" ");

            switch(parts[0].toLowerCase()){
                case("name:") -> name = parts[1];
                case("type:") -> frameType = parts[1].equalsIgnoreCase("frames") ? 0 : parts[1].equalsIgnoreCase("spritesheet") ? 1 : 2;
                case("numframes:") -> numOfFrames = Integer.parseInt(parts[1]);
                case("frametime:") -> frameTime = Double.parseDouble(parts[1]);
                case("spritesheet:") -> spriteSheetName = parts[1];
                case("filetype:") -> fileType = parts[1];
                case("frame:") -> {

                    if(parts[1].equalsIgnoreCase("tilemap")){
                        BufferedImage frame = textureManager.getTile(parts[2], parts[3]);

                        if(frame == null){
                            System.out.println("[ERROR] Could not load tile " + parts[3] + " from tilemap " + parts[2]);
                            break;
                        }

                        frames.add(frame);
                        break;
                    }

                    File imageFile = new File(animDirectory + parts[1]);
                    if(!imageFile.exists()){System.out.println("[ERROR] Could not load " + imageFile.getName()); break;}

                    frames.add(ImageIO.read(imageFile));
                }
            }
            line = reader.readLine();
        }

        switch (frameType){
            case(1) -> {
                File imageFile = new File(animDirectory + spriteSheetName + "." + fileType);
                if(!imageFile.exists()){System.out.println("[ERROR] Could not load " + imageFile.getName()); break;}

                BufferedImage originalImage = ImageIO.read(imageFile);
                int frameWidth = originalImage.getWidth() / numOfFrames;
                int frameHeight = originalImage.getHeight();

                for(int i = 0; i < numOfFrames; i++){
                    frames.add(originalImage.getSubimage(frameWidth * i, 0, frameWidth, frameHeight));
                }
            }
        }

        Animation anim = new Animation(name, frames.toArray(BufferedImage[]::new), frameTime);
        animationBuffer.put(animationName, anim);
        System.out.println("Loaded Animation " + animationName + " and added it to the buffer");
        return anim;
    }
}

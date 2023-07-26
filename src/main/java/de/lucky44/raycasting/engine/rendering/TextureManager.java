package de.lucky44.raycasting.engine.rendering;

import de.lucky44.raycasting.engine.Const;
import de.lucky44.raycasting.engine.util.TileMap;
import de.lucky44.raycasting.engine.util.TileMapTileInfo;
import de.lucky44.raycasting.engine.world.World;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Object for caching textures which are loaded from the disk
 * @author Nick Balischewski
 */
public class TextureManager {
    /**
     * All textures in use by the Application
     */
    private BufferedImage[] textures;

    /**
     * Different TileMaps used by the Application
     */
    private final HashMap<String, TileMap> tileMaps = new HashMap<>();

    /**
     * Loads the provided BufferedImage[] as the texture-list used by the Project
     * @param textures ALL textures used by the Application
     */
    public void loadTextures(BufferedImage[] textures){
        this.textures = textures;
    }

    /**
     * Loads a tilemap with the provided name and directory
     * @param tileMapName the Name of the tilemap
     * @param directory the Directory of the tilemap
     * @throws IOException gets Thrown if the tilemap cannot be loaded
     */
    public void loadTileMap(String tileMapName, String directory) throws IOException {
        try{
            File imageFile = new File(directory + tileMapName);
            if(!imageFile.exists()){
                System.out.println("[ERROR] Can not load tilemap-image-file " + imageFile.getAbsolutePath());
                return;
            }

            TileMap toAdd = new TileMap(new HashMap<String, TileMapTileInfo>(), ImageIO.read(imageFile));
            toAdd.loadMap(tileMapName.split("\\.")[0], directory);
            tileMaps.put(tileMapName.split("\\.")[0], toAdd);
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Returns the tile with the given name from the tilemap with a given name
     * @param tileMapName name of the tilemap
     * @param tileName name of the tile
     * @return one specific tile
     */
    public BufferedImage getTile(String tileMapName, String tileName){
        return tileMaps.get(tileMapName).getTile(tileName);
    }

    /**
     * Calculates a textureStripe for the renderer to use
     * @param texture the texture id to use
     * @param texX the textureX "coordinate"
     * @param height the textureY "coordinate"
     * @param side darkens the color to create a sort of proto-shading
     * @return the array of colors to be drawn vertically
     */
    public int[] getTextureStripe(int texture, double texX, int height, int side){

        if(height >  (4d * Const.VIEWPORT_HEIGHT))
            height = (int)(4d * Const.VIEWPORT_HEIGHT);

        if(height < 0)
            return new int[0];

        int[] pixels = new int[height];
        double stepY = (double) textures[texture].getHeight() / height;
        double yPos = 0;
        int xPos = (int)(texX * (textures[texture].getWidth()-1));

        if(texX < 0 || texX > 1){
            System.out.println("ERROR: texX not inside Texture -> " + texX);
            Arrays.fill(pixels, 0);
            return pixels;
        }

        try{
            for(int i = 0; i < pixels.length; i++){
                pixels[i] = side == 0 ? textures[texture].getRGB(xPos, (int)yPos) : new Color(textures[texture].getRGB(xPos, (int)yPos)).darker().getRGB();
                yPos += stepY;
            }
        }
        catch(ArrayIndexOutOfBoundsException e){
            System.out.println(xPos + " " + yPos);
        }

        return pixels;
    }

    /**
     * basically does the same as the getTextureStripe Method but for the floor
     * CURRENTLY: Not used
     */
    public int[] getFloorStripe(double dist, double floorWallX, double floorWallY, int drawHeight, double posX, double posY, World world){

        if(drawHeight < 0)
            return  new int[0];

        int[] pixels = new int[drawHeight];
        double distPlayer = 0;
        double currentDist;

        for(int y = 0; y < drawHeight; y++){
            currentDist = (double) Const.VIEWPORT_HEIGHT / (2 * y - Const.VIEWPORT_HEIGHT);
            double weight = (currentDist - distPlayer) / (dist - distPlayer);
            double currentFloorX = weight * floorWallX + (1 - weight) * posX;
            double currentFloorY = weight * floorWallY + (1 - weight) * posY;

            if(currentFloorX > world.Width() || currentFloorX < 0)
                continue;
            if(currentFloorY > world.Height() || currentFloorY < 0)
                continue;

            BufferedImage floorTexture = textures[world.getCellFloor((int) currentFloorX, (int) currentFloorY)];

            //Floor
            int texX, texY;
            texX = (int)(currentFloorX * floorTexture.getWidth()) % floorTexture.getWidth();
            texY = (int)(currentFloorY * floorTexture.getHeight()) % floorTexture.getHeight();

            if(texX > floorTexture.getWidth() || texX < 0)
                continue;
            if(texY > floorTexture.getHeight() || texY < 0)
                continue;

            pixels[y] = floorTexture.getRGB(texX, texY);
        }

        return pixels;
    }
}

package de.lucky44.raycasting.engine.util;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;

/**
 * An immutable TileMap data carrier
 * @param tiles the tiles in String to TileMapTileInfo form
 * @param tileMapImage the Image of the tileMap
 * @author Nick Balischewski
 */
public record TileMap(HashMap<String, TileMapTileInfo> tiles, BufferedImage tileMapImage) {

    /**
     * Loads a tilemap by name
     * @param tileMapName the tilemap-name
     * @param directory the tilemap-directory
     * @throws IOException gets thrown when the tilemap can't be loaded
     */
    public void loadMap(String tileMapName, String directory) throws IOException {
        File tilemapFile = new File(directory + "/" + tileMapName + ".tilemap");
        if(!tilemapFile.exists()){
            System.out.println("[ERROR] Can not load " + tilemapFile.getAbsolutePath());
            return;
        }

        BufferedReader reader = new BufferedReader(new FileReader(tilemapFile));
        String line = reader.readLine();

        while(line != null){

            String[] parts = line.split(" ");

            tiles.put(parts[0], new TileMapTileInfo(parts[0], Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), Integer.parseInt(parts[4])));

            line = reader.readLine();
        }
        System.out.println("Finished loading tilemap " + tileMapName);
    }

    /**
     * Gets a tile with a given name from this tilemap
     * @param tileName the name of the tile
     * @return the tile-image
     */
    public BufferedImage getTile(String tileName){
        TileMapTileInfo info = tiles.get(tileName);
        if(info == null)
            return null;

        return tileMapImage.getSubimage(info.x(), info.y(), info.w(), info.h());
    }
}

package de.lucky44.raycasting.engine.util;

/**
 * The immutable TileMapTileInfo carrier
 * @param name the name of this tile
 * @param x the x coordinate of this tile
 * @param y the y coordinate of this tile
 * @param w the width of this tile
 * @param h the height of this tile
 */
public record TileMapTileInfo(String name, int x, int y, int w, int h) {
}

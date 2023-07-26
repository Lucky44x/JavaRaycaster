package de.lucky44.raycasting.engine.entities;

/**
 * Script object, meant to by used in conjunction with Entities to create Enemies/Doors etc (kind of ECS system)
 * CURRENTLY: Not used
 * @author Nick Balischewski
 */
public abstract class Script {
    private Entity parent;
    public abstract void update();
    public abstract void start();
}

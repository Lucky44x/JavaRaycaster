package de.lucky44.raycasting.engine;

import de.lucky44.raycasting.engine.architecture.reflection.Ignore;

import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

/**
 * Constants used by the Engine
 * @author Nick Balischewski
 */
public class Const {
    @Ignore
    public static final String VERSION = "4.0.0a";

    @Ignore
    public static String[] constantsText;

    //Window
    public static final int HEIGHT = 800;
    public static final int WIDTH = HEIGHT * 2;
    public static final int VIEWPORT_WIDTH = Math.max(WIDTH, HEIGHT) / 2;
    public static final int VIEWPORT_HEIGHT = Math.min(WIDTH, HEIGHT);

    //Player
    public static final double PLAYER_TURN_SPEED = 50;
    public static final double PLAYER_WALK_SPEED = 1.5;

    //Time
    public static final double FPS_CAP = 120;
    public static final double UPS_CAP = 200;
    public static final double deltaTimeMultiplier = 0.00005;

    //Rendering
    /*
    For good CPUs you can use a resolution of 100% meaning, VIEWPORT_WIDTH
    For older or just bad CPUs you should use something like VIEWPORT_WIDTH/8, meaning for every 8 pixels you will shoot one ray
     */
    public static final int RAYCASTER_RESOLUTION = VIEWPORT_WIDTH;
    public static final int RAYCASTER_LINE_WIDTH = VIEWPORT_WIDTH / RAYCASTER_RESOLUTION;
    public static final int FOV = 70;
    public static final double MAX_RAY_LENGTH = 100;
    public static final double DR = 0.0174533;
    public static final double ALPHA_CLIP = 0.2d;

    //Colors
    public static final Color WALL_HIGHLIGHT = new Color(0.45f,0.45f,0.45f);
    public static final Color WALL_SHADOW = new Color(0.4f, 0.4f,0.4f);
    public static final Color FLOOR_COLOR = new Color(0.1f, 0.45f, 0.2f);
    public static final Color SKY_COLOR = new Color(0.45f, 0.72f, 0.94f);

    public static void generateConstantString(){
        Set<String> fieldText = new HashSet<>();

        for(Field f : Const.class.getDeclaredFields()){
            if(f.isAnnotationPresent(Ignore.class) || Modifier.isPrivate(f.getModifiers()))
                continue;

            String fieldValue = "";

            try{
                Type T = f.getGenericType();
                if(T == boolean.class)
                    fieldValue = String.valueOf(f.getBoolean(null));
                else if(T == int.class)
                    fieldValue = String.valueOf(f.getInt(null));
                else if(T == double.class)
                    fieldValue = String.valueOf(f.getDouble(null));
                else if(T == Color.class){
                    Color c = (Color) f.get(null);
                    fieldValue = "(" + c.getRed() + "," + c.getBlue() + "," + c.getGreen() + ")";
                }
            }
            catch (IllegalAccessException e){
                e.printStackTrace();
            }

            fieldText.add(f.getName() + ": " + fieldValue);
        }

        constantsText = fieldText.toArray(String[]::new);
    }
}

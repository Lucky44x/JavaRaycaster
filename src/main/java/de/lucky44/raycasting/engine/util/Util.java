package de.lucky44.raycasting.engine.util;

import de.lucky44.raycasting.engine.rendering.Renderable;
import java.util.List;

/**
 * A utility class for utility stuff
 *
 * NOTE: This class is also incredibly broken
 *
 * @author Nick Balischewski
 */
public class Util {
    /**
     * Supposed to quickly add all renderables in the array to the list
     * @param renderables to add
     * @param renderablesList to be added to
     */
    public static void fasterAddAll(Renderable[] renderables, List<Renderable> renderablesList){
        for(int i = 0; i < renderables.length; i++){
            renderablesList.add(renderables[i]);
        }
    }

    /**
     * Fixes an angle between 360 and -360 to an angle between 360 and 0
     * @param ang the prior to conversion angle
     * @return the fixed angle
     */
    public static double fixAng(double ang){
        if(ang>359)
            ang -= 360;
        if(ang < 0)
            ang += 360;

        return ang;
    }
}

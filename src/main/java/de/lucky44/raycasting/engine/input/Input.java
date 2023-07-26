package de.lucky44.raycasting.engine.input;

import de.lucky44.raycasting.engine.scenes.Scene;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Object for handling Inputs from mouse and keyboard
 * @author Nick Balischewski
 */
public class Input implements KeyListener {
    /**
     * Instance (Singleton Pattern)
     */
    private static Input I;
    /**
     * The different "Scene" Objects registered as listeners
     */
    private static final Set<Scene> registered = new HashSet<>();
    /**
     * The map for caching pressed/released keys
     */
    private static final HashMap<Integer, Boolean> keyMap = new HashMap<>();

    /**
     * Constructor after Singleton Pattern
     */
    public Input(){
        if(I == null)
            I = this;
        else
            System.out.println("[ERROR] Multiple instances of KeyBoardInputs");
    }

    /**
     * Registers the specified Scene as a listener
     * @param s The Scene-Object which is meant to listen
     */
    public static void register(Scene s){
        System.out.println("Registered " + s.getClass().getName());
        registered.add(s);
    }
    /**
     * unregisters the specified Scene as a listener
     * @param s The Scene-Object which is meant to no longer listen
     */
    public static void unregister(Scene s){
        System.out.println("DeRegistered " + s.getClass().getName());

        registered.remove(s);
    }

    /**
     * Inherited from KeyListener {@link KeyListener}
     * @param e the event to be processed
     */
    @Override
    public void keyTyped(KeyEvent e) {
        for(Scene s : registered)
            s.OnKeyTyped(e);
    }

    /**
     * Inherited from {@link KeyListener}
     * @param e the event to be processed
     */
    @Override
    public void keyPressed(KeyEvent e) {
        keyMap.put(e.getKeyCode(), true);

        for(Scene s : registered)
            s.OnKeyDown(e);
    }

    /**
     * Inherited from {@link KeyListener}
     * @param e the event to be processed
     */
    @Override
    public void keyReleased(KeyEvent e) {

        keyMap.put(e.getKeyCode(), false);

        for(Scene s : registered)
            s.OnKeyUp(e);
    }

    /**
     * returns if the specified keycode is currently pressed down
     * @param keyCode the keyCode of the specified key
     * @return the current state of the key
     */
    public static boolean isKeyPressed(int keyCode){

        //System.out.println("Requesting " + keyCode + " -> " + keyMap.computeIfAbsent(keyCode, k -> false));

        return keyMap.computeIfAbsent(keyCode, k -> false);
    }
}

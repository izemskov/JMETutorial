/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ru.develgame.protogame;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.behaviors.npc.steering.PursuitBehavior;
import com.jme3.ai.agents.util.control.MonkeyBrainsAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

/**
 *
 * @author izemskov
 */
public class HelloAI extends SimpleApplication {
    private Geometry player;
    private Geometry enemy;
    private Boolean isRunning = true;
    
    //defining game
    private MonkeyBrainsAppState brainsAppState = MonkeyBrainsAppState.getInstance(); 

    public static void main(String[] args) {
        HelloAI app = new HelloAI();
        app.start();
    }
    
    @Override
    public void simpleInitApp() {
        //defining app
        brainsAppState.setApp(this);
        
        Box b = new Box(1, 1, 1);
        player = new Geometry("Player", b);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
        player.setMaterial(mat);
        
        
        enemy = new Geometry("Enemy", b);
        Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setColor("Color", ColorRGBA.Red);
        enemy.setMaterial(mat1);
        
        rootNode.attachChild(player);
        rootNode.attachChild(enemy);
        
        initKeys(); // load my custom keybinding
        
        flyCam.setEnabled(false);
        
        Agent playerAgent = new Agent("Player agent", player);
        
        //initialization of Agents with their names and spatials
        Agent enemyAgent = new Agent("Enemy agent", enemy); 
        //there isn't any method in framework like createAgentSpatial()
        //user is supposed to build his own spatials for game

        //adding agent to MonkeyBrainsAppState
        brainsAppState.addAgent(enemyAgent);

        //setting moveSpeed, rotationSpeed, mass..
        enemyAgent.setMoveSpeed(20); 
        enemyAgent.setRotationSpeed(30);
        //used for steering behaviors in com.jme3.ai.agents.behaviors.npc.steering
        enemyAgent.setMass(40);
        enemyAgent.setMaxForce(3);

        //creating main behavior
        //agent can have only one behavior but that behavior can contain other behaviors
        enemyAgent.setMainBehavior(new PursuitBehavior(enemyAgent, playerAgent));

        //starting agents
        brainsAppState.start();
    }
    
    /** Custom Keybinding: Map named actions to inputs. */
    private void initKeys() {
        /* You can map one or several inputs to one named mapping. */
        inputManager.addMapping("Pause",  new KeyTrigger(KeyInput.KEY_P));
        inputManager.addMapping("Left",   new KeyTrigger(KeyInput.KEY_J));
        inputManager.addMapping("Right",  new KeyTrigger(KeyInput.KEY_K));
        inputManager.addMapping("Up",     new KeyTrigger(KeyInput.KEY_H),
                new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));
        inputManager.addMapping("Down",   new KeyTrigger(KeyInput.KEY_L),
                new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
        inputManager.addMapping("Rotate", new KeyTrigger(KeyInput.KEY_SPACE),
                new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        /* Add the named mappings to the action listeners. */
        inputManager.addListener(actionListener, "Pause");
        inputManager.addListener(analogListener, "Left", "Right", "Rotate", "Up", "Down");
    }
    
    /** Use this listener for KeyDown/KeyUp events */
    private ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("Pause") && !keyPressed) {
                isRunning = !isRunning;
            }
        }
    };

    /** Use this listener for continuous events */
    private AnalogListener analogListener = new AnalogListener() {
        @Override
        public void onAnalog(String name, float value, float tpf) {
            if (isRunning) {
                if (name.equals("Rotate")) {
                    player.rotate(0, value, 0);
                }
                if (name.equals("Right")) {
                    player.move((new Vector3f(value, 0,0)) );
                }
                if (name.equals("Left")) {
                    player.move(new Vector3f(-value, 0,0));
                }
                if (name.equals("Up")) {
                    player.move((new Vector3f(0, value,0)) );
                }
                if (name.equals("Down")) {
                    player.move((new Vector3f(0, -value,0)) );
                }
            } else {
                System.out.println("Press P to unpause.");
            }
        }
    };
    
    @Override
    public void simpleUpdate(float tpf) {
        brainsAppState.update(tpf);
    }
    
}

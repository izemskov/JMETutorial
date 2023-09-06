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
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;

/**
 *
 * @author izemskov
 */
public class HelloAI2 extends SimpleApplication implements ActionListener {

    final private Vector3f walkDirection = new Vector3f();
    private boolean left = false, right = false, up = false, down = false;
    
    //Temporary vectors used on each frame.
    //They here to avoid instantiating new vectors on each frame
    final private Vector3f camDir = new Vector3f();
    final private Vector3f camLeft = new Vector3f();
    
    private Node playerModel;
    private Node enemyModel;
    
    //defining game
    private MonkeyBrainsAppState brainsAppState = MonkeyBrainsAppState.getInstance(); 
    
    public static void main(String[] args) {
        HelloAI2 app = new HelloAI2();
        AppSettings settings = new AppSettings(true);
        settings.setResolution(1024,768);
        app.setSettings(settings);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));
        flyCam.setMoveSpeed(100);
        setUpKeys();
        setUpLight();
        
        // We load the scene from the zip file and adjust its size.
        Spatial sceneModel = assetManager.loadModel("Scenes/town/main.j3o");
        sceneModel.setLocalScale(2f);
        
        playerModel = new Node();
        
        enemyModel = (Node) assetManager.loadModel("Models/Oto/OtoOldAnim.j3o");

        // We attach the scene and the player to the rootnode and the physics space,
        // to make them appear in the game world.
        rootNode.attachChild(sceneModel);
        rootNode.attachChild(playerModel);
        rootNode.attachChild(enemyModel);
        
        
        /////////////
        /////////////
        /////////////
        //defining app
        Agent playerAgent = new Agent("Player agent", playerModel);
        
        //initialization of Agents with their names and spatials
        Agent enemyAgent = new Agent("Enemy agent", enemyModel); 
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
    
    private void setUpKeys() {
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addListener(this, "Left");
        inputManager.addListener(this, "Right");
        inputManager.addListener(this, "Up");
        inputManager.addListener(this, "Down");
        inputManager.addListener(this, "Jump");
    }

    @Override
    public void onAction(String binding, boolean value, float tpf) {
        if (binding.equals("Left")) {
            left = value;
        } else if (binding.equals("Right")) {
            right = value;
        } else if (binding.equals("Up")) {
            up = value;
        } else if (binding.equals("Down")) {
            down = value;
        } else if (binding.equals("Jump")) {
            //player.jump();
        }
    }
    
    private void setUpLight() {
        // We add light so we see the scene
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(1.3f));
        rootNode.addLight(al);

        DirectionalLight dl = new DirectionalLight();
        dl.setColor(ColorRGBA.White);
        dl.setDirection(new Vector3f(2.8f, -2.8f, -2.8f).normalizeLocal());
        rootNode.addLight(dl);
    }

    @Override
    public void simpleUpdate(float tpf) {
        camDir.set(cam.getDirection()).multLocal(0.6f);
        camLeft.set(cam.getLeft()).multLocal(0.4f);
        walkDirection.set(0, 0, 0);
        if (left) {
            walkDirection.addLocal(camLeft);
        }
        if (right) {
            walkDirection.addLocal(camLeft.negate());
        }
        if (up) {
            walkDirection.addLocal(camDir);
        }
        if (down) {
            walkDirection.addLocal(camDir.negate());
        }
        playerModel.getLocalTranslation().addLocal(walkDirection);
        cam.setLocation(playerModel.getLocalTranslation());
        
        brainsAppState.update(tpf);
        
    }
    
}

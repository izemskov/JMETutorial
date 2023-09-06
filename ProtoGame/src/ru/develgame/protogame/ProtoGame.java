/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ru.develgame.protogame;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
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
public class ProtoGame extends SimpleApplication implements ActionListener {
    private CharacterControl player;
    final private Vector3f walkDirection = new Vector3f();
    private boolean left = false, right = false, up = false, down = false;
    
    //Temporary vectors used on each frame.
    //They here to avoid instantiating new vectors on each frame
    final private Vector3f camDir = new Vector3f();
    final private Vector3f camLeft = new Vector3f();
    
    private Node enemyModel;
    private CharacterControl enemy;
    private Vector3f enemyDir = new Vector3f(0.0f, 0.0f, 1.0f);
    
    public static void main(String[] args) {
        ProtoGame app = new ProtoGame();
        AppSettings settings = new AppSettings(true);
        settings.setResolution(1024,768);
        app.setSettings(settings);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        /** Set up Physics */
        BulletAppState bulletAppState = new BulletAppState();
        bulletAppState.setDebugEnabled(true);
        stateManager.attach(bulletAppState);
        
        viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));
        flyCam.setMoveSpeed(100);
        setUpKeys();
        setUpLight();
        
        // We load the scene from the zip file and adjust its size.
        Spatial sceneModel = assetManager.loadModel("Scenes/town/main.j3o");
        sceneModel.setLocalScale(2f);

        // We set up collision detection for the scene by creating a
        // compound collision shape and a static RigidBodyControl with mass zero.
        CollisionShape sceneShape = CollisionShapeFactory.createMeshShape(sceneModel);
        RigidBodyControl landscape = new RigidBodyControl(sceneShape, 0);
        sceneModel.addControl(landscape);

        // We set up collision detection for the player by creating
        // a capsule collision shape and a CharacterControl.
        // The CharacterControl offers extra settings for
        // size, step height, jumping, falling, and gravity.
        // We also put the player in its starting position.

        CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(1.5f, 6f, 1);
        player = new CharacterControl(capsuleShape, 0.05f);
        player.setJumpSpeed(20);
        player.setFallSpeed(30);
        player.setGravity(30);
        player.setPhysicsLocation(new Vector3f(0, 10, 0));
        
        enemyModel = (Node) assetManager.loadModel("Models/Oto/OtoOldAnim.j3o");
        
        CapsuleCollisionShape capsuleShape2 = new CapsuleCollisionShape(1.5f, 6f, 1);
        enemy = new CharacterControl(capsuleShape2, 0.05f);
        enemy.setJumpSpeed(20);
        enemy.setFallSpeed(30);
        enemy.setGravity(30);
        enemy.setPhysicsLocation(new Vector3f(20, 10, 0));

        // We attach the scene and the player to the rootnode and the physics space,
        // to make them appear in the game world.
        rootNode.attachChild(sceneModel);
        rootNode.attachChild(enemyModel);
        bulletAppState.getPhysicsSpace().add(landscape);
        bulletAppState.getPhysicsSpace().add(player);
        bulletAppState.getPhysicsSpace().add(enemy);
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
            player.jump();
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
        player.setWalkDirection(walkDirection);
        cam.setLocation(player.getPhysicsLocation());
        
        walkDirection.set(0, 0, 0);
        walkDirection.addLocal(enemyDir).multLocal(0.6f);
        enemy.setWalkDirection(walkDirection);
        enemyModel.setLocalTranslation(enemy.getPhysicsLocation());
    }
    
}

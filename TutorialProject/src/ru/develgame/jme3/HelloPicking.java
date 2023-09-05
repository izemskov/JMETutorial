/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ru.develgame.jme3;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.LoopMode;
import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.system.AppSettings;
import java.util.Random;

/**
 *
 * @author izemskov
 */
public class HelloPicking extends SimpleApplication {

    public static void main(String[] args) {
        HelloPicking app = new HelloPicking();
        AppSettings settings = new AppSettings(true);
        settings.setResolution(800,600);
        app.setSettings(settings);
        app.start();
    }
    
    private Node shootables;
    private Geometry mark;
    private Random random = new Random();
    private AnimChannel channel;
    
    private Node inventory;
    private Vector3f oldTranslation;

    @Override
    public void simpleInitApp() {
        initCrossHairs(); // a "+" in the middle of the screen to help aiming
        initKeys();       // load custom key mappings
        initMark();       // a red sphere to mark the hit

        /** create four colored boxes and a floor to shoot at: */
        shootables = new Node("Shootables");
        rootNode.attachChild(shootables);
        shootables.attachChild(makeCube("a Dragon", -2f, 0f, 1f));
        shootables.attachChild(makeCube("a tin can", 1f, -2f, 0f));
        shootables.attachChild(makeCube("the Sheriff", 0f, 1f, -2f));
        shootables.attachChild(makeCube("the Deputy", 1f, 0f, -4f));
        shootables.attachChild(makeFloor());
        shootables.attachChild(makeCharacter());
        
        inventory = new Node();
        inventory.scale(30.0f);
        inventory.setLocalTranslation(30, settings.getHeight() / 2, 0);
        guiNode.attachChild(inventory);
    }

    /** Declaring the "Shoot" action and mapping to its triggers. */
    private void initKeys() {
        inputManager.addMapping("Shoot",
                new KeyTrigger(KeyInput.KEY_SPACE), // trigger 1: spacebar
                new MouseButtonTrigger(MouseInput.BUTTON_LEFT)); // trigger 2: left-button click
        inputManager.addListener(actionListener, "Shoot");
    }
  
    /** Defining the "Shoot" action: Determine what was hit and how to respond. */
    final private ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("Shoot") && !keyPressed) {
                boolean inventoryPooled = false;
                if (!inventory.getChildren().isEmpty()) {
                    inventoryPooled = true;
                    
                    Spatial geometry = inventory.getChildren().get(0);
                    shootables.attachChild(geometry);
                    geometry.setLocalTranslation(new Vector3f(oldTranslation));
                    inventory.detachAllChildren();
                }
                
                // 1. Reset results list.
                CollisionResults results = new CollisionResults();
                // 2. Aim the ray from cam loc to cam direction.
                Ray ray = new Ray(cam.getLocation(), cam.getDirection());
                // 3. Collect intersections between Ray and Shootables in results list.
                shootables.collideWith(ray, results);
                // 4. Print the results
                System.out.println("----- Collisions? " + results.size() + "-----");
                for (int i = 0; i < results.size(); i++) {
                    // For each hit, we know distance, impact point, name of geometry.
                    float dist = results.getCollision(i).getDistance();
                    Vector3f pt = results.getCollision(i).getContactPoint();
                    String hit = results.getCollision(i).getGeometry().getName();
                    System.out.println("* Collision #" + i);
                    System.out.println("  You shot " + hit + " at " + pt + ", " + dist + " wu away.");
                }
                // 5. Use the results (we mark the hit object)
                if (results.size() > 0) {
                    // The closest collision point is what was truly hit:
                    CollisionResult closest = results.getClosestCollision();
                    Geometry geometry = closest.getGeometry();
                    if (!geometry.getName().startsWith("Oto")) {
                        Material material = geometry.getMaterial();
                        material.setColor("Color", new ColorRGBA(random.nextFloat(1.0f), 
                                random.nextFloat(1.0f), 
                                random.nextFloat(1.0f), 1.0f));
                        
                        if (!inventoryPooled && inventory.getChildren().isEmpty()) {
                            shootables.detachChild(geometry);
                            inventory.attachChild(geometry);
                            oldTranslation = new Vector3f(geometry.getLocalTranslation());
                            geometry.setLocalTranslation(new Vector3f(0, 0, 0));
                        }
                    }
                    else {
                        if (!channel.getAnimationName().equals("Dodge")) {
                            channel.setAnim("Dodge", 0.50f);
                            channel.setLoopMode(LoopMode.DontLoop);
                        }
                    }
                    // Let's interact - we mark the hit with a red dot.
                    mark.setLocalTranslation(closest.getContactPoint());
                    rootNode.attachChild(mark);
                } else {
                    // No hits? Then remove the red mark.
                    rootNode.detachChild(mark);
                }
            }
        }
    };

    /** A cube object for target practice */
    private Geometry makeCube(String name, float x, float y, float z) {
        Box box = new Box(1, 1, 1);
        Geometry cube = new Geometry(name, box);
        cube.setLocalTranslation(x, y, z);
        Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setColor("Color", ColorRGBA.randomColor());
        cube.setMaterial(mat1);
        return cube;
    }

    /** A floor to show that the "shot" can go through several objects. */
    private Geometry makeFloor() {
        Box box = new Box(15, .2f, 15);
        Geometry floor = new Geometry("the Floor", box);
        floor.setLocalTranslation(0, -4, -5);
        Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setColor("Color", ColorRGBA.Gray);
        floor.setMaterial(mat1);
        return floor;
    }

    /** A red ball that marks the last spot that was "hit" by the "shot". */
    private void initMark() {
        Sphere sphere = new Sphere(30, 30, 0.2f);
        mark = new Geometry("BOOM!", sphere);
        Material mark_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat.setColor("Color", ColorRGBA.Red);
        mark.setMaterial(mark_mat);
    }

    /** A centred plus sign to help the player aim. */
    private void initCrossHairs() {
        setDisplayStatView(false);
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        BitmapText ch = new BitmapText(guiFont);
        ch.setSize(guiFont.getCharSet().getRenderedSize() * 2);
        ch.setText("+"); // crosshairs
        ch.setLocalTranslation( // center
        settings.getWidth() / 2 - ch.getLineWidth()/2, settings.getHeight() / 2 + ch.getLineHeight()/2, 0);
        guiNode.attachChild(ch);
    }

    private Spatial makeCharacter() {
        // load a character from jme3test-test-data
        Node golem = (Node) assetManager.loadModel("Models/Oto/OtoOldAnim.j3o");
        golem.scale(0.5f);
        golem.setLocalTranslation(-1.0f, -1.5f, -0.6f);
        golem.setName("Oto");
        
        AnimControl control = golem.getControl(AnimControl.class);
        channel = control.createChannel();
        channel.setAnim("stand");

        // We must add a light to make the model visible
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.1f, -0.7f, -1.0f));
        golem.addLight(sun);
        return golem;
    }
}

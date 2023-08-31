/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ru.develgame.jme3;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

/**
 *
 * @author izemskov
 */
public class HelloLoop extends SimpleApplication {
    private Geometry player;
    private Geometry player1;
    private Geometry player2;
    
    private Geometry player3;
    private Material mat3;
    
    private Geometry player4;
    
    private ColorRGBA currentColor = new ColorRGBA(0.0f, 0.0f, 0.0f, 1.0f);
    private float currentGrad = 0;
    private boolean decreaseGrad = false;
    
    private float currentScale = 1.0f;
    private boolean decreaseScale = true;
    
    private float currentDistance = 0.0f;
    private boolean decreaseDistance = false;
    
    public static void main(String[] args) {
        HelloLoop app = new HelloLoop();
        app.start();
    }
    
    @Override
    public void simpleInitApp() {
        /** this blue box is our player character */
        Box b = new Box(0.5f, 0.5f, 0.5f);
        player = new Geometry("blue cube", b);
        Material mat = new Material(assetManager,
          "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
        player.setMaterial(mat);
        rootNode.attachChild(player);
        
        Box b1 = new Box(0.5f, 0.5f, 0.5f);
        player1 = new Geometry("red cube", b1);
        Material mat1 = new Material(assetManager,
          "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setColor("Color", ColorRGBA.Red);
        player1.setMaterial(mat1);
        player1.move(0.0f, 1.0f, 0.0f);
        rootNode.attachChild(player1);
        
        Box b2 = new Box(0.5f, 0.5f, 0.5f);
        player2 = new Geometry("green cube", b2);
        Material mat2 = new Material(assetManager,
          "Common/MatDefs/Misc/Unshaded.j3md");
        mat2.setColor("Color", ColorRGBA.Green);
        player2.setMaterial(mat2);
        player2.move(0.0f, -1.0f, 0.0f);
        rootNode.attachChild(player2);
        
        Box b3 = new Box(0.5f, 0.5f, 0.5f);
        player3 = new Geometry("shine cube", b3);
        mat3 = new Material(assetManager,
          "Common/MatDefs/Misc/Unshaded.j3md");
        mat3.setColor("Color", currentColor);
        player3.setMaterial(mat3);
        player3.move(0.0f, 2.0f, 0.0f);
        rootNode.attachChild(player3);
        
        Box b4 = new Box(0.5f, 0.5f, 0.5f);
        player4 = new Geometry("rotate cube", b4);
        Material mat4 = new Material(assetManager,
          "Common/MatDefs/Misc/Unshaded.j3md");
        mat4.setColor("Color", ColorRGBA.Orange);
        player4.setMaterial(mat4);
        player4.move(0.0f, -2.0f, 0.0f);
        rootNode.attachChild(player4);
    }

    @Override
    public void simpleUpdate(float tpf) {
        // make the player rotate:
        player.rotate(0, 2 * tpf, 0);
        player1.rotate(0, 4 * tpf, 0);
        
        if (decreaseScale) {
            currentScale -= 0.01f;
            player2.scale(0.99f);
            if (currentScale < 0.5f) {
                decreaseScale = false;
            }
        }
        else {
            currentScale += 0.01f;
            player2.scale(1.01f);
            if (currentScale > 1.5f) {
                decreaseScale = true;
            }
        }
        
        if (decreaseGrad) {
            currentGrad -= 100.0f * tpf;
            if (Math.round(currentGrad) < 0) {
                currentGrad = 0;
                decreaseGrad = false;
            }
        }
        else {
            currentGrad += 100.0f * tpf;
            if (Math.round(currentGrad) > 360) {
                currentGrad = 360;
                decreaseGrad = true;
            }
        }
        
        double toRadians = Math.toRadians(Math.round(currentGrad));
        
        currentColor = new ColorRGBA((float) ((Math.sin(toRadians) + 1.0d) * 0.5d), 
                (float) ((Math.cos(toRadians) + 1.0d) * 0.5d), 
                (float) ((Math.sin(toRadians) + 1.0d) * 0.5d), 
                1.0f);
        mat3.setColor("Color", currentColor);
        player3.setMaterial(mat3);
        
        player4.rotate(2 * tpf, 0, 0);
        
        if (decreaseDistance) {
            currentDistance -= 2 * tpf;
            if (currentDistance < -4.0f) {
                decreaseDistance = false;
            }
            
            player4.move(0, 0, 2 * tpf);
        }
        else {
            currentDistance += 2 * tpf;
            if (currentDistance > 4.0f) {
                decreaseDistance = true;
            }
            
            player4.move(0, 0, -2 * tpf);
        }
        
        
    }
}

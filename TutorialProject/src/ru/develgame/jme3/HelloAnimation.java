/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ru.develgame.jme3;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.LoopMode;
import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.debug.SkeletonDebugger;

/**
 *
 * @author pasca
 */
public class HelloAnimation extends SimpleApplication implements AnimEventListener {
    private AnimChannel channel;
    private AnimChannel channel2;
    private AnimControl control;
    Node player;

    public static void main(String[] args) {
        HelloAnimation app = new HelloAnimation();
        app.start();
    }
    
    @Override
    public void simpleInitApp() {
        viewPort.setBackgroundColor(ColorRGBA.LightGray);
        initKeys();
        
        DirectionalLight dl = new DirectionalLight();
        dl.setDirection(new Vector3f(-0.1f, -1f, -1).normalizeLocal());
        rootNode.addLight(dl);
        
        player = (Node) assetManager.loadModel("Models/Oto/OtoOldAnim.j3o");
        player.setLocalScale(0.5f);
        rootNode.attachChild(player);
        
        control = player.getControl(AnimControl.class);
        control.addListener(this);
        
        channel = control.createChannel();
        channel.setAnim("stand");
        
        channel2 = control.createChannel();
        channel2.setAnim("stand");
        
        SkeletonDebugger skeletonDebug = new SkeletonDebugger("skeleton", control.getSkeleton());
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Green);
        mat.getAdditionalRenderState().setDepthTest(false);
        skeletonDebug.setMaterial(mat);
        player.attachChild(skeletonDebug);
    }
    
    private void initKeys() {
        inputManager.addMapping("Walk", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("Pull", new MouseButtonTrigger(MouseInput.BUTTON_LEFT) );
        inputManager.addListener(actionListener, "Walk", "Pull");
    }
    
    private ActionListener actionListener = new ActionListener() {
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("Walk") && !keyPressed) {
                if (!channel.getAnimationName().equals("Walk")) {
                    channel.setAnim("Walk", 0.50f);
                    channel.setLoopMode(LoopMode.Loop);
                }
            } else if (name.equals("Pull") && !keyPressed) {
                if (!channel2.getAnimationName().equals("pull")) {
                    channel2.setAnim("pull", 0.50f);
                    channel2.setLoopMode(LoopMode.Loop);
                }
            }
        }
    };

    @Override
    public void onAnimCycleDone(AnimControl ac, AnimChannel ac1, String animName) {
        if (animName.equals("Walk")) {
            channel.setAnim("stand", 0.50f);
            channel.setLoopMode(LoopMode.DontLoop);
            channel.setSpeed(1f);
        }
        
        if (animName.equals("pull")) {
            channel2.setAnim("stand", 0.50f);
            channel2.setLoopMode(LoopMode.DontLoop);
            channel2.setSpeed(1f);
        }
    }

    @Override
    public void onAnimChange(AnimControl ac, AnimChannel ac1, String string) {
    }
}

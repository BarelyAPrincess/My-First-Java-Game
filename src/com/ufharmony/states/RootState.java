package com.ufharmony.states;

import com.jme3.app.state.AbstractAppState;
import com.jme3.scene.Node;

public class RootState extends AbstractAppState {

    private Node rootNode = new Node("Root Node");

    public Node getRootNode(){
        return rootNode;
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);

        rootNode.updateLogicalState(tpf);
        rootNode.updateGeometricState();
    }

}
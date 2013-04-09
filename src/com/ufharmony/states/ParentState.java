package com.ufharmony.states;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.scene.Node;
import com.ufharmony.Main;

public class ParentState extends AbstractAppState
{
	private Main app;
	
	private Node x = new Node( "x" );
	
	public Node getX()
	{
		return x;
	}
	
	@Override
	public void initialize( AppStateManager stateManager, Application app )
	{
		super.initialize( stateManager, app );
		this.app = (Main) app;
		
		this.app.getRootNode().attachChild( getX() );
		//this.app.doSomething();
	}
	
	@Override
	public void cleanup()
	{
		super.cleanup();
		this.app.getRootNode().detachChild( getX() );
		//this.app.doSomethingElse();
	}
	
	@Override
   public void setEnabled(boolean enabled) {
     super.setEnabled(enabled);
     if(enabled){
       this.app.getRootNode().attachChild(getX());
       //this.app.doSomethingElse();
     } else {
       // take away everything not needed while this state is PAUSED
     }
   }
	
	@Override
   public void update(float tpf) {
     this.app.getRootNode().getChild("blah").scale(tpf);
     //x.setUserData(...);
   }
}

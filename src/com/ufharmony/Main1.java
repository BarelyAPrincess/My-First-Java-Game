package com.ufharmony;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.input.controls.ActionListener;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeContext;
import com.ufharmony.states.RootState;

public class Main1 extends Application
{
	public static void main( String[] args )
	{
		Main1 app = new Main1();
		app.start();
	}
	
	@Override
	public void start( JmeContext.Type contextType )
	{
		AppSettings settings = new AppSettings( true );
		settings.setResolution( 1024, 768 );
		setSettings( settings );
		
		super.start( contextType );
	}
	
	@Override
	public void initialize()
	{
		super.initialize();
		
		RootState state = new RootState();
		viewPort.attachScene( state.getRootNode() );
		stateManager.attach( state );
		
		NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay( assetManager, inputManager, audioRenderer, guiViewPort );
		niftyDisplay.getNifty().fromXml( "Interface/HomeScreen/layout.xml", "start" );
		guiViewPort.addProcessor( niftyDisplay );
	}
	
	@Override
	public void update()
	{
		super.update();
		
		float tpf = timer.getTimePerFrame();
		
		stateManager.update( tpf );
		stateManager.render( renderManager );
		
		renderManager.render( tpf, context.isRenderable() );
	}
	
	@Override
	public void destroy()
	{
		super.destroy();
		
		System.out.println( "Destroy" );
	}
}

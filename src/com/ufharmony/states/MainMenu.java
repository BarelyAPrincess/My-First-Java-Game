package com.ufharmony.states;

import com.jme3.app.state.AbstractAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.ufharmony.Main;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

public class MainMenu extends AbstractAppState implements ScreenController
{
	private Nifty nifty;
	
	public void simpleInitApp()
	{
		Main.getInstance().setPauseOnLostFocus( false );
		
		Box b = new Box( Vector3f.ZERO, 1, 1, 1 );
		Geometry geom = new Geometry( "Box", b );
		Material mat = new Material( Main.getInstance().getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md" );
		mat.setTexture( "ColorMap", Main.getInstance().getAssetManager().loadTexture( "Interface/Logo/Monkey.jpg" ) );
		geom.setMaterial( mat );
		Main.getInstance().getRootNode().attachChild( geom );
		
		NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay( Main.getInstance().getAssetManager(), Main.getInstance().getInputManager(), Main.getInstance().getAudioRenderer(), Main.getInstance().getGuiViewPort() );
		nifty = niftyDisplay.getNifty();
		nifty.fromXml( "Interface/Nifty/HelloJme.xml", "start", this );
		
		// attach the nifty display to the gui view port as a processor
		Main.getInstance().getGuiViewPort().addProcessor( niftyDisplay );
		
		Main.getInstance().getFlyByCamera().setEnabled(false);
		Main.getInstance().getFlyByCamera().setDragToRotate(true);
		Main.getInstance().getInputManager().setCursorVisible( true );
	}
	
	public void bind( Nifty nifty, Screen screen )
	{
		System.out.println( "bind( " + screen.getScreenId() + ")" );
	}
	
	public void onStartScreen()
	{
		System.out.println( "onStartScreen" );
	}
	
	public void onEndScreen()
	{
		System.out.println( "onEndScreen" );
	}
	
	public void quit()
	{
		nifty.gotoScreen( "end" );
	}
}

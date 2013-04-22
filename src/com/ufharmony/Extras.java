package com.ufharmony;

import com.jme3.font.BitmapText;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;

public class Extras
{
	public Main app;
	
	public Extras( Main theApp )
	{
		app = theApp;
	}
	
	void setUpKeys()
	{
		InputManager inputManager = app.getInputManager();
		
		inputManager.addMapping( "Left", new KeyTrigger( KeyInput.KEY_A ) );
		inputManager.addMapping( "Right", new KeyTrigger( KeyInput.KEY_D ) );
		inputManager.addMapping( "Up", new KeyTrigger( KeyInput.KEY_W ) );
		inputManager.addMapping( "Down", new KeyTrigger( KeyInput.KEY_S ) );
		inputManager.addMapping( "Jump", new KeyTrigger( KeyInput.KEY_SPACE ) );
		inputManager.addListener( app, "Left" );
		inputManager.addListener( app, "Right" );
		inputManager.addListener( app, "Up" );
		inputManager.addListener( app, "Down" );
		inputManager.addListener( app, "Jump" );
		
		inputManager.addMapping( "F5", new KeyTrigger( KeyInput.KEY_F5 ) );
		inputManager.addListener( app, "F5" );
		
		inputManager.addMapping( "Pause", new KeyTrigger( KeyInput.KEY_P ) );
		inputManager.addListener( app, "Pause" );
		
		inputManager.addMapping( "break", new MouseButtonTrigger( MouseInput.BUTTON_LEFT ) );
		inputManager.addListener( app, "break" );
		
		inputManager.addMapping( "place", new MouseButtonTrigger( MouseInput.BUTTON_RIGHT ) );
		inputManager.addListener( app, "place" );
		
		inputManager.addMapping( "Derp", new KeyTrigger( KeyInput.KEY_LCONTROL ) );
		inputManager.addListener( app, "Derp" );
		
		inputManager.addMapping( "MainMenu", new KeyTrigger( KeyInput.KEY_Q ) );
		inputManager.addListener( app, "MainMenu" );
		
		inputManager.addMapping( "Respawn", new KeyTrigger( KeyInput.KEY_F ) );
		inputManager.addListener( app, "Respawn" );
		
		// inputManager.deleteMapping( "FLYCAM_ZoomIn" );
		// inputManager.deleteMapping( "FLYCAM_ZoomOut" );
		
		inputManager.addMapping( "inv", new MouseAxisTrigger( MouseInput.AXIS_WHEEL, true ) );
		inputManager.addListener( app, "inv" );
		
		inputManager.addMapping( "MainMenu2", new KeyTrigger( KeyInput.KEY_ESCAPE ) );
		inputManager.addListener( app, "MainMenu2" );
	}
	
	void setLegal()
	{
		app.getGuiNode().detachAllChildren();
		app.guiFont = app.getAssetManager().loadFont( "Interface/Fonts/Default.fnt" );
		BitmapText legal = new BitmapText( app.guiFont, false );
		legal.setSize( app.guiFont.getCharSet().getRenderedSize() );
		legal.setText( "Copyright 2013 Apple Bloom Company - Not Final Product - DO NOT REDISTRIBUTE" );
		legal.setLocalTranslation( 300, legal.getLineHeight(), 0 );
		app.getGuiNode().attachChild( legal );
	}
	
	void initCrossHairs()
	{
		app.guiFont = app.getAssetManager().loadFont( "Interface/Fonts/Default.fnt" );
		BitmapText ch = new BitmapText( app.guiFont, false );
		ch.setSize( app.guiFont.getCharSet().getRenderedSize() * 2 );
		ch.setText( "+" );
		ch.setLocalTranslation( app.settings.getWidth() / 2 - app.guiFont.getCharSet().getRenderedSize() / 3 * 2, app.settings.getHeight() / 2 + ch.getLineHeight() / 2, 0 );
		app.getGuiNode().attachChild( ch );
	}
	
	public static boolean isValidIndex ( Object[] o, int index )
	{
		try
		{
			Object t = o[index];
		}
		catch ( ArrayIndexOutOfBoundsException e )
		{
			return false;
		}
		
		return true;
	}
}

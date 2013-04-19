package com.ufharmony.states;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.FlyByCamera;
import com.ufharmony.CameraBase;

public class FlyCam extends AbstractAppState
{
	private Application app;
	private CameraBase flyCam;
	
	public FlyCam()
	{
	}
	
	public void setCamera( CameraBase cam )
	{
		this.flyCam = cam;
	}
	
	public CameraBase getCamera()
	{
		return flyCam;
	}
	
	@Override
	public void initialize( AppStateManager stateManager, Application app )
	{
		super.initialize( stateManager, app );
		
		this.app = app;
		
		if ( app.getInputManager() != null )
		{
			
			if ( flyCam == null )
			{
				flyCam = new CameraBase( app.getCamera() );
			}
			
			flyCam.registerWithInput( app.getInputManager() );
		}
	}
	
	@Override
	public void setEnabled( boolean enabled )
	{
		super.setEnabled( enabled );
		
		flyCam.setEnabled( enabled );
	}
	
	@Override
	public void cleanup()
	{
		super.cleanup();
		
		if ( flyCam != null )
			flyCam.unregisterInput();
	}
	
}

package com.ufharmony.objects;

import com.ufharmony.Main;
import com.ufharmony.grid.UniqueSquare;

public class ObjectLamp extends ObjectBase
{
	public ObjectLamp(int id)
	{
		super( id );
		
		myModel = Main.getInstance().getAssetManager().loadModel( "Model/chest.obj" );
	}

	public void customizeMe( UniqueSquare us )
	{
		us.setScale( 0.5f );
		us.setLightLevel( 1.0f );
	}
}

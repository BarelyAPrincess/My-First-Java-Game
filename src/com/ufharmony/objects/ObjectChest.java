package com.ufharmony.objects;

import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.ufharmony.Main;
import com.ufharmony.grid.Square;
import com.ufharmony.grid.UniqueSquare;

public class ObjectChest extends ObjectBase
{
	public ObjectChest(int objectId)
	{
		super( objectId );
		
		myModel = Main.getInstance().getAssetManager().loadModel( "Model/chest.obj" );
	}
	
	public void customizeMe( UniqueSquare us )
	{
		us.setScale( 0.5f );
		//us.setOffset( new Vector3f( 0, 10, 0 ) );
	}
}

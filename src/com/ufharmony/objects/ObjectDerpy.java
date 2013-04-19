package com.ufharmony.objects;

import com.jme3.math.Vector3f;
import com.ufharmony.Main;
import com.ufharmony.grid.UniqueSquare;

public class ObjectDerpy extends ObjectBase
{
	public ObjectDerpy(int objectId)
	{
		super( objectId );
		
		myModel = Main.getInstance().getAssetManager().loadModel( "Model/Derpy/derpy_all-in-one.OBJ" );
	}
	
	public void customizeMe( UniqueSquare us )
	{
		us.setScale( 0.50f );
		us.setOffset( new Vector3f( 0, 0.4f, 0 ) );
	}
}

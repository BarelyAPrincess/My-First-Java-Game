package com.ufharmony.objects;

import com.jme3.scene.Spatial;
import com.ufharmony.Main;
import com.ufharmony.grid.Square;

public class ObjectChest extends ObjectBase
{
	public ObjectChest(int objectId)
	{
		super( objectId );
		
		myModel = Main.getInstance().getAssetManager().loadModel( "Model/Derpy/derpy_all-in-one.OBJ" );
	}
}

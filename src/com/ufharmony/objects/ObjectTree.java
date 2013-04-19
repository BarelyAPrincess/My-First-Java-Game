package com.ufharmony.objects;

import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.ufharmony.Main;
import com.ufharmony.grid.UniqueSquare;

public class ObjectTree extends ObjectBase
{
	public ObjectTree(int objectId)
	{
		super( objectId );
		
		myModel = Main.getInstance().getAssetManager().loadModel( "Model/baum.obj" );
	}
	
	public void customizeMe( UniqueSquare us )
	{
		//us.setScale( 0.5f );
		us.setOffset( new Vector3f( 0, -1.0f, 0 ) );
		us.setMaterial( new Material( Main.getInstance().getAssetManager(), "Common/MatDefs/Misc/ShowNormals.j3md" ) );
	}
}

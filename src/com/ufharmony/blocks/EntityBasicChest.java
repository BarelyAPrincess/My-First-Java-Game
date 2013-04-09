package com.ufharmony.blocks;

import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.ufharmony.Main;

public class EntityBasicChest extends EntityBaseStorage
{
	public EntityBasicChest(Vector3f loc)
	{
		super( loc );
	}
	
	@Override
	public Material getMaterial()
	{
		return new Material( Main.getInstance().getAssetManager(), "Common/MatDefs/Misc/ShowNormals.j3md" );
	}
	
	@Override
	public Spatial getModel()
	{
		return Main.getInstance().getAssetManager().loadModel( "Models/Entity/chest.obj" );
	}
}

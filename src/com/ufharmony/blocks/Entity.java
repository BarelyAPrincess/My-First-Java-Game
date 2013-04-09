package com.ufharmony.blocks;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.ufharmony.Main;

public class Entity
{
	Spatial obj = null;
	
	public Entity(Vector3f loc)
	{
		if ( loc == null )
			return;
		
		obj = getModel();
		Material mat_default = getMaterial();
		obj.setMaterial( mat_default );
		
		setLocation( loc );
		setScale( 0.5f );
		
		RigidBodyControl derpy_phy = new RigidBodyControl( 0.5f );
		obj.addControl( derpy_phy );
		Main.getBullet().getPhysicsSpace().add( derpy_phy );
		
		Main.getInstance().getRootNode().attachChild( obj );
		
		// Box box1 = new Box( Vector3f.ZERO, 1, 1, 1 );
	}
	
	/**
	 * Override this class to set the Entity Material
	 * 
	 * @return Material
	 */
	public Material getMaterial()
	{
		return new Material( Main.getInstance().getAssetManager(), "Common/MatDefs/Misc/ShowNormals.j3md" );
	}
	
	/**
	 * Override this class to set the Entity Model
	 * 
	 * @return Spatial
	 */
	public Spatial getModel()
	{
		return Main.getInstance().getAssetManager().loadModel( "Model/Derpy/derpy_all-in-one.OBJ" );
	}
	
	public void setLocation( Vector3f loc )
	{
		obj.setLocalTranslation( loc );
	}
	
	public void setScale( float f )
	{
		obj.setLocalScale( f );
	}
}

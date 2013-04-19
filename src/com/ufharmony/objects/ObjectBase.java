package com.ufharmony.objects;

import com.jme3.scene.Spatial;
import com.ufharmony.blocks.BlockBase;
import com.ufharmony.grid.Square;
import com.ufharmony.grid.UniqueSquare;

public abstract class ObjectBase extends Square
{
	// Global Properties
	private static ObjectBase[] objectsList = new ObjectBase[65565];
	
	// Indivigual Properties
	protected Spatial myModel;

	public ObjectBase(byte id)
	{
		super( id );
	}
	
	public ObjectBase(int id)
	{
		super( id );
	}
	
	public static void registerObjects()
	{
		objectsList[1] = new ObjectChest( 1 );
		objectsList[2] = new ObjectDerpy( 2 );
		objectsList[3] = new ObjectTree( 3 );
		objectsList[4] = new ObjectLamp( 4 );
	}
	
	public static ObjectBase getGlobalObject( Class<? extends Square> blockClass )
	{
		for ( ObjectBase obj : objectsList )
		{
			if ( obj != null && obj.getClass().equals( blockClass ) )
				return obj;
		}
		
		return null;
	}
	
	@Override
	public Class<? extends Square> getParentClass()
	{
		return ObjectBase.class;
	}
	
	public Spatial getModel ()
	{
		return myModel;
	}
	
	abstract public void customizeMe( UniqueSquare uniqueSquare );
}

package com.ufharmony.objects;

import com.jme3.scene.Spatial;
import com.ufharmony.blocks.BlockBase;
import com.ufharmony.grid.Square;

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
}

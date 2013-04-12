package com.ufharmony.grid;

import com.ufharmony.blocks.BlockBase;
import com.ufharmony.objects.ObjectBase;

public class UniqueSquare
{
	private Class<? extends Square> parentClass = null;
	
	public UniqueSquare( Square ob )
	{
		this.type = ob.getId();
		
		if ( ob instanceof BlockBase )
			this.skin = ((BlockBase) ob).getSkin();
		
		parentClass = ob.getParentClass();
	}
	
	public Class<? extends Square> getParentClass()
	{
		return parentClass;
	}
	
	public Square getParent()
	{
		return null;
	}
	
	private byte type;
	private BlockSkin skin;
	
	public byte getType()
	{
		return type;
	}
	
	public BlockBase getBlock()
	{
		return BlockBase.getBlock( type );
	}
	
	public BlockSkin getSkin()
	{
		return skin;
	}
}
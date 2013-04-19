package com.ufharmony.grid;

import com.ufharmony.blocks.BlockBase;
import com.ufharmony.objects.ObjectBase;

public abstract class Square
{
	protected byte squareId = 0;
	
	public byte getId()
	{
		return squareId;
	}
	
	public Square(int blockId)
	{
		this.squareId = (byte) blockId;
	}
	
	public Square(byte blockId)
	{
		this.squareId = blockId;
	}
	
	public byte getSquareId()
	{
		return squareId;
	}
	
	public static enum Face
	{
		Top, Bottom, North, South, West, East;
	}
	
	public static Square getGlobalObject( Class<? extends Square> theClass )
	{
		Square ob = BlockBase.getGlobalBlock( theClass );
		
		if ( ob != null )
			return ob;
		
		ob =  ObjectBase.getGlobalObject( theClass );
		
		return ob;
	}
	
	abstract public Class<? extends Square> getParentClass();
	abstract public void customizeMe( UniqueSquare uniqueSquare );
}
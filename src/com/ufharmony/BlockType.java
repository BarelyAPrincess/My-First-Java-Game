package com.ufharmony;

import com.ufharmony.blocks.BlockBase;

public class BlockType
{
	private byte type;
	private BlockSkin skin;
	
	public BlockType(byte type, BlockSkin skin)
	{
		this.type = type;
		this.skin = skin;
	}
	
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

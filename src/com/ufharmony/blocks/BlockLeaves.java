package com.ufharmony.blocks;

import com.ufharmony.grid.BlockSkin;
import com.ufharmony.grid.Skin_TextureLocation;

public class BlockLeaves extends BlockBase
{
	public BlockLeaves ( int blockId )
	{
		super( blockId );
		
		this.isLeaves = true;
	}
	
	@Override
	public BlockSkin getSkin()
	{
		return new BlockSkin( new Skin_TextureLocation( 4, 8 ), true );
	}
}

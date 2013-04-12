package com.ufharmony.blocks;

import com.ufharmony.grid.BlockSkin;
import com.ufharmony.grid.Skin_TextureLocation;

public class BlockDirt extends BlockBase
{
	public BlockDirt(int blockId)
	{
		super( blockId );
	}
	
	@Override
	public BlockSkin getSkin()
	{
		return new BlockSkin( new Skin_TextureLocation( 2, 0 ), false );
	}
}

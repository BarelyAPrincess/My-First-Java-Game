package com.ufharmony.blocks;

import com.ufharmony.grid.BlockSkin;
import com.ufharmony.grid.Skin_TextureLocation;

public class BlockWater extends BlockBase
{
	public BlockWater(int blockId)
	{
		super( blockId );
	}
	
	@Override
	public BlockSkin getSkin()
	{
		return new BlockSkin( new Skin_TextureLocation( 3, 4 ), true );
	}
}

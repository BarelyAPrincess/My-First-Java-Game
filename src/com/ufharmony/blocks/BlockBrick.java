package com.ufharmony.blocks;

import com.ufharmony.grid.BlockSkin;
import com.ufharmony.grid.Skin_TextureLocation;

public class BlockBrick extends BlockBase
{
	public BlockBrick(int blockId)
	{
		super( blockId );
	}
	
	@Override
	public BlockSkin getSkin()
	{
		return new BlockSkin( new Skin_TextureLocation( 7, 0 ), false );
	}
}

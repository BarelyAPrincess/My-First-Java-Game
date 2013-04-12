package com.ufharmony.blocks;

import com.ufharmony.grid.BlockSkin;
import com.ufharmony.grid.Skin_TextureLocation;

public class BlockStone extends BlockBase
{
	public BlockStone(int blockId)
	{
		super( blockId );
	}
	
	@Override
	public BlockSkin getSkin()
	{
		return new BlockSkin( new Skin_TextureLocation( 1, 0 ), false );
	}
}

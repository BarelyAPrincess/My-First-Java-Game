package com.ufharmony.blocks;

import com.ufharmony.BlockManager;
import com.ufharmony.BlockSkin;
import com.ufharmony.BlockSkin_TextureLocation;

public class BlockWater extends BlockBase
{
	public BlockWater(int blockId)
	{
		super( blockId );
		
		BlockManager.register( BlockWater.class, blockId, new BlockSkin( new BlockSkin_TextureLocation( 3, 4 ), true ) );
	}
}

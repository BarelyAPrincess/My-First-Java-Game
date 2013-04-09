package com.ufharmony.blocks;

import com.ufharmony.BlockManager;
import com.ufharmony.BlockSkin;
import com.ufharmony.BlockSkin_TextureLocation;

public class BlockStone extends BlockBase
{
	public BlockStone(int blockId)
	{
		super( blockId );
		
		BlockManager.register( BlockStone.class, blockId, new BlockSkin( new BlockSkin_TextureLocation( 1, 0 ), false ) );
	}
}

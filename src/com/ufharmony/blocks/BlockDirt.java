package com.ufharmony.blocks;

import com.ufharmony.Block;
import com.ufharmony.BlockManager;
import com.ufharmony.BlockSkin;
import com.ufharmony.BlockSkin_TextureLocation;


public class BlockDirt extends BlockBase
{
	public BlockDirt(int blockId)
	{
		super( blockId );
		
		BlockManager.register( BlockDirt.class, blockId, new BlockSkin( new BlockSkin_TextureLocation( 2, 0 ), false ) );
	}
}

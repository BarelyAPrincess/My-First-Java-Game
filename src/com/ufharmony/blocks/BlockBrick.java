package com.ufharmony.blocks;

import com.ufharmony.BlockManager;
import com.ufharmony.BlockSkin;
import com.ufharmony.BlockSkin_TextureLocation;

public class BlockBrick extends BlockBase
{
	public BlockBrick(int blockId)
	{
		super( blockId );
		
		BlockManager.register( BlockBrick.class, blockId, new BlockSkin( new BlockSkin_TextureLocation( 7, 0 ), false ) );
	}
}

package com.ufharmony.blocks;

import com.ufharmony.BlockManager;
import com.ufharmony.BlockSkin;
import com.ufharmony.BlockSkin_TextureLocation;

public class BlockSapling extends BlockBase
{
	public BlockSapling(int blockId)
	{
		super( blockId );
		
		BlockManager.register( BlockSapling.class, blockId, new BlockSkin( new BlockSkin_TextureLocation( 15, 0 ), true ) );
	}
}

package com.ufharmony.blocks;

import com.ufharmony.BlockManager;
import com.ufharmony.BlockSkin;
import com.ufharmony.BlockSkin_TextureLocation;

public class BlockSponge extends BlockBase
{
	public BlockSponge(int blockId)
	{
		super( blockId );
		
		BlockManager.register( BlockSponge.class, blockId, new BlockSkin( new BlockSkin_TextureLocation( 0, 3 ), false ) );
	}
}

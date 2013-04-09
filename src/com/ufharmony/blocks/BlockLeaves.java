package com.ufharmony.blocks;

import com.ufharmony.BlockManager;
import com.ufharmony.BlockSkin;
import com.ufharmony.BlockSkin_TextureLocation;

public class BlockLeaves extends BlockBase
{
	public BlockLeaves ( int blockId )
	{
		super( blockId );
		
		this.isLeaves = true;
		BlockManager.register( BlockLeaves.class, blockId, new BlockSkin( new BlockSkin_TextureLocation( 4, 8 ), true ) );
	}
}

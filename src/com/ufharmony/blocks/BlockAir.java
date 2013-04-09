package com.ufharmony.blocks;

import com.ufharmony.BlockManager;
import com.ufharmony.BlockSkin;
import com.ufharmony.BlockSkin_TextureLocation;

public class BlockAir extends BlockBase
{
	public BlockAir(int blockId)
	{
		super( blockId );
		
		BlockManager.register( BlockAir.class, blockId, new BlockSkin( new BlockSkin_TextureLocation( 0, 0 ), false ) );
	}
}

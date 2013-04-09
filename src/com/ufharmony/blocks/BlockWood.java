package com.ufharmony.blocks;

import com.ufharmony.BlockManager;
import com.ufharmony.BlockSkin;
import com.ufharmony.BlockSkin_TextureLocation;

public class BlockWood extends BlockBase
{
	public BlockWood(int blockId)
	{
		super( blockId );
		
		this.isWood = true;
		BlockManager.register( BlockWood.class, 101, new BlockSkin( new BlockSkin_TextureLocation[] { new BlockSkin_TextureLocation( 5, 1 ), new BlockSkin_TextureLocation( 5, 1 ), new BlockSkin_TextureLocation( 4, 1 ), new BlockSkin_TextureLocation( 4, 1 ), new BlockSkin_TextureLocation( 4, 1 ), new BlockSkin_TextureLocation( 4, 1 ) }, false ) );
	}
}

package com.ufharmony.blocks;

import com.ufharmony.grid.BlockSkin;
import com.ufharmony.grid.Skin_TextureLocation;

public class BlockWood extends BlockBase
{
	public BlockWood(int blockId)
	{
		super( blockId );
		
		this.isWood = true;
	}
	
	@Override
	public BlockSkin getSkin()
	{
		return new BlockSkin( new Skin_TextureLocation[] { new Skin_TextureLocation( 5, 1 ), new Skin_TextureLocation( 5, 1 ), new Skin_TextureLocation( 4, 1 ), new Skin_TextureLocation( 4, 1 ), new Skin_TextureLocation( 4, 1 ), new Skin_TextureLocation( 4, 1 ) }, false );
	}
}

package com.ufharmony.blocks;

import com.ufharmony.grid.BlockSkin;
import com.ufharmony.grid.Skin_TextureLocation;

public class BlockBedrock extends BlockBase
{
	public BlockBedrock(int i)
	{
		super( i );
	}

	@Override
	public BlockSkin getSkin()
	{
		return new BlockSkin( new Skin_TextureLocation( 1, 1 ), false );
	}
}
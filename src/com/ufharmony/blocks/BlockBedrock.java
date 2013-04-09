package com.ufharmony.blocks;

import com.ufharmony.BlockManager;
import com.ufharmony.BlockSkin;
import com.ufharmony.BlockSkin_TextureLocation;

public class BlockBedrock extends BlockBase
{
	public BlockBedrock(int i)
	{
		super( i );
		
		BlockManager.register( BlockBedrock.class, i, new BlockSkin( new BlockSkin_TextureLocation( 1, 1 ), false ) );
	}	
}
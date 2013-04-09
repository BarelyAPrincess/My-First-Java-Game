package com.ufharmony.blocks;

import com.ufharmony.Block;
import com.ufharmony.BlockChunkControl;
import com.ufharmony.BlockManager;
import com.ufharmony.BlockSkin;
import com.ufharmony.BlockSkin_TextureLocation;
import com.ufharmony.Vector3Int;

public class BlockGrass extends BlockBase
{
	public BlockGrass( int blockId )
	{
		super( blockId );
		
		BlockManager.register( BlockGrass.class, blockId, new BlockSkin( new BlockSkin_TextureLocation[] { new BlockSkin_TextureLocation( 15, 1 ), new BlockSkin_TextureLocation( 3, 0 ), new BlockSkin_TextureLocation( 2, 0 ) }, false )
		{
			protected int getTextureLocationIndex( BlockChunkControl chunk, Vector3Int blockLocation, Block.Face face )
			{
				//System.out.println( "WHAT ONE EARTH DOES THIS METHOD DO?????????????????????????" );
				
				if ( chunk.isBlockOnSurface( blockLocation ) )
				{
					switch ( face )
					{
						case Top:
							return 0;
						case Bottom:
							return 2;
					}
					return 1;
				}
				return 2;
			}
		} );
	}
}
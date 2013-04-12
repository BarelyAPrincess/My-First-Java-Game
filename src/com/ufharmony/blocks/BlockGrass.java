package com.ufharmony.blocks;

import com.ufharmony.grid.BlockSkin;
import com.ufharmony.grid.ChunkControl;
import com.ufharmony.grid.Skin_TextureLocation;
import com.ufharmony.grid.Square;
import com.ufharmony.utils.Vector3Int;

public class BlockGrass extends BlockBase
{
	public BlockGrass( int blockId )
	{
		super( blockId );
	}
	
	@Override
	public BlockSkin getSkin()
	{
		return new BlockSkin( new Skin_TextureLocation[] { new Skin_TextureLocation( 15, 1 ), new Skin_TextureLocation( 3, 0 ), new Skin_TextureLocation( 2, 0 ) }, false )
		{
			protected int getTextureLocationIndex( ChunkControl chunk, Vector3Int blockLocation, Square.Face face )
			{
				if ( chunk.isSquareOnSurface( blockLocation ) )
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
		};
	}
}
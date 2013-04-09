package com.ufharmony;

import java.util.HashMap;

import com.ufharmony.utils.objects;

public class BlockManager
{
	private static HashMap<Class<? extends Block>, BlockType> BLOCK_TYPES = new HashMap();
	private static BlockType[] TYPES_BLOCKS = new BlockType[256];
	private static byte nextBlockType = 1;
	
	public static boolean register( Class<? extends Block> blockClass, int blockId, BlockSkin skin )
	{
		return register( blockClass, (byte) blockId, skin );
	}
	
	public static boolean register( Class<? extends Block> blockClass, byte blockId, BlockSkin skin )
	{
		if ( TYPES_BLOCKS[blockId] == null )
		{
			BlockType blockType = new BlockType( blockId, skin );
			BLOCK_TYPES.put( blockClass, blockType );
			TYPES_BLOCKS[blockId] = blockType;
			
			return true;
		}
		
		return false;
	}
	
	public static byte register( Class<? extends Block> blockClass, BlockSkin skin )
	{
		do
		{
			nextBlockType = (byte) ( nextBlockType + 1 );
		}
		while (!register(blockClass, nextBlockType, skin));
		
		return nextBlockType;
	}
	
	public static Class<? extends Block> getBlockClass ( BlockType type )
	{
		if ( BLOCK_TYPES.containsValue( type ) )
		{
			return objects.reverse( BLOCK_TYPES ).get( type );
		}
		
		return null;
	}
	
	public static BlockType getType( Class<? extends Block> blockClass )
	{
		return (BlockType) BLOCK_TYPES.get( blockClass );
	}
	
	public static BlockType getType( byte type )
	{
		return TYPES_BLOCKS[type];
	}
}

package com.ufharmony.blocks;

import com.ufharmony.grid.BlockSkin;
import com.ufharmony.grid.Square;

public abstract class BlockBase extends Square
{
	private static BlockBase[] blocksList = new BlockBase[65565];
	
	public BlockBase(byte blockId)
	{
		super( blockId );
	}
	
	public BlockBase(int blockId)
	{
		super( blockId );
	}
	
	public static void registerBlocks()
	{
		blocksList[100] = new BlockGrass( 100 );
		blocksList[101] = new BlockWood( 101 );
		blocksList[102] = new BlockStone( 102 );
		blocksList[103] = new BlockWater( 103 );
		blocksList[104] = new BlockBrick( 104 );
		blocksList[105] = new BlockDirt( 105 );
		blocksList[106] = new BlockLeaves( 106 );
		blocksList[107] = new BlockSponge( 107 );
		blocksList[108] = new BlockSapling( 108 );
		blocksList[109] = new BlockBedrock( 109 );
	}
	
	abstract public BlockSkin getSkin();
	
	boolean isLeaves = false;
	boolean isWood = false;
	
	public boolean isLeaves()
	{
		return this.isLeaves;
	}
	
	public boolean isWood()
	{
		return this.isWood;
	}
	
	public boolean canSustainPlant( Face top, Class<? extends BlockBase> class1 )
	{
		return true;
	}
	
	public boolean canBeReplacedByLeaves()
	{
		return true;
	}
	
	public boolean isLeaves( int x, int y, int z )
	{
		return false;
		// return Main.getWorld().getBlock( x, y, z ).isLeaves();
	}
	
	public static BlockBase getBlock( byte id )
	{
		return blocksList[id];
	}
	
	public static BlockBase getBlock( int id )
	{
		return blocksList[id];
	}
	
	public void onBlockDestroyedByExplosion()
	{
		
	}
	
	public static BlockBase getGlobalBlock( Class<? extends Square> blockClass )
	{
		for ( BlockBase obj : blocksList )
		{
			if ( obj != null && obj.getClass().equals( blockClass ) )
				return obj;
		}
		
		return null;
	}
	
	@Override
	public Class<? extends Square> getParentClass()
	{
		return BlockBase.class;
	}
}

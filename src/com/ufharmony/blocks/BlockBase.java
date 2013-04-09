package com.ufharmony.blocks;

import com.ufharmony.Block;
import com.ufharmony.BlockManager;
import com.ufharmony.BlockSkin;
import com.ufharmony.BlockSkin_TextureLocation;
import com.ufharmony.Block.Face;

public class BlockBase extends Block
{
	private static BlockBase[] blocksList = new BlockBase[65565];
	
	boolean isLeaves = false;
	boolean isWood = false;
	byte blockId = 0;
	
	public BlockBase(int blockId)
	{
		this.blockId = (byte) blockId;
	}
	
	public BlockBase(byte blockId)
	{
		this.blockId = blockId;
	}
	
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
	
	public static void registerBlocks()
	{
		blocksList[1] = new BlockAir( 1 );
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
	
	public static BlockBase getBlock( byte blockId )
	{
		return blocksList[ blockId ];
	}
	
	public static BlockBase getBlock( int blockId )
	{
		return blocksList[ blockId ];
	}
	
	public byte getBlockId()
	{
		return blockId;
	}
	
	public void onBlockDestroyedByExplosion ()
	{
		
	}
}

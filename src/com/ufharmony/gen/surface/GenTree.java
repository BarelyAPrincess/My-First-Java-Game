package com.ufharmony.gen.surface;

import java.util.Random;

import com.ufharmony.Main;
import com.ufharmony.Vector3Int;
import com.ufharmony.Block.Face;
import com.ufharmony.blocks.BlockBase;
import com.ufharmony.blocks.BlockLeaves;
import com.ufharmony.blocks.BlockSapling;
import com.ufharmony.blocks.BlockWood;
import com.ufharmony.gen.GenBase;

public class GenTree extends GenBase
{
	private final int minTreeHeight;
	
	public GenTree(int minTreeHeight)
	{
		this.minTreeHeight = minTreeHeight;
	}
	
	public GenTree()
	{
		minTreeHeight = 12;
	}

	public boolean generate( Random r, Vector3Int l )
	{
		return generate( r, l.getX(), l.getY(), l.getZ() );
	}
	
	public boolean generate( Random r, int x, int y, int z )
	{
		int thisHeight = r.nextInt( 3 ) + this.minTreeHeight;
		boolean plant = true;
		
		if ( y >= 1 && y + thisHeight + 1 <= 256 )
		{
			int var8;
			byte var9;
			int var11;
			int var12;
			
			for ( var8 = y; var8 <= y + 1 + thisHeight; ++var8 )
			{
				var9 = 1;
				
				if ( var8 == y )
				{
					var9 = 0;
				}
				
				if ( var8 >= y + 1 + thisHeight - 2 )
				{
					var9 = 2;
				}
				
				for ( int var10 = x - var9; var10 <= x + var9 && plant; ++var10 )
				{
					for ( var11 = z - var9; var11 <= z + var9 && plant; ++var11 )
					{
						if ( var8 >= 0 && var8 < 256 )
						{
							BlockBase block = Main.getWorld().getBlockInst( var10, var8, var11 );
							
							if ( block != null )
								var12 = block.getBlockId();
							
							if ( block != null && !block.isLeaves() && !block.canSustainPlant( Face.Top, BlockSapling.class ) && !block.isWood() )
							{
								plant = false;
							}
						}
						else
						{
							plant = false;
						}
					}
				}
			}
			
			if ( !plant )
			{
				return false;
			}
			else
			{
				
				BlockBase soil = Main.getWorld().getBlockInst( x, y - 1, z );
				boolean isSoil = ( soil != null && soil.canSustainPlant( Face.Top, BlockSapling.class ) );
				
				if ( isSoil && y < 256 - thisHeight - 1 )
				{
					//soil.onPlantGrow( x, y - 1, z, x, y, z );
					var9 = 3; // How many levels of leaves on top.
					byte var18 = 0;
					int var13;
					int var14;
					int var15;
					
					for ( var11 = y - var9 + thisHeight; var11 <= y + thisHeight; ++var11 )
					{
						var12 = var11 - ( y + thisHeight );
						var13 = var18 + 1 - var12 / 2;
						
						for ( var14 = x - var13; var14 <= x + var13; ++var14 )
						{
							var15 = var14 - x;
							
							for ( int var16 = z - var13; var16 <= z + var13; ++var16 )
							{
								int var17 = var16 - z;
								
								BlockBase block = Main.getWorld().getBlockInst( var14, var11, var16 );
								
								if ( ( Math.abs( var15 ) != var13 || Math.abs( var17 ) != var13 || r.nextInt( 2 ) != 0 && var12 != 0 ) && ( block == null || block.canBeReplacedByLeaves() ) )
								{
									Main.getWorld().setBlock( var14, var11, var16, BlockLeaves.class );
								}
							}
						}
					}
					
					for ( var11 = 0; var11 < thisHeight; ++var11 )
					{
						BlockBase block = Main.getWorld().getBlockInst( x, y + var11, z );
						
						var12 = ( block != null ) ? block.getBlockId() : 0;
						
						if ( var12 == 0 || block == null || block.isLeaves() )
						{
							Main.getWorld().setBlock( x, y + var11, z, BlockWood.class );
							
							/*
							if ( this.vinesGrow && var11 > 0 )
							{
								if ( r.nextInt( 3 ) > 0 && par1World.isAirBlock( x - 1, y + var11, z ) )
								{
									this.setBlockAndMetadata( par1World, x - 1, y + var11, z, Block.vine.blockID, 8 );
								}
								
								if ( r.nextInt( 3 ) > 0 && par1World.isAirBlock( x + 1, y + var11, z ) )
								{
									this.setBlockAndMetadata( par1World, x + 1, y + var11, z, Block.vine.blockID, 2 );
								}
								
								if ( r.nextInt( 3 ) > 0 && par1World.isAirBlock( x, y + var11, z - 1 ) )
								{
									this.setBlockAndMetadata( par1World, x, y + var11, z - 1, Block.vine.blockID, 1 );
								}
								
								if ( r.nextInt( 3 ) > 0 && par1World.isAirBlock( x, y + var11, z + 1 ) )
								{
									this.setBlockAndMetadata( par1World, x, y + var11, z + 1, Block.vine.blockID, 4 );
								}
							}
							*/
						}
					}
					
					/*
					if ( this.vinesGrow )
					{
						for ( var11 = y - 3 + thisHeight; var11 <= y + thisHeight; ++var11 )
						{
							var12 = var11 - ( y + thisHeight );
							var13 = 2 - var12 / 2;
							
							for ( var14 = x - var13; var14 <= x + var13; ++var14 )
							{
								for ( var15 = z - var13; var15 <= z + var13; ++var15 )
								{
									Block block = Block.blocksList[par1World.getBlockId( var14, var11, var15 )];
									if ( block != null && block.isLeaves( par1World, var14, var11, var15 ) )
									{
										if ( r.nextInt( 4 ) == 0 && par1World.getBlockId( var14 - 1, var11, var15 ) == 0 )
										{
											this.growVines( par1World, var14 - 1, var11, var15, 8 );
										}
										
										if ( r.nextInt( 4 ) == 0 && par1World.getBlockId( var14 + 1, var11, var15 ) == 0 )
										{
											this.growVines( par1World, var14 + 1, var11, var15, 2 );
										}
										
										if ( r.nextInt( 4 ) == 0 && par1World.getBlockId( var14, var11, var15 - 1 ) == 0 )
										{
											this.growVines( par1World, var14, var11, var15 - 1, 1 );
										}
										
										if ( r.nextInt( 4 ) == 0 && par1World.getBlockId( var14, var11, var15 + 1 ) == 0 )
										{
											this.growVines( par1World, var14, var11, var15 + 1, 4 );
										}
									}
								}
							}
						}
						
						if ( r.nextInt( 5 ) == 0 && thisHeight > 5 )
						{
							for ( var11 = 0; var11 < 2; ++var11 )
							{
								for ( var12 = 0; var12 < 4; ++var12 )
								{
									if ( r.nextInt( 4 - var11 ) == 0 )
									{
										var13 = r.nextInt( 3 );
										this.setBlockAndMetadata( par1World, x + Direction.offsetX[Direction.footInvisibleFaceRemap[var12]], y + thisHeight - 5 + var11, z + Direction.offsetZ[Direction.footInvisibleFaceRemap[var12]], Block.cocoaPlant.blockID, var13 << 2 | var12 );
									}
								}
							}
						}
					}
					*/
					
					return true;
				}
				else
				{
					return false;
				}
			}
		}
		else
		{
			return false;
		}
	}
}

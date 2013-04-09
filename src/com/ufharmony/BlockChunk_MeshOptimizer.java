package com.ufharmony;

import java.util.ArrayList;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;
import com.ufharmony.Block.Face;

public class BlockChunk_MeshOptimizer
{
	private static Vector3f[] vertices;
	private static Vector2f[] textureCoordinates;
	private static int[] indices;
	private static final BlockChunk_MeshMerger defaultBlockMerger = new BlockChunk_MeshMerger()
	{
		public boolean shouldFaceBeAdded( BlockChunkControl chunk, Vector3Int location, Block.Face face )
		{
			BlockType neighborBlock = chunk.getNeighborBlock( location, face );
			if ( neighborBlock != null )
			{
				if ( neighborBlock.getSkin().isTransparent() != neighborBlock.getSkin().isTransparent() )
				{
					return true;
				}
				return false;
			}
			return true;
		}
	};
	
	public static Mesh generateOptimizedMesh( BlockChunkControl blockChunk )
	{
		loadMeshData( blockChunk, defaultBlockMerger );
		return generateMesh();
	}
	
	private static BlockProperties getBlockPropertiesNull( BlockProperties[][][] bp, Vector3Int vec )
	{
		try
		{
			BlockProperties bp1 = bp[vec.getX()][vec.getY()][vec.getZ()];
			
			return bp1;
		}
		catch ( ArrayIndexOutOfBoundsException e )
		{
			return null;
		}
	}
	
	private static BlockProperties getBlockProperties( BlockProperties[][][] bp, Vector3Int vec )
	{
		try
		{
			BlockProperties bp1 = bp[vec.getX()][vec.getY()][vec.getZ()];
			
			if ( bp1 == null )
				return new BlockProperties();
			
			return bp1;
		}
		catch ( ArrayIndexOutOfBoundsException e )
		{
			return new BlockProperties();
		}
	}
	
	private static void setBlockProperties( BlockProperties[][][] bp, Vector3Int vec, BlockProperties bp1 )
	{
		try
		{
			bp[vec.getX()][vec.getY()][vec.getZ()] = bp1;
		}
		catch ( ArrayIndexOutOfBoundsException e )
		{}
	}
	
	private static Vector3Int getNeighbor( Vector3Int vec, Block.Face f )
	{
		if ( f == Block.Face.Front )
		{
			return vec.subtract( 0, 0, 1 );
		}
		else if ( f == Block.Face.Back )
		{
			return vec.add( 0, 0, 1 );
		}
		else if ( f == Block.Face.Left )
		{
			return vec.subtract( 1, 0, 0 );
		}
		else if ( f == Block.Face.Right )
		{
			return vec.add( 1, 0, 0 );
		}
		else if ( f == Block.Face.Top )
		{
			return vec.add( 0, 1, 0 );
		}
		else if ( f == Block.Face.Bottom )
		{
			return vec.subtract( 0, 1, 0 );
		}
		
		return vec;
	}
	
	public static BlockProperties[][][] blockProperties;
	
	private static void loadMeshDataNEW( BlockChunkControl chunk, BlockChunk_MeshMerger meshMerger )
	{
		ArrayList verticeList = new ArrayList();
		ArrayList textureCoordinateList = new ArrayList();
		ArrayList indicesList = new ArrayList();
		BlockTerrainControl blockTerrain = chunk.getTerrain();
		Vector3Int tmpLocation = new Vector3Int();
		BlockProperties tmpBP;
		BlockProperties s1;
		BlockProperties s2;
		
		blockProperties = new BlockProperties[blockTerrain.getSettings().getChunkSizeX()][blockTerrain.getSettings().getChunkSizeY()][blockTerrain.getSettings().getChunkSizeZ()];
		
		for ( int x1 = 0; x1 < blockTerrain.getSettings().getChunkSizeX(); x1++ )
		{
			for ( int y1 = 0; y1 < blockTerrain.getSettings().getChunkSizeY(); y1++ )
			{
				for ( int z1 = 0; z1 < blockTerrain.getSettings().getChunkSizeZ(); z1++ )
				{
					tmpLocation.set( x1, y1, z1 );
					BlockType block = chunk.getBlock( tmpLocation );
					if ( block != null )
					{
						Vector3f blockLocation = new Vector3f( x1, y1, z1 );
						
						Vector3f faceLoc_Bottom_TopLeft = blockLocation.add( new Vector3f( 0.0F, 0.0F, 0.0F ) ).mult( blockTerrain.getSettings().getBlockSize() );
						Vector3f faceLoc_Bottom_TopRight = blockLocation.add( new Vector3f( 1.0F, 0.0F, 0.0F ) ).mult( blockTerrain.getSettings().getBlockSize() );
						Vector3f faceLoc_Bottom_BottomLeft = blockLocation.add( new Vector3f( 0.0F, 0.0F, 1.0F ) ).mult( blockTerrain.getSettings().getBlockSize() );
						Vector3f faceLoc_Bottom_BottomRight = blockLocation.add( new Vector3f( 1.0F, 0.0F, 1.0F ) ).mult( blockTerrain.getSettings().getBlockSize() );
						Vector3f faceLoc_Top_TopLeft = blockLocation.add( new Vector3f( 0.0F, 0.5F, 0.0F ) ).mult( blockTerrain.getSettings().getBlockSize() );
						Vector3f faceLoc_Top_TopRight = blockLocation.add( new Vector3f( 1.0F, 0.5F, 0.0F ) ).mult( blockTerrain.getSettings().getBlockSize() );
						Vector3f faceLoc_Top_BottomLeft = blockLocation.add( new Vector3f( 0.0F, 0.5F, 1.0F ) ).mult( blockTerrain.getSettings().getBlockSize() );
						Vector3f faceLoc_Top_BottomRight = blockLocation.add( new Vector3f( 1.0F, 0.5F, 1.0F ) ).mult( blockTerrain.getSettings().getBlockSize() );
						
						tmpBP = new BlockProperties();
						
						tmpBP.corner_bottom_1 = faceLoc_Bottom_TopLeft;
						tmpBP.corner_bottom_2 = faceLoc_Bottom_TopRight;
						tmpBP.corner_bottom_3 = faceLoc_Bottom_BottomRight;
						tmpBP.corner_bottom_4 = faceLoc_Bottom_BottomLeft;
						
						tmpBP.corner_top_1 = faceLoc_Top_TopLeft;
						tmpBP.corner_top_2 = faceLoc_Top_TopRight;
						tmpBP.corner_top_3 = faceLoc_Top_BottomRight;
						tmpBP.corner_top_4 = faceLoc_Top_BottomLeft;
						
						tmpBP.x = x1;
						tmpBP.y = y1;
						tmpBP.z = z1;
						
						tmpBP.blockSkin = block.getSkin();
						
						blockProperties[x1][y1][z1] = tmpBP;
					}
				}
			}
		}
		
		for ( int x1 = 0; x1 < blockTerrain.getSettings().getChunkSizeX(); x1 = x1 + 2 )
		{
			for ( int y1 = 0; y1 < blockTerrain.getSettings().getChunkSizeY(); y1++ )
			{
				for ( int z1 = 0; z1 < blockTerrain.getSettings().getChunkSizeZ(); z1 = z1 + 2 )
				{
					tmpLocation.set( x1, y1, z1 );
					BlockType block = chunk.getBlock( tmpLocation );
					if ( block != null )
					{
						if ( meshMerger.shouldFaceBeAdded( chunk, tmpLocation, Block.Face.Top ) )
						{
							blockProperties[x1][y1][z1].blockSkin = new BlockSkin( new BlockSkin_TextureLocation( 0, 3 ), false );
							BlockProperties bp = getBlockProperties( blockProperties, tmpLocation );
							
							/*
							Vector3Int x = getNeighbor( tmpLocation, Block.Face.Left );
							setBlockProperties( blockProperties, x, getBlockProperties( blockProperties, x ).corner_top_2( bp.corner_top_1 ) );
							setBlockProperties( blockProperties, x, getBlockProperties( blockProperties, x ).corner_top_3( bp.corner_top_4 ) );
							
							x = getNeighbor( tmpLocation, Block.Face.Right );
							setBlockProperties( blockProperties, x, getBlockProperties( blockProperties, x ).corner_top_1( bp.corner_top_2 ) );
							setBlockProperties( blockProperties, x, getBlockProperties( blockProperties, x ).corner_top_4( bp.corner_top_3 ) );
							*/
							
							Vector3Int x = getNeighbor( tmpLocation, Block.Face.Front );
							BlockProperties tbp = getBlockPropertiesNull( blockProperties, x );
							
							if ( tbp == null )
							{
								x = getNeighbor( x, Block.Face.Bottom );
								tbp = getBlockPropertiesNull( blockProperties, x );
								
								if ( tbp != null )
								{
									tbp.corner_top_4( bp.corner_top_1 );
									tbp.corner_top_3( bp.corner_top_2 );
									setBlockProperties( blockProperties, x, tbp );
								}
							}
							else
							{
								if ( meshMerger.shouldFaceBeAdded( chunk, x, Block.Face.Top ) )
								{
									tbp.corner_top_4( bp.corner_top_1 );
									tbp.corner_top_3( bp.corner_top_2 );
									setBlockProperties( blockProperties, x, tbp );
								}
								else
								{
									x = getNeighbor( x, Block.Face.Top );
									tbp = getBlockPropertiesNull( blockProperties, x );
									
									if ( tbp != null && meshMerger.shouldFaceBeAdded( chunk, x, Block.Face.Top ) )
									{
										tbp.corner_top_4( bp.corner_top_1 );
										tbp.corner_top_3( bp.corner_top_2 );
										setBlockProperties( blockProperties, x, tbp );
									}
								}
							}
							
							/*
							x = getNeighbor( tmpLocation, Block.Face.Back );
							setBlockProperties( blockProperties, x, getBlockProperties( blockProperties, x ).corner_top_1( bp.corner_top_4 ) );
							setBlockProperties( blockProperties, x, getBlockProperties( blockProperties, x ).corner_top_2( bp.corner_top_3 ) );
							*/
						}
					}
				}
			}
		}
		
		for ( int x1 = 0; x1 < blockTerrain.getSettings().getChunkSizeX(); x1++ )
		{
			for ( int y1 = 0; y1 < blockTerrain.getSettings().getChunkSizeY(); y1++ )
			{
				for ( int z1 = 0; z1 < blockTerrain.getSettings().getChunkSizeZ(); z1++ )
				{
					tmpLocation.set( x1, y1, z1 );
					BlockType block = chunk.getBlock( tmpLocation );
					if ( block != null )
					{
						Vector3f blockLocation = new Vector3f( x1, y1, z1 );
						
						BlockProperties bp = blockProperties[x1][y1][z1];
						
						BlockSkin blockSkin = bp.blockSkin;
						
						Vector3f faceLoc_Bottom_TopLeft = bp.corner_bottom_1;
						Vector3f faceLoc_Bottom_TopRight = bp.corner_bottom_2;
						Vector3f faceLoc_Bottom_BottomLeft = bp.corner_bottom_4;
						Vector3f faceLoc_Bottom_BottomRight = bp.corner_bottom_3;
						
						Vector3f faceLoc_Top_TopLeft = bp.corner_top_1;
						Vector3f faceLoc_Top_TopRight = bp.corner_top_2;
						Vector3f faceLoc_Top_BottomLeft = bp.corner_top_4;
						Vector3f faceLoc_Top_BottomRight = bp.corner_top_3;
						
						/*
						 * if ( meshMerger.shouldFaceBeAdded( chunk, tmpLocation, Block.Face.Top ) ) { s1 =
						 * getBlockProperties( blockProperties, getNeighbor( tmpLocation, Block.Face.Left ) );
						 * 
						 * // If null them there so no block on the right. if ( s1 == null ) { faceLoc_Top_TopLeft =
						 * faceLoc_Top_TopLeft.subtract( new Vector3f( 0, blockTerrain.getSettings().getBlockSize() / 2f, 0 )
						 * ); faceLoc_Top_BottomLeft = faceLoc_Top_BottomLeft.subtract( new Vector3f( 0,
						 * blockTerrain.getSettings().getBlockSize() / 2f, 0 ) ); } else { s2 = getBlockProperties(
						 * blockProperties, getNeighbor( getNeighbor( tmpLocation, Block.Face.Left ), Block.Face.Top ) );
						 * 
						 * if ( s2 != null ) { faceLoc_Top_TopLeft = faceLoc_Top_TopLeft.add( new Vector3f( 0,
						 * blockTerrain.getSettings().getBlockSize() / 2f, 0 ) ); faceLoc_Top_BottomLeft =
						 * faceLoc_Top_BottomLeft.add( new Vector3f( 0, blockTerrain.getSettings().getBlockSize() / 2f, 0 ) );
						 * } }
						 * 
						 * s1 = getBlockProperties( blockProperties, getNeighbor( tmpLocation, Block.Face.Right ) );
						 * 
						 * if ( s1 == null ) { faceLoc_Top_TopRight = faceLoc_Top_TopRight.subtract( new Vector3f( 0,
						 * blockTerrain.getSettings().getBlockSize() / 2f, 0 ) ); faceLoc_Top_BottomRight =
						 * faceLoc_Top_BottomRight.subtract( new Vector3f( 0, blockTerrain.getSettings().getBlockSize() / 2f,
						 * 0 ) ); } else { s2 = getBlockProperties( blockProperties, getNeighbor( getNeighbor( tmpLocation,
						 * Block.Face.Right ), Block.Face.Top ) );
						 * 
						 * if ( s2 != null ) { faceLoc_Top_TopRight = faceLoc_Top_TopRight.add( new Vector3f( 0,
						 * blockTerrain.getSettings().getBlockSize() / 2f, 0 ) ); faceLoc_Top_BottomRight =
						 * faceLoc_Top_BottomRight.add( new Vector3f( 0, blockTerrain.getSettings().getBlockSize() / 2f, 0 )
						 * ); } }
						 * 
						 * s1 = getBlockProperties( blockProperties, getNeighbor( tmpLocation, Block.Face.Front ) );
						 * 
						 * if ( s1 == null ) { faceLoc_Top_TopRight = faceLoc_Top_TopRight.subtract( new Vector3f( 0,
						 * blockTerrain.getSettings().getBlockSize() / 2f, 0 ) ); faceLoc_Top_TopLeft =
						 * faceLoc_Top_TopLeft.subtract( new Vector3f( 0, blockTerrain.getSettings().getBlockSize() / 2f, 0 )
						 * ); } else { s2 = getBlockProperties( blockProperties, getNeighbor( getNeighbor( tmpLocation,
						 * Block.Face.Front ), Block.Face.Top ) );
						 * 
						 * if ( s2 != null ) { faceLoc_Top_TopRight = faceLoc_Top_TopRight.add( new Vector3f( 0,
						 * blockTerrain.getSettings().getBlockSize() / 2f, 0 ) ); faceLoc_Top_TopLeft =
						 * faceLoc_Top_TopLeft.add( new Vector3f( 0, blockTerrain.getSettings().getBlockSize() / 2f, 0 ) ); }
						 * }
						 * 
						 * s1 = getBlockProperties( blockProperties, getNeighbor( tmpLocation, Block.Face.Back ) );
						 * 
						 * if ( s1 == null ) { faceLoc_Top_BottomRight = faceLoc_Top_BottomRight.subtract( new Vector3f( 0,
						 * blockTerrain.getSettings().getBlockSize() / 2f, 0 ) ); faceLoc_Top_BottomLeft =
						 * faceLoc_Top_BottomLeft.subtract( new Vector3f( 0, blockTerrain.getSettings().getBlockSize() / 2f, 0
						 * ) ); } else { s2 = getBlockProperties( blockProperties, getNeighbor( getNeighbor( tmpLocation,
						 * Block.Face.Back ), Block.Face.Top ) );
						 * 
						 * if ( s2 != null ) { faceLoc_Top_BottomRight = faceLoc_Top_BottomRight.add( new Vector3f( 0,
						 * blockTerrain.getSettings().getBlockSize() / 2f, 0 ) ); faceLoc_Top_BottomLeft =
						 * faceLoc_Top_BottomLeft.add( new Vector3f( 0, blockTerrain.getSettings().getBlockSize() / 2f, 0 ) );
						 * } } }
						 */
						
						if ( meshMerger.shouldFaceBeAdded( chunk, tmpLocation, Block.Face.Top ) )
						{
							addVerticeIndexes( verticeList, indicesList );
							verticeList.add( faceLoc_Top_BottomLeft );
							verticeList.add( faceLoc_Top_BottomRight );
							verticeList.add( faceLoc_Top_TopLeft );
							verticeList.add( faceLoc_Top_TopRight );
							addBlockTextureCoordinates( textureCoordinateList, blockSkin.getTextureLocation( chunk, tmpLocation, Block.Face.Top ) );
						}
						
						if ( meshMerger.shouldFaceBeAdded( chunk, tmpLocation, Block.Face.Bottom ) )
						{
							addVerticeIndexes( verticeList, indicesList );
							verticeList.add( faceLoc_Bottom_BottomRight );
							verticeList.add( faceLoc_Bottom_BottomLeft );
							verticeList.add( faceLoc_Bottom_TopRight );
							verticeList.add( faceLoc_Bottom_TopLeft );
							addBlockTextureCoordinates( textureCoordinateList, blockSkin.getTextureLocation( chunk, tmpLocation, Block.Face.Bottom ) );
						}
						
						if ( meshMerger.shouldFaceBeAdded( chunk, tmpLocation, Block.Face.Left ) )
						{
							addVerticeIndexes( verticeList, indicesList );
							verticeList.add( faceLoc_Bottom_TopLeft );
							verticeList.add( faceLoc_Bottom_BottomLeft );
							verticeList.add( faceLoc_Top_TopLeft );
							verticeList.add( faceLoc_Top_BottomLeft );
							addBlockTextureCoordinates( textureCoordinateList, blockSkin.getTextureLocation( chunk, tmpLocation, Block.Face.Left ) );
						}
						
						if ( meshMerger.shouldFaceBeAdded( chunk, tmpLocation, Block.Face.Right ) )
						{
							addVerticeIndexes( verticeList, indicesList );
							verticeList.add( faceLoc_Bottom_BottomRight );
							verticeList.add( faceLoc_Bottom_TopRight );
							verticeList.add( faceLoc_Top_BottomRight );
							verticeList.add( faceLoc_Top_TopRight );
							addBlockTextureCoordinates( textureCoordinateList, blockSkin.getTextureLocation( chunk, tmpLocation, Block.Face.Right ) );
						}
						
						if ( meshMerger.shouldFaceBeAdded( chunk, tmpLocation, Block.Face.Front ) )
						{
							addVerticeIndexes( verticeList, indicesList );
							verticeList.add( faceLoc_Bottom_BottomLeft );
							verticeList.add( faceLoc_Bottom_BottomRight );
							verticeList.add( faceLoc_Top_BottomLeft );
							verticeList.add( faceLoc_Top_BottomRight );
							addBlockTextureCoordinates( textureCoordinateList, blockSkin.getTextureLocation( chunk, tmpLocation, Block.Face.Front ) );
						}
						
						if ( meshMerger.shouldFaceBeAdded( chunk, tmpLocation, Block.Face.Back ) )
						{
							addVerticeIndexes( verticeList, indicesList );
							verticeList.add( faceLoc_Bottom_TopRight );
							verticeList.add( faceLoc_Bottom_TopLeft );
							verticeList.add( faceLoc_Top_TopRight );
							verticeList.add( faceLoc_Top_TopLeft );
							addBlockTextureCoordinates( textureCoordinateList, blockSkin.getTextureLocation( chunk, tmpLocation, Block.Face.Back ) );
						}
					}
				}
			}
		}
		
		vertices = new Vector3f[verticeList.size()];
		for ( int i = 0; i < verticeList.size(); i++ )
		{
			vertices[i] = ( (Vector3f) verticeList.get( i ) );
		}
		textureCoordinates = new Vector2f[textureCoordinateList.size()];
		for ( int i = 0; i < textureCoordinateList.size(); i++ )
		{
			textureCoordinates[i] = ( (Vector2f) textureCoordinateList.get( i ) );
		}
		indices = new int[indicesList.size()];
		for ( int i = 0; i < indicesList.size(); i++ )
			indices[i] = ( (Integer) indicesList.get( i ) ).intValue();
	}
	
	private static void loadMeshData( BlockChunkControl chunk, BlockChunk_MeshMerger meshMerger )
	{
		ArrayList verticeList = new ArrayList();
		ArrayList textureCoordinateList = new ArrayList();
		ArrayList indicesList = new ArrayList();
		BlockTerrainControl blockTerrain = chunk.getTerrain();
		Vector3Int tmpLocation = new Vector3Int();
		
		blockProperties = new BlockProperties[blockTerrain.getSettings().getChunkSizeX()][blockTerrain.getSettings().getChunkSizeY()][blockTerrain.getSettings().getChunkSizeZ()];
		
		for ( int x1 = 0; x1 < blockTerrain.getSettings().getChunkSizeX(); x1++ )
		{
			for ( int y1 = 0; y1 < blockTerrain.getSettings().getChunkSizeY(); y1++ )
			{
				for ( int z1 = 0; z1 < blockTerrain.getSettings().getChunkSizeZ(); z1++ )
				{
					tmpLocation.set( x1, y1, z1 );
					BlockType block = chunk.getBlock( tmpLocation );
					if ( block != null )
					{
						if ( meshMerger.shouldFaceBeAdded( chunk, tmpLocation, Block.Face.Top ) )
						{
							blockProperties[x1][y1][z1] = new BlockProperties();
							
							if ( meshMerger.shouldFaceBeAdded( chunk, tmpLocation, Block.Face.Left ) )
								blockProperties[x1][y1][z1].side_top_left = true;
							
							if ( meshMerger.shouldFaceBeAdded( chunk, tmpLocation, Block.Face.Right ) )
								blockProperties[x1][y1][z1].side_top_right = true;
							
							if ( meshMerger.shouldFaceBeAdded( chunk, tmpLocation, Block.Face.Front ) )
								blockProperties[x1][y1][z1].side_top_front = true;
							
							if ( meshMerger.shouldFaceBeAdded( chunk, tmpLocation, Block.Face.Back ) )
								blockProperties[x1][y1][z1].side_top_back = true;
						}
					}
				}
			}
		}
		
		for ( int x2 = 0; x2 < blockTerrain.getSettings().getChunkSizeX(); x2++ )
		{
			for ( int y2 = 0; y2 < blockTerrain.getSettings().getChunkSizeY(); y2++ )
			{
				for ( int z2 = 0; z2 < blockTerrain.getSettings().getChunkSizeZ(); z2++ )
				{
					tmpLocation.set( x2, y2, z2 );
					BlockType block = chunk.getBlock( tmpLocation );
					if ( block != null )
					{
						BlockSkin blockSkin = block.getSkin();
						Vector3f blockLocation = new Vector3f( x2, y2, z2 );
						
						float mb = blockTerrain.getSettings().getBlockSize();
						float blockDevision = 2;
						
						Vector3f faceLoc_Bottom_TopLeft = blockLocation.add( new Vector3f( 0.0F, 0.0F, 0.0F ) ).mult( new Vector3f( mb, mb / blockDevision, mb ) );
						Vector3f faceLoc_Bottom_TopRight = blockLocation.add( new Vector3f( 1.0F, 0.0F, 0.0F ) ).mult( new Vector3f( mb, mb / blockDevision, mb ) );
						Vector3f faceLoc_Bottom_BottomLeft = blockLocation.add( new Vector3f( 0.0F, 0.0F, 1.0F ) ).mult( new Vector3f( mb, mb / blockDevision, mb ) );
						Vector3f faceLoc_Bottom_BottomRight = blockLocation.add( new Vector3f( 1.0F, 0.0F, 1.0F ) ).mult( new Vector3f( mb, mb / blockDevision, mb ) );
						Vector3f faceLoc_Top_TopLeft = blockLocation.add( new Vector3f( 0.0F, 1.0F, 0.0F ) ).mult( new Vector3f( mb, mb / blockDevision, mb ) );
						Vector3f faceLoc_Top_TopRight = blockLocation.add( new Vector3f( 1.0F, 1.0F, 0.0F ) ).mult( new Vector3f( mb, mb / blockDevision, mb ) );
						Vector3f faceLoc_Top_BottomLeft = blockLocation.add( new Vector3f( 0.0F, 1.0F, 1.0F ) ).mult( new Vector3f( mb, mb / blockDevision, mb ) );
						Vector3f faceLoc_Top_BottomRight = blockLocation.add( new Vector3f( 1.0F, 1.0F, 1.0F ) ).mult( new Vector3f( mb, mb / blockDevision, mb ) );
						
						BlockProperties bp = getBlockProperties( blockProperties, tmpLocation );
						BlockProperties s1;
						BlockProperties s2;
						
						if ( meshMerger.shouldFaceBeAdded( chunk, tmpLocation, Block.Face.Top ) )
						{
							if ( bp.side_top_left )
							{
								s1 = getBlockProperties( blockProperties, getNeighbor( tmpLocation, Block.Face.Front ) );
								s2 = getBlockProperties( blockProperties, getNeighbor( tmpLocation, Block.Face.Back ) );
								
								if ( s1.side_top_left && s2.side_top_left )
								{
									faceLoc_Top_TopLeft = faceLoc_Bottom_TopLeft;
									faceLoc_Top_BottomLeft = faceLoc_Bottom_BottomLeft;
								}
								else if ( s1.side_top_left && !s2.side_top_left )
								{
									faceLoc_Top_TopLeft = faceLoc_Bottom_TopLeft;
								}
								else if ( !s1.side_top_left && s2.side_top_left )
								{
									faceLoc_Top_BottomLeft = faceLoc_Bottom_BottomLeft;
								}
								else
								{
									// No change
								}
							}
							
							if ( bp.side_top_right )
							{
								s1 = getBlockProperties( blockProperties, getNeighbor( tmpLocation, Block.Face.Front ) );
								s2 = getBlockProperties( blockProperties, getNeighbor( tmpLocation, Block.Face.Back ) );
								
								if ( s1.side_top_right && s2.side_top_right )
								{
									faceLoc_Top_TopRight = faceLoc_Bottom_TopRight;
									faceLoc_Top_BottomRight = faceLoc_Bottom_BottomRight;
								}
								else if ( s1.side_top_right && !s2.side_top_right )
								{
									faceLoc_Top_TopRight = faceLoc_Bottom_TopRight;
								}
								else if ( !s1.side_top_right && s2.side_top_right )
								{
									faceLoc_Top_BottomRight = faceLoc_Bottom_BottomRight;
								}
								else
								{
									// No change
								}
							}
							
							if ( bp.side_top_front )
							{
								// blockSkin = new BlockSkin( new BlockSkin_TextureLocation( 0, 3 ), false );
								
								s1 = getBlockProperties( blockProperties, getNeighbor( tmpLocation, Block.Face.Left ) );
								s2 = getBlockProperties( blockProperties, getNeighbor( tmpLocation, Block.Face.Right ) );
								
								if ( s1.side_top_front && s2.side_top_front )
								{
									faceLoc_Top_BottomLeft = faceLoc_Bottom_BottomLeft;
									faceLoc_Top_BottomRight = faceLoc_Bottom_BottomRight;
								}
								else if ( s1.side_top_front && !s2.side_top_front )
								{
									faceLoc_Top_BottomLeft = faceLoc_Bottom_BottomLeft;
								}
								else if ( !s1.side_top_front && s2.side_top_front )
								{
									faceLoc_Top_BottomRight = faceLoc_Bottom_BottomRight;
								}
								else
								{
									// No change
								}
							}
							
							if ( bp.side_top_back )
							{
								s1 = getBlockProperties( blockProperties, getNeighbor( tmpLocation, Block.Face.Left ) );
								s2 = getBlockProperties( blockProperties, getNeighbor( tmpLocation, Block.Face.Right ) );
								
								if ( s1.side_top_back && s2.side_top_back )
								{
									faceLoc_Top_TopLeft = faceLoc_Bottom_TopLeft;
									faceLoc_Top_TopRight = faceLoc_Bottom_TopRight;
								}
								else if ( s1.side_top_back && !s2.side_top_back )
								{
									faceLoc_Top_TopLeft = faceLoc_Bottom_TopLeft;
								}
								else if ( !s1.side_top_back && s2.side_top_back )
								{
									faceLoc_Top_TopRight = faceLoc_Bottom_TopRight;
								}
								else
								{
									// No change
								}
							}
							
							Vector3Int vec = getNeighbor( tmpLocation, Block.Face.Top );
							
							s1 = getBlockProperties( blockProperties, getNeighbor( vec, Block.Face.Left ) );
							s2 = getBlockProperties( blockProperties, getNeighbor( vec, Block.Face.Front ) );
							BlockProperties s3 = getBlockProperties( blockProperties, getNeighbor( vec, Block.Face.Right ) );
							BlockProperties s4 = getBlockProperties( blockProperties, getNeighbor( vec, Block.Face.Back ) );
							BlockProperties stmp;
							
							if ( !s1.dummyInst && !s2.dummyInst && !s3.dummyInst && !s4.dummyInst )
							{
								// Do Nothing
							}
							else if ( !s1.dummyInst && !s2.dummyInst )
							{
								stmp = getBlockProperties( blockProperties, getNeighbor( getNeighbor( vec, Block.Face.Left ), Block.Face.Right ) );
								
								faceLoc_Top_TopLeft = faceLoc_Top_TopLeft.add( new Vector3f( 0, blockTerrain.getSettings().getBlockSize(), 0 ) );
							}
							else if ( !s2.dummyInst && !s3.dummyInst )
							{
								faceLoc_Top_TopRight = faceLoc_Top_TopRight.add( new Vector3f( 0, blockTerrain.getSettings().getBlockSize(), 0 ) );
							}
							else if ( !s3.dummyInst && !s4.dummyInst )
							{
								faceLoc_Top_BottomRight = faceLoc_Top_BottomRight.add( new Vector3f( 0, blockTerrain.getSettings().getBlockSize(), 0 ) );
							}
							else if ( !s4.dummyInst && !s1.dummyInst )
							{
								faceLoc_Top_BottomLeft = faceLoc_Top_BottomLeft.add( new Vector3f( 0, blockTerrain.getSettings().getBlockSize(), 0 ) );
							}
						}
						
						if ( meshMerger.shouldFaceBeAdded( chunk, tmpLocation, Block.Face.Top ) )
						{
							addVerticeIndexes( verticeList, indicesList );
							verticeList.add( faceLoc_Top_BottomLeft );
							verticeList.add( faceLoc_Top_BottomRight );
							verticeList.add( faceLoc_Top_TopLeft );
							verticeList.add( faceLoc_Top_TopRight );
							addBlockTextureCoordinates( textureCoordinateList, blockSkin.getTextureLocation( chunk, tmpLocation, Block.Face.Top ) );
						}
						
						if ( meshMerger.shouldFaceBeAdded( chunk, tmpLocation, Block.Face.Bottom ) )
						{
							addVerticeIndexes( verticeList, indicesList );
							verticeList.add( faceLoc_Bottom_BottomRight );
							verticeList.add( faceLoc_Bottom_BottomLeft );
							verticeList.add( faceLoc_Bottom_TopRight );
							verticeList.add( faceLoc_Bottom_TopLeft );
							addBlockTextureCoordinates( textureCoordinateList, blockSkin.getTextureLocation( chunk, tmpLocation, Block.Face.Bottom ) );
						}
						
						boolean side1 = false;
						boolean side2 = false;
						boolean side3 = false;
						boolean side4 = false;
						
						if ( meshMerger.shouldFaceBeAdded( chunk, tmpLocation, Block.Face.Left ) )
						{
							side4 = true;
							side1 = true;
							side2 = true;
						}
						
						if ( meshMerger.shouldFaceBeAdded( chunk, tmpLocation, Block.Face.Right ) )
						{
							side2 = true;
							side3 = true;
							side4 = true;
						}
						
						if ( meshMerger.shouldFaceBeAdded( chunk, tmpLocation, Block.Face.Front ) )
						{
							side1 = true;
							side2 = true;
							side3 = true;
						}
						
						if ( meshMerger.shouldFaceBeAdded( chunk, tmpLocation, Block.Face.Back ) )
						{
							side3 = true;
							side4 = true;
							side1 = true;
						}
						
						if ( side1 )
						{
							addVerticeIndexes( verticeList, indicesList );
							verticeList.add( faceLoc_Bottom_TopLeft );
							verticeList.add( faceLoc_Bottom_BottomLeft );
							verticeList.add( faceLoc_Top_TopLeft );
							verticeList.add( faceLoc_Top_BottomLeft );
							addBlockTextureCoordinates( textureCoordinateList, blockSkin.getTextureLocation( chunk, tmpLocation, Block.Face.Left ) );
						}
						
						if ( side3 )
						{
							addVerticeIndexes( verticeList, indicesList );
							verticeList.add( faceLoc_Bottom_BottomRight );
							verticeList.add( faceLoc_Bottom_TopRight );
							verticeList.add( faceLoc_Top_BottomRight );
							verticeList.add( faceLoc_Top_TopRight );
							addBlockTextureCoordinates( textureCoordinateList, blockSkin.getTextureLocation( chunk, tmpLocation, Block.Face.Right ) );
						}
						
						if ( side2 )
						{
							addVerticeIndexes( verticeList, indicesList );
							verticeList.add( faceLoc_Bottom_BottomLeft );
							verticeList.add( faceLoc_Bottom_BottomRight );
							verticeList.add( faceLoc_Top_BottomLeft );
							verticeList.add( faceLoc_Top_BottomRight );
							addBlockTextureCoordinates( textureCoordinateList, blockSkin.getTextureLocation( chunk, tmpLocation, Block.Face.Front ) );
						}
						
						if ( side4 )
						{
							addVerticeIndexes( verticeList, indicesList );
							verticeList.add( faceLoc_Bottom_TopRight );
							verticeList.add( faceLoc_Bottom_TopLeft );
							verticeList.add( faceLoc_Top_TopRight );
							verticeList.add( faceLoc_Top_TopLeft );
							addBlockTextureCoordinates( textureCoordinateList, blockSkin.getTextureLocation( chunk, tmpLocation, Block.Face.Back ) );
						}
					}
				}
			}
		}
		
		vertices = new Vector3f[verticeList.size()];
		for ( int i = 0; i < verticeList.size(); i++ )
		{
			vertices[i] = ( (Vector3f) verticeList.get( i ) );
		}
		textureCoordinates = new Vector2f[textureCoordinateList.size()];
		for ( int i = 0; i < textureCoordinateList.size(); i++ )
		{
			textureCoordinates[i] = ( (Vector2f) textureCoordinateList.get( i ) );
		}
		indices = new int[indicesList.size()];
		for ( int i = 0; i < indicesList.size(); i++ )
			indices[i] = ( (Integer) indicesList.get( i ) ).intValue();
	}
	
	private static void addBlockTextureCoordinates( ArrayList<Vector2f> textureCoordinatesList, BlockSkin_TextureLocation textureLocation )
	{
		textureCoordinatesList.add( getTextureCoordinates( textureLocation, 0, 0 ) );
		textureCoordinatesList.add( getTextureCoordinates( textureLocation, 1, 0 ) );
		textureCoordinatesList.add( getTextureCoordinates( textureLocation, 0, 1 ) );
		textureCoordinatesList.add( getTextureCoordinates( textureLocation, 1, 1 ) );
	}
	
	private static Vector2f getTextureCoordinates( BlockSkin_TextureLocation textureLocation, int xUnitsToAdd, int yUnitsToAdd )
	{
		float textureCount = 16.0F;
		float textureUnit = 1.0F / textureCount;
		float x = ( textureLocation.getColumn() + xUnitsToAdd ) * textureUnit;
		float y = ( -1 * textureLocation.getRow() + ( yUnitsToAdd - 1 ) ) * textureUnit + 1.0F;
		return new Vector2f( x, y );
	}
	
	private static void addVerticeIndexes( ArrayList<Vector3f> verticeList, ArrayList<Integer> indexesList )
	{
		int verticesCount = verticeList.size();
		indexesList.add( Integer.valueOf( verticesCount + 2 ) );
		indexesList.add( Integer.valueOf( verticesCount + 0 ) );
		indexesList.add( Integer.valueOf( verticesCount + 1 ) );
		indexesList.add( Integer.valueOf( verticesCount + 1 ) );
		indexesList.add( Integer.valueOf( verticesCount + 3 ) );
		indexesList.add( Integer.valueOf( verticesCount + 2 ) );
	}
	
	private static Mesh generateMesh()
	{
		Mesh mesh = new Mesh();
		mesh.setBuffer( VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer( vertices ) );
		mesh.setBuffer( VertexBuffer.Type.TexCoord, 2, BufferUtils.createFloatBuffer( textureCoordinates ) );
		mesh.setBuffer( VertexBuffer.Type.Index, 1, BufferUtils.createIntBuffer( indices ) );
		mesh.updateBound();
		return mesh;
	}
}

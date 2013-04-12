package com.ufharmony.grid;

import java.util.ArrayList;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;
import com.ufharmony.blocks.BlockBase;
import com.ufharmony.grid.Square.Face;
import com.ufharmony.utils.Vector3Int;

public class Chunk_MeshOptimizer
{
	private static Vector3f[] vertices;
	private static Vector2f[] textureCoordinates;
	private static int[] indices;
	private static final Chunk_MeshMerger defaultSquareMerger = new Chunk_MeshMerger()
	{
		public boolean shouldFaceBeAdded( ChunkControl chunk, Vector3Int location, Square.Face face )
		{
			UniqueSquare neighborSquare = chunk.getNeighborSquare( location, face );
			if ( neighborSquare != null )
			{
				if ( neighborSquare.getSkin().isTransparent() != neighborSquare.getSkin().isTransparent() )
				{
					return true;
				}
				return false;
			}
			return true;
		}
	};
	
	public static Mesh generateOptimizedMesh( ChunkControl squareChunk )
	{
		loadMeshData( squareChunk, defaultSquareMerger );
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
	
	private static Vector3Int getNeighbor( Vector3Int vec, Square.Face f )
	{
		if ( f == Square.Face.North )
		{
			return vec.subtract( 0, 0, 1 );
		}
		else if ( f == Square.Face.South )
		{
			return vec.add( 0, 0, 1 );
		}
		else if ( f == Square.Face.West )
		{
			return vec.subtract( 1, 0, 0 );
		}
		else if ( f == Square.Face.East )
		{
			return vec.add( 1, 0, 0 );
		}
		else if ( f == Square.Face.Top )
		{
			return vec.add( 0, 1, 0 );
		}
		else if ( f == Square.Face.Bottom )
		{
			return vec.subtract( 0, 1, 0 );
		}
		
		return vec;
	}
	
	public static BlockProperties[][][] squareProperties;
	
	private static void loadMeshDataNEW( ChunkControl chunk, Chunk_MeshMerger meshMerger )
	{
		ArrayList verticeList = new ArrayList();
		ArrayList textureCoordinateList = new ArrayList();
		ArrayList indicesList = new ArrayList();
		TerrainControl squareTerrain = chunk.getTerrain();
		Vector3Int tmpLocation = new Vector3Int();
		BlockProperties tmpBP;
		BlockProperties s1;
		BlockProperties s2;
		
		squareProperties = new BlockProperties[squareTerrain.getSettings().getChunkSizeX()][squareTerrain.getSettings().getChunkSizeY()][squareTerrain.getSettings().getChunkSizeZ()];
		
		for ( int x1 = 0; x1 < squareTerrain.getSettings().getChunkSizeX(); x1++ )
		{
			for ( int y1 = 0; y1 < squareTerrain.getSettings().getChunkSizeY(); y1++ )
			{
				for ( int z1 = 0; z1 < squareTerrain.getSettings().getChunkSizeZ(); z1++ )
				{
					tmpLocation.set( x1, y1, z1 );
					UniqueSquare square = chunk.getSquare( tmpLocation );
					if ( square != null && square.getParentClass().equals( BlockBase.class ) )
					{
						Vector3f squareLocation = new Vector3f( x1, y1, z1 );
						
						Vector3f faceLoc_Bottom_TopLeft = squareLocation.add( new Vector3f( 0.0F, 0.0F, 0.0F ) ).mult( squareTerrain.getSettings().getSquareSize() );
						Vector3f faceLoc_Bottom_TopRight = squareLocation.add( new Vector3f( 1.0F, 0.0F, 0.0F ) ).mult( squareTerrain.getSettings().getSquareSize() );
						Vector3f faceLoc_Bottom_BottomLeft = squareLocation.add( new Vector3f( 0.0F, 0.0F, 1.0F ) ).mult( squareTerrain.getSettings().getSquareSize() );
						Vector3f faceLoc_Bottom_BottomRight = squareLocation.add( new Vector3f( 1.0F, 0.0F, 1.0F ) ).mult( squareTerrain.getSettings().getSquareSize() );
						Vector3f faceLoc_Top_TopLeft = squareLocation.add( new Vector3f( 0.0F, 0.5F, 0.0F ) ).mult( squareTerrain.getSettings().getSquareSize() );
						Vector3f faceLoc_Top_TopRight = squareLocation.add( new Vector3f( 1.0F, 0.5F, 0.0F ) ).mult( squareTerrain.getSettings().getSquareSize() );
						Vector3f faceLoc_Top_BottomLeft = squareLocation.add( new Vector3f( 0.0F, 0.5F, 1.0F ) ).mult( squareTerrain.getSettings().getSquareSize() );
						Vector3f faceLoc_Top_BottomRight = squareLocation.add( new Vector3f( 1.0F, 0.5F, 1.0F ) ).mult( squareTerrain.getSettings().getSquareSize() );
						
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
						
						tmpBP.blockSkin = square.getSkin();
						
						squareProperties[x1][y1][z1] = tmpBP;
					}
				}
			}
		}
		
		for ( int x1 = 0; x1 < squareTerrain.getSettings().getChunkSizeX(); x1 = x1 + 2 )
		{
			for ( int y1 = 0; y1 < squareTerrain.getSettings().getChunkSizeY(); y1++ )
			{
				for ( int z1 = 0; z1 < squareTerrain.getSettings().getChunkSizeZ(); z1 = z1 + 2 )
				{
					tmpLocation.set( x1, y1, z1 );
					UniqueSquare square = chunk.getSquare( tmpLocation );
					if ( square != null && square.getParentClass().equals( BlockBase.class ) )
					{
						if ( meshMerger.shouldFaceBeAdded( chunk, tmpLocation, Square.Face.Top ) )
						{
							squareProperties[x1][y1][z1].blockSkin = new BlockSkin( new Skin_TextureLocation( 0, 3 ), false );
							BlockProperties bp = getBlockProperties( squareProperties, tmpLocation );
							
							/*
							 * Vector3Int x = getNeighbor( tmpLocation, Square.Face.West ); setBlockProperties(
							 * squareProperties, x, getBlockProperties( squareProperties, x ).corner_top_2( bp.corner_top_1 )
							 * ); setBlockProperties( squareProperties, x, getBlockProperties( squareProperties, x
							 * ).corner_top_3( bp.corner_top_4 ) );
							 * 
							 * x = getNeighbor( tmpLocation, Square.Face.East ); setBlockProperties( squareProperties, x,
							 * getBlockProperties( squareProperties, x ).corner_top_1( bp.corner_top_2 ) ); setBlockProperties(
							 * squareProperties, x, getBlockProperties( squareProperties, x ).corner_top_4( bp.corner_top_3 )
							 * );
							 */
							
							Vector3Int x = getNeighbor( tmpLocation, Square.Face.North );
							BlockProperties tbp = getBlockPropertiesNull( squareProperties, x );
							
							if ( tbp == null )
							{
								x = getNeighbor( x, Square.Face.Bottom );
								tbp = getBlockPropertiesNull( squareProperties, x );
								
								if ( tbp != null )
								{
									tbp.corner_top_4( bp.corner_top_1 );
									tbp.corner_top_3( bp.corner_top_2 );
									setBlockProperties( squareProperties, x, tbp );
								}
							}
							else
							{
								if ( meshMerger.shouldFaceBeAdded( chunk, x, Square.Face.Top ) )
								{
									tbp.corner_top_4( bp.corner_top_1 );
									tbp.corner_top_3( bp.corner_top_2 );
									setBlockProperties( squareProperties, x, tbp );
								}
								else
								{
									x = getNeighbor( x, Square.Face.Top );
									tbp = getBlockPropertiesNull( squareProperties, x );
									
									if ( tbp != null && meshMerger.shouldFaceBeAdded( chunk, x, Square.Face.Top ) )
									{
										tbp.corner_top_4( bp.corner_top_1 );
										tbp.corner_top_3( bp.corner_top_2 );
										setBlockProperties( squareProperties, x, tbp );
									}
								}
							}
							
							/*
							 * x = getNeighbor( tmpLocation, Square.Face.South ); setBlockProperties( squareProperties, x,
							 * getBlockProperties( squareProperties, x ).corner_top_1( bp.corner_top_4 ) ); setBlockProperties(
							 * squareProperties, x, getBlockProperties( squareProperties, x ).corner_top_2( bp.corner_top_3 )
							 * );
							 */
						}
					}
				}
			}
		}
		
		for ( int x1 = 0; x1 < squareTerrain.getSettings().getChunkSizeX(); x1++ )
		{
			for ( int y1 = 0; y1 < squareTerrain.getSettings().getChunkSizeY(); y1++ )
			{
				for ( int z1 = 0; z1 < squareTerrain.getSettings().getChunkSizeZ(); z1++ )
				{
					tmpLocation.set( x1, y1, z1 );
					UniqueSquare square = chunk.getSquare( tmpLocation );
					if ( square != null && square.getParentClass().equals( BlockBase.class ) )
					{
						Vector3f squareLocation = new Vector3f( x1, y1, z1 );
						
						BlockProperties bp = squareProperties[x1][y1][z1];
						
						BlockSkin squareSkin = bp.blockSkin;
						
						Vector3f faceLoc_Bottom_TopLeft = bp.corner_bottom_1;
						Vector3f faceLoc_Bottom_TopRight = bp.corner_bottom_2;
						Vector3f faceLoc_Bottom_BottomLeft = bp.corner_bottom_4;
						Vector3f faceLoc_Bottom_BottomRight = bp.corner_bottom_3;
						
						Vector3f faceLoc_Top_TopLeft = bp.corner_top_1;
						Vector3f faceLoc_Top_TopRight = bp.corner_top_2;
						Vector3f faceLoc_Top_BottomLeft = bp.corner_top_4;
						Vector3f faceLoc_Top_BottomRight = bp.corner_top_3;
						
						/*
						 * if ( meshMerger.shouldFaceBeAdded( chunk, tmpLocation, Square.Face.Top ) ) { s1 =
						 * getBlockProperties( squareProperties, getNeighbor( tmpLocation, Square.Face.West ) );
						 * 
						 * // If null them there so no square on the right. if ( s1 == null ) { faceLoc_Top_TopLeft =
						 * faceLoc_Top_TopLeft.subtract( new Vector3f( 0, squareTerrain.getSettings().getSquareSize() / 2f, 0
						 * ) ); faceLoc_Top_BottomLeft = faceLoc_Top_BottomLeft.subtract( new Vector3f( 0,
						 * squareTerrain.getSettings().getSquareSize() / 2f, 0 ) ); } else { s2 = getBlockProperties(
						 * squareProperties, getNeighbor( getNeighbor( tmpLocation, Square.Face.West ), Square.Face.Top ) );
						 * 
						 * if ( s2 != null ) { faceLoc_Top_TopLeft = faceLoc_Top_TopLeft.add( new Vector3f( 0,
						 * squareTerrain.getSettings().getSquareSize() / 2f, 0 ) ); faceLoc_Top_BottomLeft =
						 * faceLoc_Top_BottomLeft.add( new Vector3f( 0, squareTerrain.getSettings().getSquareSize() / 2f, 0 )
						 * ); } }
						 * 
						 * s1 = getBlockProperties( squareProperties, getNeighbor( tmpLocation, Square.Face.East ) );
						 * 
						 * if ( s1 == null ) { faceLoc_Top_TopRight = faceLoc_Top_TopRight.subtract( new Vector3f( 0,
						 * squareTerrain.getSettings().getSquareSize() / 2f, 0 ) ); faceLoc_Top_BottomRight =
						 * faceLoc_Top_BottomRight.subtract( new Vector3f( 0, squareTerrain.getSettings().getSquareSize() /
						 * 2f, 0 ) ); } else { s2 = getBlockProperties( squareProperties, getNeighbor( getNeighbor(
						 * tmpLocation, Square.Face.East ), Square.Face.Top ) );
						 * 
						 * if ( s2 != null ) { faceLoc_Top_TopRight = faceLoc_Top_TopRight.add( new Vector3f( 0,
						 * squareTerrain.getSettings().getSquareSize() / 2f, 0 ) ); faceLoc_Top_BottomRight =
						 * faceLoc_Top_BottomRight.add( new Vector3f( 0, squareTerrain.getSettings().getSquareSize() / 2f, 0 )
						 * ); } }
						 * 
						 * s1 = getBlockProperties( squareProperties, getNeighbor( tmpLocation, Square.Face.North ) );
						 * 
						 * if ( s1 == null ) { faceLoc_Top_TopRight = faceLoc_Top_TopRight.subtract( new Vector3f( 0,
						 * squareTerrain.getSettings().getSquareSize() / 2f, 0 ) ); faceLoc_Top_TopLeft =
						 * faceLoc_Top_TopLeft.subtract( new Vector3f( 0, squareTerrain.getSettings().getSquareSize() / 2f, 0
						 * ) ); } else { s2 = getBlockProperties( squareProperties, getNeighbor( getNeighbor( tmpLocation,
						 * Square.Face.North ), Square.Face.Top ) );
						 * 
						 * if ( s2 != null ) { faceLoc_Top_TopRight = faceLoc_Top_TopRight.add( new Vector3f( 0,
						 * squareTerrain.getSettings().getSquareSize() / 2f, 0 ) ); faceLoc_Top_TopLeft =
						 * faceLoc_Top_TopLeft.add( new Vector3f( 0, squareTerrain.getSettings().getSquareSize() / 2f, 0 ) );
						 * } }
						 * 
						 * s1 = getBlockProperties( squareProperties, getNeighbor( tmpLocation, Square.Face.South ) );
						 * 
						 * if ( s1 == null ) { faceLoc_Top_BottomRight = faceLoc_Top_BottomRight.subtract( new Vector3f( 0,
						 * squareTerrain.getSettings().getSquareSize() / 2f, 0 ) ); faceLoc_Top_BottomLeft =
						 * faceLoc_Top_BottomLeft.subtract( new Vector3f( 0, squareTerrain.getSettings().getSquareSize() / 2f,
						 * 0 ) ); } else { s2 = getBlockProperties( squareProperties, getNeighbor( getNeighbor( tmpLocation,
						 * Square.Face.South ), Square.Face.Top ) );
						 * 
						 * if ( s2 != null ) { faceLoc_Top_BottomRight = faceLoc_Top_BottomRight.add( new Vector3f( 0,
						 * squareTerrain.getSettings().getSquareSize() / 2f, 0 ) ); faceLoc_Top_BottomLeft =
						 * faceLoc_Top_BottomLeft.add( new Vector3f( 0, squareTerrain.getSettings().getSquareSize() / 2f, 0 )
						 * ); } } }
						 */
						
						if ( meshMerger.shouldFaceBeAdded( chunk, tmpLocation, Square.Face.Top ) )
						{
							addVerticeIndexes( verticeList, indicesList );
							verticeList.add( faceLoc_Top_BottomLeft );
							verticeList.add( faceLoc_Top_BottomRight );
							verticeList.add( faceLoc_Top_TopLeft );
							verticeList.add( faceLoc_Top_TopRight );
							addSquareTextureCoordinates( textureCoordinateList, squareSkin.getTextureLocation( chunk, tmpLocation, Square.Face.Top ) );
						}
						
						if ( meshMerger.shouldFaceBeAdded( chunk, tmpLocation, Square.Face.Bottom ) )
						{
							addVerticeIndexes( verticeList, indicesList );
							verticeList.add( faceLoc_Bottom_BottomRight );
							verticeList.add( faceLoc_Bottom_BottomLeft );
							verticeList.add( faceLoc_Bottom_TopRight );
							verticeList.add( faceLoc_Bottom_TopLeft );
							addSquareTextureCoordinates( textureCoordinateList, squareSkin.getTextureLocation( chunk, tmpLocation, Square.Face.Bottom ) );
						}
						
						if ( meshMerger.shouldFaceBeAdded( chunk, tmpLocation, Square.Face.West ) )
						{
							addVerticeIndexes( verticeList, indicesList );
							verticeList.add( faceLoc_Bottom_TopLeft );
							verticeList.add( faceLoc_Bottom_BottomLeft );
							verticeList.add( faceLoc_Top_TopLeft );
							verticeList.add( faceLoc_Top_BottomLeft );
							addSquareTextureCoordinates( textureCoordinateList, squareSkin.getTextureLocation( chunk, tmpLocation, Square.Face.West ) );
						}
						
						if ( meshMerger.shouldFaceBeAdded( chunk, tmpLocation, Square.Face.East ) )
						{
							addVerticeIndexes( verticeList, indicesList );
							verticeList.add( faceLoc_Bottom_BottomRight );
							verticeList.add( faceLoc_Bottom_TopRight );
							verticeList.add( faceLoc_Top_BottomRight );
							verticeList.add( faceLoc_Top_TopRight );
							addSquareTextureCoordinates( textureCoordinateList, squareSkin.getTextureLocation( chunk, tmpLocation, Square.Face.East ) );
						}
						
						if ( meshMerger.shouldFaceBeAdded( chunk, tmpLocation, Square.Face.North ) )
						{
							addVerticeIndexes( verticeList, indicesList );
							verticeList.add( faceLoc_Bottom_BottomLeft );
							verticeList.add( faceLoc_Bottom_BottomRight );
							verticeList.add( faceLoc_Top_BottomLeft );
							verticeList.add( faceLoc_Top_BottomRight );
							addSquareTextureCoordinates( textureCoordinateList, squareSkin.getTextureLocation( chunk, tmpLocation, Square.Face.North ) );
						}
						
						if ( meshMerger.shouldFaceBeAdded( chunk, tmpLocation, Square.Face.South ) )
						{
							addVerticeIndexes( verticeList, indicesList );
							verticeList.add( faceLoc_Bottom_TopRight );
							verticeList.add( faceLoc_Bottom_TopLeft );
							verticeList.add( faceLoc_Top_TopRight );
							verticeList.add( faceLoc_Top_TopLeft );
							addSquareTextureCoordinates( textureCoordinateList, squareSkin.getTextureLocation( chunk, tmpLocation, Square.Face.South ) );
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
	
	private static void loadMeshDataOLD( ChunkControl chunk, Chunk_MeshMerger meshMerger )
	{
		ArrayList verticeList = new ArrayList();
		ArrayList textureCoordinateList = new ArrayList();
		ArrayList indicesList = new ArrayList();
		TerrainControl squareTerrain = chunk.getTerrain();
		Vector3Int tmpLocation = new Vector3Int();
		
		squareProperties = new BlockProperties[squareTerrain.getSettings().getChunkSizeX()][squareTerrain.getSettings().getChunkSizeY()][squareTerrain.getSettings().getChunkSizeZ()];
		
		for ( int x1 = 0; x1 < squareTerrain.getSettings().getChunkSizeX(); x1++ )
		{
			for ( int y1 = 0; y1 < squareTerrain.getSettings().getChunkSizeY(); y1++ )
			{
				for ( int z1 = 0; z1 < squareTerrain.getSettings().getChunkSizeZ(); z1++ )
				{
					tmpLocation.set( x1, y1, z1 );
					UniqueSquare square = chunk.getSquare( tmpLocation );
					if ( square != null && square.getParentClass().equals( BlockBase.class ) )
					{
						if ( meshMerger.shouldFaceBeAdded( chunk, tmpLocation, Square.Face.Top ) )
						{
							squareProperties[x1][y1][z1] = new BlockProperties();
							
							if ( meshMerger.shouldFaceBeAdded( chunk, tmpLocation, Square.Face.West ) )
								squareProperties[x1][y1][z1].side_top_left = true;
							
							if ( meshMerger.shouldFaceBeAdded( chunk, tmpLocation, Square.Face.East ) )
								squareProperties[x1][y1][z1].side_top_right = true;
							
							if ( meshMerger.shouldFaceBeAdded( chunk, tmpLocation, Square.Face.North ) )
								squareProperties[x1][y1][z1].side_top_front = true;
							
							if ( meshMerger.shouldFaceBeAdded( chunk, tmpLocation, Square.Face.South ) )
								squareProperties[x1][y1][z1].side_top_back = true;
						}
					}
				}
			}
		}
		
		for ( int x2 = 0; x2 < squareTerrain.getSettings().getChunkSizeX(); x2++ )
		{
			for ( int y2 = 0; y2 < squareTerrain.getSettings().getChunkSizeY(); y2++ )
			{
				for ( int z2 = 0; z2 < squareTerrain.getSettings().getChunkSizeZ(); z2++ )
				{
					tmpLocation.set( x2, y2, z2 );
					UniqueSquare square = chunk.getSquare( tmpLocation );
					if ( square != null && square.getParentClass().equals( BlockBase.class ) )
					{
						BlockSkin squareSkin = square.getSkin();
						Vector3f squareLocation = new Vector3f( x2, y2, z2 );
						
						float mb = squareTerrain.getSettings().getSquareSize();
						
						Vector3f faceLoc_Bottom_TopLeft = squareLocation.add( new Vector3f( 0.0F, 0.0F, 0.0F ) ).mult( new Vector3f( mb, mb, mb ) );
						Vector3f faceLoc_Bottom_TopRight = squareLocation.add( new Vector3f( 1.0F, 0.0F, 0.0F ) ).mult( new Vector3f( mb, mb, mb ) );
						Vector3f faceLoc_Bottom_BottomLeft = squareLocation.add( new Vector3f( 0.0F, 0.0F, 1.0F ) ).mult( new Vector3f( mb, mb, mb ) );
						Vector3f faceLoc_Bottom_BottomRight = squareLocation.add( new Vector3f( 1.0F, 0.0F, 1.0F ) ).mult( new Vector3f( mb, mb, mb ) );
						Vector3f faceLoc_Top_TopLeft = squareLocation.add( new Vector3f( 0.0F, 1.0F, 0.0F ) ).mult( new Vector3f( mb, mb, mb ) );
						Vector3f faceLoc_Top_TopRight = squareLocation.add( new Vector3f( 1.0F, 1.0F, 0.0F ) ).mult( new Vector3f( mb, mb, mb ) );
						Vector3f faceLoc_Top_BottomLeft = squareLocation.add( new Vector3f( 0.0F, 1.0F, 1.0F ) ).mult( new Vector3f( mb, mb, mb ) );
						Vector3f faceLoc_Top_BottomRight = squareLocation.add( new Vector3f( 1.0F, 1.0F, 1.0F ) ).mult( new Vector3f( mb, mb, mb ) );
						
						BlockProperties bp = getBlockProperties( squareProperties, tmpLocation );
						BlockProperties s1;
						BlockProperties s2;
						
						if ( meshMerger.shouldFaceBeAdded( chunk, tmpLocation, Square.Face.Top ) )
						{
							if ( bp.side_top_left )
							{
								s1 = getBlockProperties( squareProperties, getNeighbor( tmpLocation, Square.Face.North ) );
								s2 = getBlockProperties( squareProperties, getNeighbor( tmpLocation, Square.Face.South ) );
								
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
								s1 = getBlockProperties( squareProperties, getNeighbor( tmpLocation, Square.Face.North ) );
								s2 = getBlockProperties( squareProperties, getNeighbor( tmpLocation, Square.Face.South ) );
								
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
								// squareSkin = new SquareSkin( new SquareSkin_TextureLocation( 0, 3 ), false );
								
								s1 = getBlockProperties( squareProperties, getNeighbor( tmpLocation, Square.Face.West ) );
								s2 = getBlockProperties( squareProperties, getNeighbor( tmpLocation, Square.Face.East ) );
								
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
								s1 = getBlockProperties( squareProperties, getNeighbor( tmpLocation, Square.Face.West ) );
								s2 = getBlockProperties( squareProperties, getNeighbor( tmpLocation, Square.Face.East ) );
								
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
							
							Vector3Int vec = getNeighbor( tmpLocation, Square.Face.Top );
							
							s1 = getBlockProperties( squareProperties, getNeighbor( vec, Square.Face.West ) );
							s2 = getBlockProperties( squareProperties, getNeighbor( vec, Square.Face.North ) );
							BlockProperties s3 = getBlockProperties( squareProperties, getNeighbor( vec, Square.Face.East ) );
							BlockProperties s4 = getBlockProperties( squareProperties, getNeighbor( vec, Square.Face.South ) );
							BlockProperties stmp;
							
							if ( !s1.dummyInst && !s2.dummyInst && !s3.dummyInst && !s4.dummyInst )
							{
								// Do Nothing
							}
							else if ( !s1.dummyInst && !s2.dummyInst )
							{
								stmp = getBlockProperties( squareProperties, getNeighbor( getNeighbor( vec, Square.Face.West ), Square.Face.East ) );
								
								faceLoc_Top_TopLeft = faceLoc_Top_TopLeft.add( new Vector3f( 0, squareTerrain.getSettings().getSquareSize(), 0 ) );
							}
							else if ( !s2.dummyInst && !s3.dummyInst )
							{
								faceLoc_Top_TopRight = faceLoc_Top_TopRight.add( new Vector3f( 0, squareTerrain.getSettings().getSquareSize(), 0 ) );
							}
							else if ( !s3.dummyInst && !s4.dummyInst )
							{
								faceLoc_Top_BottomRight = faceLoc_Top_BottomRight.add( new Vector3f( 0, squareTerrain.getSettings().getSquareSize(), 0 ) );
							}
							else if ( !s4.dummyInst && !s1.dummyInst )
							{
								faceLoc_Top_BottomLeft = faceLoc_Top_BottomLeft.add( new Vector3f( 0, squareTerrain.getSettings().getSquareSize(), 0 ) );
							}
						}
						
						if ( meshMerger.shouldFaceBeAdded( chunk, tmpLocation, Square.Face.Top ) )
						{
							addVerticeIndexes( verticeList, indicesList );
							verticeList.add( faceLoc_Top_BottomLeft );
							verticeList.add( faceLoc_Top_BottomRight );
							verticeList.add( faceLoc_Top_TopLeft );
							verticeList.add( faceLoc_Top_TopRight );
							addSquareTextureCoordinates( textureCoordinateList, squareSkin.getTextureLocation( chunk, tmpLocation, Square.Face.Top ) );
						}
						
						if ( meshMerger.shouldFaceBeAdded( chunk, tmpLocation, Square.Face.Bottom ) )
						{
							addVerticeIndexes( verticeList, indicesList );
							verticeList.add( faceLoc_Bottom_BottomRight );
							verticeList.add( faceLoc_Bottom_BottomLeft );
							verticeList.add( faceLoc_Bottom_TopRight );
							verticeList.add( faceLoc_Bottom_TopLeft );
							addSquareTextureCoordinates( textureCoordinateList, squareSkin.getTextureLocation( chunk, tmpLocation, Square.Face.Bottom ) );
						}
						
						boolean side1 = false;
						boolean side2 = false;
						boolean side3 = false;
						boolean side4 = false;
						
						if ( meshMerger.shouldFaceBeAdded( chunk, tmpLocation, Square.Face.West ) )
						{
							side4 = true;
							side1 = true;
							side2 = true;
						}
						
						if ( meshMerger.shouldFaceBeAdded( chunk, tmpLocation, Square.Face.East ) )
						{
							side2 = true;
							side3 = true;
							side4 = true;
						}
						
						if ( meshMerger.shouldFaceBeAdded( chunk, tmpLocation, Square.Face.North ) )
						{
							side1 = true;
							side2 = true;
							side3 = true;
						}
						
						if ( meshMerger.shouldFaceBeAdded( chunk, tmpLocation, Square.Face.South ) )
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
							addSquareTextureCoordinates( textureCoordinateList, squareSkin.getTextureLocation( chunk, tmpLocation, Square.Face.West ) );
						}
						
						if ( side3 )
						{
							addVerticeIndexes( verticeList, indicesList );
							verticeList.add( faceLoc_Bottom_BottomRight );
							verticeList.add( faceLoc_Bottom_TopRight );
							verticeList.add( faceLoc_Top_BottomRight );
							verticeList.add( faceLoc_Top_TopRight );
							addSquareTextureCoordinates( textureCoordinateList, squareSkin.getTextureLocation( chunk, tmpLocation, Square.Face.East ) );
						}
						
						if ( side2 )
						{
							addVerticeIndexes( verticeList, indicesList );
							verticeList.add( faceLoc_Bottom_BottomLeft );
							verticeList.add( faceLoc_Bottom_BottomRight );
							verticeList.add( faceLoc_Top_BottomLeft );
							verticeList.add( faceLoc_Top_BottomRight );
							addSquareTextureCoordinates( textureCoordinateList, squareSkin.getTextureLocation( chunk, tmpLocation, Square.Face.North ) );
						}
						
						if ( side4 )
						{
							addVerticeIndexes( verticeList, indicesList );
							verticeList.add( faceLoc_Bottom_TopRight );
							verticeList.add( faceLoc_Bottom_TopLeft );
							verticeList.add( faceLoc_Top_TopRight );
							verticeList.add( faceLoc_Top_TopLeft );
							addSquareTextureCoordinates( textureCoordinateList, squareSkin.getTextureLocation( chunk, tmpLocation, Square.Face.South ) );
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
	
	private static void loadMeshData( ChunkControl chunk, Chunk_MeshMerger meshMerger )
	{
		ArrayList verticeList = new ArrayList();
		ArrayList textureCoordinateList = new ArrayList();
		ArrayList indicesList = new ArrayList();
		TerrainControl blockTerrain = chunk.getTerrain();
		Vector3Int tmpLocation = new Vector3Int();
		for ( int x = 0; x < blockTerrain.getSettings().getChunkSizeX(); x++ )
		{
			for ( int y = 0; y < blockTerrain.getSettings().getChunkSizeY(); y++ )
			{
				for ( int z = 0; z < blockTerrain.getSettings().getChunkSizeZ(); z++ )
				{
					tmpLocation.set( x, y, z );
					UniqueSquare block = chunk.getSquare( tmpLocation );
					
					if ( block != null && block.getParentClass().equals( BlockBase.class ) )
					{
						BlockSkin blockSkin = block.getSkin();
						Vector3f blockLocation = new Vector3f( x, y, z );
						
						Vector3f faceLoc_Bottom_TopLeft = blockLocation.add( new Vector3f( 0.0F, 0.0F, 0.0F ) ).mult( blockTerrain.getSettings().getSquareSize() );
						Vector3f faceLoc_Bottom_TopRight = blockLocation.add( new Vector3f( 1.0F, 0.0F, 0.0F ) ).mult( blockTerrain.getSettings().getSquareSize() );
						Vector3f faceLoc_Bottom_BottomLeft = blockLocation.add( new Vector3f( 0.0F, 0.0F, 1.0F ) ).mult( blockTerrain.getSettings().getSquareSize() );
						Vector3f faceLoc_Bottom_BottomRight = blockLocation.add( new Vector3f( 1.0F, 0.0F, 1.0F ) ).mult( blockTerrain.getSettings().getSquareSize() );
						Vector3f faceLoc_Top_TopLeft = blockLocation.add( new Vector3f( 0.0F, 1.0F, 0.0F ) ).mult( blockTerrain.getSettings().getSquareSize() );
						Vector3f faceLoc_Top_TopRight = blockLocation.add( new Vector3f( 1.0F, 1.0F, 0.0F ) ).mult( blockTerrain.getSettings().getSquareSize() );
						Vector3f faceLoc_Top_BottomLeft = blockLocation.add( new Vector3f( 0.0F, 1.0F, 1.0F ) ).mult( blockTerrain.getSettings().getSquareSize() );
						Vector3f faceLoc_Top_BottomRight = blockLocation.add( new Vector3f( 1.0F, 1.0F, 1.0F ) ).mult( blockTerrain.getSettings().getSquareSize() );
						
						if ( meshMerger.shouldFaceBeAdded( chunk, tmpLocation, Square.Face.Top ) )
						{
							addVerticeIndexes( verticeList, indicesList );
							verticeList.add( faceLoc_Top_BottomLeft );
							verticeList.add( faceLoc_Top_BottomRight );
							verticeList.add( faceLoc_Top_TopLeft );
							verticeList.add( faceLoc_Top_TopRight );
							addSquareTextureCoordinates( textureCoordinateList, blockSkin.getTextureLocation( chunk, tmpLocation, Square.Face.Top ) );
						}
						if ( meshMerger.shouldFaceBeAdded( chunk, tmpLocation, Square.Face.Bottom ) )
						{
							addVerticeIndexes( verticeList, indicesList );
							verticeList.add( faceLoc_Bottom_BottomRight );
							verticeList.add( faceLoc_Bottom_BottomLeft );
							verticeList.add( faceLoc_Bottom_TopRight );
							verticeList.add( faceLoc_Bottom_TopLeft );
							addSquareTextureCoordinates( textureCoordinateList, blockSkin.getTextureLocation( chunk, tmpLocation, Square.Face.Bottom ) );
						}
						if ( meshMerger.shouldFaceBeAdded( chunk, tmpLocation, Square.Face.West ) )
						{
							addVerticeIndexes( verticeList, indicesList );
							verticeList.add( faceLoc_Bottom_TopLeft );
							verticeList.add( faceLoc_Bottom_BottomLeft );
							verticeList.add( faceLoc_Top_TopLeft );
							verticeList.add( faceLoc_Top_BottomLeft );
							addSquareTextureCoordinates( textureCoordinateList, blockSkin.getTextureLocation( chunk, tmpLocation, Square.Face.West ) );
						}
						if ( meshMerger.shouldFaceBeAdded( chunk, tmpLocation, Square.Face.East ) )
						{
							addVerticeIndexes( verticeList, indicesList );
							verticeList.add( faceLoc_Bottom_BottomRight );
							verticeList.add( faceLoc_Bottom_TopRight );
							verticeList.add( faceLoc_Top_BottomRight );
							verticeList.add( faceLoc_Top_TopRight );
							addSquareTextureCoordinates( textureCoordinateList, blockSkin.getTextureLocation( chunk, tmpLocation, Square.Face.East ) );
						}
						if ( meshMerger.shouldFaceBeAdded( chunk, tmpLocation, Square.Face.North ) )
						{
							addVerticeIndexes( verticeList, indicesList );
							verticeList.add( faceLoc_Bottom_BottomLeft );
							verticeList.add( faceLoc_Bottom_BottomRight );
							verticeList.add( faceLoc_Top_BottomLeft );
							verticeList.add( faceLoc_Top_BottomRight );
							addSquareTextureCoordinates( textureCoordinateList, blockSkin.getTextureLocation( chunk, tmpLocation, Square.Face.North ) );
						}
						if ( meshMerger.shouldFaceBeAdded( chunk, tmpLocation, Square.Face.South ) )
						{
							addVerticeIndexes( verticeList, indicesList );
							verticeList.add( faceLoc_Bottom_TopRight );
							verticeList.add( faceLoc_Bottom_TopLeft );
							verticeList.add( faceLoc_Top_TopRight );
							verticeList.add( faceLoc_Top_TopLeft );
							addSquareTextureCoordinates( textureCoordinateList, blockSkin.getTextureLocation( chunk, tmpLocation, Square.Face.South ) );
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
	
	private static void addSquareTextureCoordinates( ArrayList<Vector2f> textureCoordinatesList, Skin_TextureLocation textureLocation )
	{
		textureCoordinatesList.add( getTextureCoordinates( textureLocation, 0, 0 ) );
		textureCoordinatesList.add( getTextureCoordinates( textureLocation, 1, 0 ) );
		textureCoordinatesList.add( getTextureCoordinates( textureLocation, 0, 1 ) );
		textureCoordinatesList.add( getTextureCoordinates( textureLocation, 1, 1 ) );
	}
	
	private static Vector2f getTextureCoordinates( Skin_TextureLocation textureLocation, int xUnitsToAdd, int yUnitsToAdd )
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

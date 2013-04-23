package com.ufharmony.grid;

import java.util.ArrayList;

import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.debug.Arrow;
import com.jme3.util.BufferUtils;
import com.ufharmony.Extras;
import com.ufharmony.Main;
import com.ufharmony.blocks.BlockBase;
import com.ufharmony.utils.Vector3Int;

public class Bandage_MeshOptimizer
{
	private static Vector3f[] vertices;
	private static Vector2f[] textureCoordinates;
	private static int[] indices;
	
	public static Mesh generateOptimizedMesh( BandageControl bc )
	{
		loadMeshData( bc );
		return generateMesh();
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
	
	private static void loadMeshData( BandageControl bandAid )
	{
		ArrayList<Vector3f> verticeList = new ArrayList<Vector3f>();
		ArrayList<Vector2f> textureCoordinateList = new ArrayList<Vector2f>();
		ArrayList<Integer> indicesList = new ArrayList<Integer>();
		TerrainControl squareTerrain = bandAid.getTerrain();
		Vector3Int tmpLocation = new Vector3Int();
		Vector3Int tmpLocationLocal = new Vector3Int();
		float squareSize = TerrainControl.getSettings().getSquareSize();
		
		Vector3Int maxXY = bandAid.getMaxXY();
		
		for ( int x1 = ( maxXY.getX() > 0 ? 2 : 0 ); x1 <= maxXY.getX(); x1 = x1 + 2 )
		{
			for ( int z1 = 0; z1 <= maxXY.getZ(); z1 = z1 + 2 )
			{
				for ( int y1 = TerrainControl.getSettings().getChunkSizeY() - 1; y1 >= 0; y1-- )
				{
					tmpLocationLocal = new Vector3Int( x1, y1, z1 ).subtract( 1, 0, 1 );
					tmpLocation = new Vector3Int( x1, y1, z1 ).add( bandAid.getLocation() ).subtract( 1, 0, 1 );
					
					UniqueSquare us = squareTerrain.getSquare( tmpLocation );
					
					if ( us != null && us.getParentClass().equals( BlockBase.class ) )
					{
						Vector3f point = new Vector3f( tmpLocationLocal.getX(), tmpLocationLocal.getY(), tmpLocationLocal.getZ() ).mult( new Vector3f( squareSize, squareSize / 2f, squareSize ) );
						
						Vector2f xYS[] = new Vector2f[] { new Vector2f( -1, 0 ), new Vector2f( -1, -1 ), new Vector2f( 0, -1 ), new Vector2f( 1, -1 ), new Vector2f( 1, 0 ), new Vector2f( 1, 1 ), new Vector2f( 0, 1 ), new Vector2f( -1, 1 ) };
						Vector3f pointArray[] = new Vector3f[8];
						int cc = 0;
						
						for ( Vector2f xY : xYS )
						{
							for ( int y2 = TerrainControl.getSettings().getChunkSizeY() - 1; y2 >= 0; y2-- )
							{
								Vector3Int newLocation = tmpLocation.clone().setY( 0 ).add( new Vector3Int( (int) xY.getX(), y2, (int) xY.getY() ) );
								Vector3Int newLocationLocal = tmpLocationLocal.clone().setY( 0 ).add( new Vector3Int( (int) xY.getX(), y2, (int) xY.getY() ) );
								
								UniqueSquare us2 = squareTerrain.getSquare( newLocation );
								
								if ( us2 != null )
								{
									pointArray[cc] = new Vector3f( newLocationLocal.getX(), newLocationLocal.getY(), newLocationLocal.getZ() ).add( new Vector3f( 1, 0, 1 ) ).mult( new Vector3f( squareSize, squareSize / 2f, squareSize ) );
									break;
								}
							}
							
							if ( !Extras.isValidIndex( pointArray, cc ) || pointArray[cc] == null )
								pointArray[cc] = point.add( new Vector3f( xY.getX(), 0, xY.getY() ).mult( new Vector3f( squareSize, squareSize / 2f, squareSize ) ) );
							
							cc++;
						}
						
						BlockSkin bs = us.getSkin();
						
						// Square #1
						if ( Extras.isValidIndex( pointArray, 0 ) && Extras.isValidIndex( pointArray, 1 ) && Extras.isValidIndex( pointArray, 2 ) )
						{
							addVerticeIndexes( verticeList, indicesList );
							verticeList.add( pointArray[0] );
							verticeList.add( point );
							verticeList.add( pointArray[1] );
							verticeList.add( pointArray[2] );
							addSquareTextureCoordinates( textureCoordinateList, bs.getTextureLocation( TerrainControl.getInstance().getChunk( tmpLocation ), tmpLocation, Square.Face.Top ) );
						}
						
						// Square #2
						if ( Extras.isValidIndex( pointArray, 2 ) && Extras.isValidIndex( pointArray, 3 ) && Extras.isValidIndex( pointArray, 4 ) )
						{
							addVerticeIndexes( verticeList, indicesList );
							verticeList.add( pointArray[3] );
							verticeList.add( pointArray[2] );
							verticeList.add( pointArray[4] );
							verticeList.add( point );
							addSquareTextureCoordinates( textureCoordinateList, bs.getTextureLocation( TerrainControl.getInstance().getChunk( tmpLocation ), tmpLocation, Square.Face.Top ) );
						}
						
						// Square #3
						if ( Extras.isValidIndex( pointArray, 4 ) && Extras.isValidIndex( pointArray, 5 ) && Extras.isValidIndex( pointArray, 6 ) )
						{
							addVerticeIndexes( verticeList, indicesList );
							verticeList.add( pointArray[7] );
							verticeList.add( pointArray[6] );
							verticeList.add( pointArray[0] );
							verticeList.add( point );
							addSquareTextureCoordinates( textureCoordinateList, bs.getTextureLocation( TerrainControl.getInstance().getChunk( tmpLocation ), tmpLocation, Square.Face.Top ) );
						}
						
						// Square #4
						if ( Extras.isValidIndex( pointArray, 6 ) && Extras.isValidIndex( pointArray, 7 ) && Extras.isValidIndex( pointArray, 0 ) )
						{
							addVerticeIndexes( verticeList, indicesList );
							verticeList.add( pointArray[5] );
							verticeList.add( pointArray[4] );
							verticeList.add( pointArray[6] );
							verticeList.add( point );
							addSquareTextureCoordinates( textureCoordinateList, bs.getTextureLocation( TerrainControl.getInstance().getChunk( tmpLocation ), tmpLocation, Square.Face.Top ) );
						}
						
						// Break for Y loop.
						break;
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

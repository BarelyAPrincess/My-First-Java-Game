package com.ufharmony.grid;

import java.io.IOException;
import java.util.ArrayList;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Texture;
import com.ufharmony.blocks.BlockBase;
import com.ufharmony.network.BitInputStream;
import com.ufharmony.network.BitOutputStream;
import com.ufharmony.network.BitSerializable;
import com.ufharmony.network.CubesSerializer;
import com.ufharmony.utils.Vector3Int;

public class TerrainControl extends AbstractControl implements BitSerializable
{
	private GridSettings settings;
	private ChunkControl[][][] chunks;
	private ArrayList<ChunkListener> chunkListeners = new ArrayList();
	
	public TerrainControl(GridSettings settings, Vector3Int chunksCount)
	{
		this.settings = settings;
		initializeChunks( chunksCount );
	}
	
	private void initializeChunks( Vector3Int chunksCount )
	{
		chunks = new ChunkControl[chunksCount.getX()][chunksCount.getY()][chunksCount.getZ()];
		for ( int x = 0; x < chunks.length; x++ )
			for ( int y = 0; y < chunks[0].length; y++ )
				for ( int z = 0; z < chunks[0][0].length; z++ )
				{
					ChunkControl chunk = new ChunkControl( this, x, y, z );
					chunks[x][y][z] = chunk;
				}
	}
	
	public void setSpatial( Spatial spatial )
	{
		Spatial oldSpatial = this.spatial;
		super.setSpatial( spatial );
		for ( int x = 0; x < chunks.length; x++ )
			for ( int y = 0; y < chunks[0].length; y++ )
				for ( int z = 0; z < chunks[0][0].length; z++ )
					if ( spatial == null )
					{
						oldSpatial.removeControl( chunks[x][y][z] );
					}
					else
						spatial.addControl( chunks[x][y][z] );
	}
	
	protected void controlUpdate( float lastTimePerFrame )
	{
		updateSpatial();
	}
	
	protected void controlRender( RenderManager renderManager, ViewPort viewPort )
	{
	}
	
	public Control cloneForSpatial( Spatial spatial )
	{
		throw new UnsupportedOperationException( "Not supported yet." );
	}
	
	public Square getInst( int x, int y, int z )
	{
		return getInst( new Vector3Int( x, y, z ) );
	}
	
	public Square getInst( Vector3Int location )
	{
		try
		{
			Terrain_LocalSquareState localSquareState = getLocalSquareState( location );
			if ( localSquareState != null )
			{
				return localSquareState.getSquare().getParent();
			}
			
			return null;
		}
		catch ( NullPointerException e )
		{
			return null;
		}
	}
	
	public UniqueSquare getSquare( int x, int y, int z )
	{
		return getSquare( new Vector3Int( x, y, z ) );
	}
	
	public byte getSquareId( int x, int y, int z )
	{
		return getSquareId( new Vector3Int( x, y, z ) );
	}
	
	public UniqueSquare getSquare( Vector3Int location )
	{
		Terrain_LocalSquareState localSquareState = getLocalSquareState( location );
		if ( localSquareState != null )
		{
			return localSquareState.getSquare();
		}
		return null;
	}
	
	public byte getSquareId( Vector3Int location )
	{
		return getSquare( location ).getParent().getId();
	}
	
	public void setSquareArea( Vector3Int location, Vector3Int size, Class<? extends Square> SquareClass )
	{
		Vector3Int tmpLocation = new Vector3Int();
		for ( int x = 0; x < size.getX(); x++ )
			for ( int y = 0; y < size.getY(); y++ )
				for ( int z = 0; z < size.getZ(); z++ )
				{
					tmpLocation.set( location.getX() + x, location.getY() + y, location.getZ() + z );
					setSquare( tmpLocation, SquareClass );
				}
	}
	
	public void setSquare( int x, int y, int z, Class<? extends Square> squareClass )
	{
		setSquare( new Vector3Int( x, y, z ), squareClass );
	}
	
	public void setSquare( Vector3Int location, Class<? extends Square> squareClass )
	{
		Terrain_LocalSquareState localSquareState = getLocalSquareState( location );
		if ( localSquareState != null )
			localSquareState.setSquare( squareClass );
	}
	
	public void removeSquareArea( Vector3Int location, Vector3Int size )
	{
		Vector3Int tmpLocation = new Vector3Int();
		for ( int x = 0; x < size.getX(); x++ )
			for ( int y = 0; y < size.getY(); y++ )
				for ( int z = 0; z < size.getZ(); z++ )
				{
					tmpLocation.set( location.getX() + x, location.getY() + y, location.getZ() + z );
					removeSquare( tmpLocation );
				}
	}
	
	public void removeSquare( int x, int y, int z )
	{
		removeSquare( new Vector3Int( x, y, z ) );
	}
	
	public void removeSquare( Vector3Int location )
	{
		Terrain_LocalSquareState localSquareState = getLocalSquareState( location );
		if ( localSquareState != null )
			localSquareState.removeSquare();
	}
	
	private Terrain_LocalSquareState getLocalSquareState( Vector3Int squareLocation )
	{
		if ( squareLocation.hasNegativeCoordinate() )
		{
			return null;
		}
		ChunkControl chunk = getChunk( squareLocation );
		if ( chunk != null )
		{
			Vector3Int localSquareLocation = getLocalSquareLocation( squareLocation, chunk );
			return new Terrain_LocalSquareState( chunk, localSquareLocation );
		}
		return null;
	}
	
	public ChunkControl getChunk( Vector3Int squareLocation )
	{
		if ( squareLocation.hasNegativeCoordinate() )
		{
			return null;
		}
		Vector3Int chunkLocation = getChunkLocation( squareLocation );
		if ( isValidChunkLocation( chunkLocation ) )
		{
			return chunks[chunkLocation.getX()][chunkLocation.getY()][chunkLocation.getZ()];
		}
		return null;
	}
	
	private boolean isValidChunkLocation( Vector3Int location )
	{
		return Util.isValidIndex( chunks, location );
	}
	
	private Vector3Int getChunkLocation( Vector3Int squareLocation )
	{
		Vector3Int chunkLocation = new Vector3Int();
		int chunkX = squareLocation.getX() / settings.getChunkSizeX();
		int chunkY = squareLocation.getY() / settings.getChunkSizeY();
		int chunkZ = squareLocation.getZ() / settings.getChunkSizeZ();
		chunkLocation.set( chunkX, chunkY, chunkZ );
		return chunkLocation;
	}
	
	public static Vector3Int getLocalSquareLocation( Vector3Int squareLocation, ChunkControl chunk )
	{
		Vector3Int localLocation = new Vector3Int();
		int localX = squareLocation.getX() - chunk.getSquareLocation().getX();
		int localY = squareLocation.getY() - chunk.getSquareLocation().getY();
		int localZ = squareLocation.getZ() - chunk.getSquareLocation().getZ();
		localLocation.set( localX, localY, localZ );
		return localLocation;
	}
	
	public boolean updateSpatial()
	{
		boolean wasUpdatedNeeded = false;
		for ( int x = 0; x < chunks.length; x++ )
		{
			for ( int y = 0; y < chunks[0].length; y++ )
			{
				for ( int z = 0; z < chunks[0][0].length; z++ )
				{
					ChunkControl chunk = chunks[x][y][z];
					if ( chunk.updateSpatial() )
					{
						wasUpdatedNeeded = true;
						for ( int i = 0; i < chunkListeners.size(); i++ )
						{
							ChunkListener squareTerrainListener = (ChunkListener) chunkListeners.get( i );
							squareTerrainListener.onSpatialUpdated( chunk );
						}
					}
				}
			}
		}
		return wasUpdatedNeeded;
	}
	
	public void addChunkListener( ChunkListener squareChunkListener )
	{
		chunkListeners.add( squareChunkListener );
	}
	
	public void removeChunkListener( ChunkListener squareChunkListener )
	{
		chunkListeners.remove( squareChunkListener );
	}
	
	public GridSettings getSettings()
	{
		return settings;
	}
	
	public void setSquaresFromHeightmap( Vector3Int location, String heightmapPath, int maximumHeight, Class<? extends Square> squareClass )
	{
		try
		{
			Texture heightmapTexture = settings.getAssetManager().loadTexture( heightmapPath );
			ImageBasedHeightMap heightmap = new ImageBasedHeightMap( heightmapTexture.getImage(), 1.0F );
			heightmap.load();
			heightmap.setHeightScale( maximumHeight / 255.0F );
			setSquaresFromHeightmap( location, getHeightmapSquareData( heightmap.getScaledHeightMap(), heightmap.getSize() ), squareClass );
		}
		catch ( Exception ex )
		{
			System.out.println( "Error while loading heightmap '" + heightmapPath + "'." );
		}
	}
	
	private static int[][] getHeightmapSquareData( float[] heightmapData, int length )
	{
		int[][] data = new int[heightmapData.length / length][length];
		int x = 0;
		int z = 0;
		for ( int i = 0; i < heightmapData.length; i++ )
		{
			data[x][z] = Math.round( heightmapData[i] );
			x++;
			if ( ( x != 0 ) && ( x % length == 0 ) )
			{
				x = 0;
				z++;
			}
		}
		return data;
	}
	
	public void setSquaresFromHeightmap( Vector3Int location, int[][] heightmap, Class<? extends Square> squareClass )
	{
		Vector3Int tmpLocation = new Vector3Int();
		Vector3Int tmpSize = new Vector3Int();
		for ( int x = 0; x < heightmap.length; x++ )
			for ( int z = 0; z < heightmap[0].length; z++ )
			{
				tmpLocation.set( location.getX() + x, location.getY(), location.getZ() + z );
				tmpSize.set( 1, heightmap[x][z], 1 );
				setSquareArea( tmpLocation, tmpSize, squareClass );
			}
	}
	
	public void setSquaresFromNoise( Vector3Int location, Vector3Int size, float roughness, Class<? extends Square> squareClass )
	{
		Noise noise = new Noise( null, roughness, size.getX(), size.getZ() );
		noise.initialise();
		float gridMinimum = noise.getMinimum();
		float gridLargestDifference = noise.getMaximum() - gridMinimum;
		float[][] grid = noise.getGrid();
		for ( int x = 0; x < grid.length; x++ )
		{
			float[] row = grid[x];
			for ( int z = 0; z < row.length; z++ )
			{
				int squareHeight = (int) ( ( row[z] - gridMinimum ) * 100.0F / gridLargestDifference / 100.0F * size.getY() ) + 1;
				Vector3Int tmpLocation = new Vector3Int();
				for ( int y = 0; y < squareHeight; y++ )
				{
					tmpLocation.set( location.getX() + x, location.getY() + y, location.getZ() + z );
					setSquare( tmpLocation, squareClass );
				}
			}
		}
	}
	
	public void setSquaresForMaximumFaces( Vector3Int location, Vector3Int size, Class<? extends Square> squareClass )
	{
		Vector3Int tmpLocation = new Vector3Int();
		for ( int x = 0; x < size.getX(); x++ )
			for ( int y = 0; y < size.getY(); y++ )
				for ( int z = 0; z < size.getZ(); z++ )
					if ( ( ( x ^ y ^ z ) & 0x1 ) == 1 )
					{
						tmpLocation.set( location.getX() + x, location.getY() + y, location.getZ() + z );
						setSquare( tmpLocation, squareClass );
					}
	}
	
	public TerrainControl clone()
	{
		TerrainControl squareTerrain = new TerrainControl( settings, new Vector3Int() );
		squareTerrain.setSquaresFromTerrain( this );
		return squareTerrain;
	}
	
	public void setSquaresFromTerrain( TerrainControl squareTerrain )
	{
		CubesSerializer.readFromBytes( this, CubesSerializer.writeToBytes( squareTerrain ) );
	}
	
	public void write( BitOutputStream outputStream )
	{
		outputStream.writeInteger( chunks.length );
		outputStream.writeInteger( chunks[0].length );
		outputStream.writeInteger( chunks[0][0].length );
		for ( int x = 0; x < chunks.length; x++ )
			for ( int y = 0; y < chunks[0].length; y++ )
				for ( int z = 0; z < chunks[0][0].length; z++ )
					chunks[x][y][z].write( outputStream );
	}
	
	public void read( BitInputStream inputStream ) throws IOException
	{
		int chunksCountX = inputStream.readInteger();
		int chunksCountY = inputStream.readInteger();
		int chunksCountZ = inputStream.readInteger();
		initializeChunks( new Vector3Int( chunksCountX, chunksCountY, chunksCountZ ) );
		for ( int x = 0; x < chunksCountX; x++ )
			for ( int y = 0; y < chunksCountY; y++ )
				for ( int z = 0; z < chunksCountZ; z++ )
					chunks[x][y][z].read( inputStream );
	}
}

package com.ufharmony.grid;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;

import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.ufharmony.Main;
import com.ufharmony.utils.Vector3Int;

public class ChunkManager
{
	private HashMap<String, ChunkControl> chunks = new HashMap<String, ChunkControl>();
	private HashMap<String, BandageControl> bandAids = new HashMap<String, BandageControl>();
	private ArrayList<ChunkListener> chunkListeners = new ArrayList<ChunkListener>();
	
	public ChunkControl get( int x, int y, int z )
	{
		//System.out.println( "c." + x + "." + y + "." + z );
		
		ChunkControl c = chunks.get( "c." + x + "." + y + "." + z );
		return c;
	}
	
	public ChunkControl get( Vector3Int vec )
	{
		return get( vec.getX(), vec.getY(), vec.getZ() );
	}
	
	public void set( int x, int y, int z, ChunkControl c )
	{
		chunks.put( "c." + x + "." + y + "." + z, c );
	}
	
	public void set( Vector3Int vec, ChunkControl c )
	{
		set( vec.getX(), vec.getY(), vec.getZ(), c );
	}
	
	public BandageControl getBandAid( ChunkControl a, ChunkControl b )
	{
		if ( a == null || b == null )
			return null;
		
		//System.out.println( "Getting Bandage: b." + a.getLocation().getX() + "." + a.getLocation().getY() + "." + a.getLocation().getZ() + "." + b.getLocation().getX() + "." + b.getLocation().getY() + "." + b.getLocation().getZ() );
		
		BandageControl c = bandAids.get( "b." + a.getLocation().getX() + "." + a.getLocation().getY() + "." + a.getLocation().getZ() + "." + b.getLocation().getX() + "." + b.getLocation().getY() + "." + b.getLocation().getZ() );
		return c;
	}
	
	public void setBandAid( ChunkControl a, ChunkControl b, BandageControl c )
	{
		if ( a == null || b == null )
			return;
		
		bandAids.put( "b." + a.getLocation().getX() + "." + a.getLocation().getY() + "." + a.getLocation().getZ() + "." + b.getLocation().getX() + "." + b.getLocation().getY() + "." + b.getLocation().getZ(), c );
	}
	
	public boolean isValidChunk( int x, int y, int z )
	{
		return ( chunks.get( "c." + x + "." + y + "." + z ) != null );
	}
	
	public boolean isValidChunk( Vector3Int vec )
	{
		return isValidChunk( vec.getX(), vec.getY(), vec.getZ() );
	}
	
	public void forEach( AbstractForEach afe )
	{
		try
		{
			for ( ChunkControl squareChunk : chunks.values() )
			{
				afe.forChunk( squareChunk );
			}
		}
		catch ( ConcurrentModificationException e )
		{
			// e.printStackTrace();
		}
	}
	
	/**
	 * This method determines what chunks need updating according to their last update, player location and looking
	 * direction.
	 * 
	 * @param lastTimePerFrame
	 */
	public void chunkPing( float lastTimePerFrame )
	{
		//System.out.println( "There are " + chunks.size() + " loaded. TPF: " + lastTimePerFrame );
		
		// Calculate the players chunk position;
		CharacterControl player = Main.getPlayer();
		
		Vector3f playerLocationTmp = player.getPhysicsLocation().divide( TerrainControl.getSettings().getSquareSize() ).divide( new Vector3f( TerrainControl.getSettings().getChunkSizeX(), TerrainControl.getSettings().getChunkSizeY(), TerrainControl.getSettings().getChunkSizeZ() ) );
		Vector3Int playerLocation = new Vector3Int( (int) playerLocationTmp.getX(), (int) playerLocationTmp.getY(), (int) playerLocationTmp.getZ() );
		
		//System.out.println( "Player is located at: " + playerLocation );
		
		// Update the chunk the player in standing in.
		processChunk( playerLocation.getX(), playerLocation.getY(), playerLocation.getZ() );
		
		// Update the chunk North of player
		processChunk( playerLocation.getX() + 1, playerLocation.getY(), playerLocation.getZ() );
		
		// Update the chunk South of player
		processChunk( playerLocation.getX() - 1, playerLocation.getY(), playerLocation.getZ() );
		
		// Update the chunk West of player
		processChunk( playerLocation.getX(), playerLocation.getY(), playerLocation.getZ() + 1 );
		
		// Update the chunk East of player
		processChunk( playerLocation.getX(), playerLocation.getY(), playerLocation.getZ() - 1 );
		
		// Update the chunk North of player
		processChunk( playerLocation.getX() + 1, playerLocation.getY(), playerLocation.getZ() + 1 );
		
		// Update the chunk South of player
		processChunk( playerLocation.getX() - 1, playerLocation.getY(), playerLocation.getZ() - 1 );
		
		// Update the chunk West of player
		processChunk( playerLocation.getX() - 1, playerLocation.getY(), playerLocation.getZ() + 1 );
		
		// Update the chunk East of player
		processChunk( playerLocation.getX() + 1, playerLocation.getY(), playerLocation.getZ() - 1 );
		
		/*
		Vector3Int playerLookingAt = playerLocation;
		Vector3Int multi = new Vector3Int();
		
		if ( player.getWalkDirection().getX() < -0.1f )
		{
			multi.setX( -2 );
		}
		else if ( player.getWalkDirection().getX() > 0.1f )
		{
			multi.setX( 2 );
		}
		
		if ( player.getWalkDirection().getZ() < -0.1f )
		{
			multi.setZ( -2 );
		}
		else if ( player.getWalkDirection().getZ() > 0.1f )
		{
			multi.setZ( 2 );
		}
		
		if ( multi.getX() > 0 || multi.getX() < 0 || multi.getZ() > 0 || multi.getZ() < 0 )
		{
			playerLookingAt = playerLookingAt.add( multi );
			
			if ( playerLookingAt.getX() < 0f )
				playerLookingAt.setX( playerLookingAt.getX() - 1 );
			
			if ( playerLookingAt.getZ() < 0f )
				playerLookingAt.setZ( playerLookingAt.getZ() - 1 );
			
			processChunk( playerLookingAt.getX(), playerLookingAt.getY(), playerLookingAt.getZ() );
		}
		*/
	}
	
	public void processChunk( int x, int y, int z )
	{
		ChunkControl chunk = get( x, y, z );
		
		if ( chunk != null )
		{
			updateChunk( chunk );
		}
		else
		{
			ChunkControl newChunk = new ChunkControl( TerrainControl.getInstance(), x, y, z );
			
			set( x, y, z, newChunk );
			
			newChunk.makeTerrain();
			
			updateChunk( newChunk );
		}
	}
	
	public void addChunkListener( ChunkListener squareChunkListener )
	{
		chunkListeners.add( squareChunkListener );
	}
	
	public void removeChunkListener( ChunkListener squareChunkListener )
	{
		chunkListeners.remove( squareChunkListener );
	}
	
	public void updateBandages( ChunkControl chunk )
	{
		try
		{
			if ( chunk == null )
				return;
			
			Vector2f xYS[] = new Vector2f[]{new Vector2f(-1, 0),new Vector2f(0, -1),new Vector2f(1, 0),new Vector2f(0, 1)};
			
			for ( Vector2f xY: xYS )
			{
				Vector3Int cl = chunk.getLocation().add( new Vector3Int( (int) xY.getX(), 0, (int) xY.getY() ) );
				
				ChunkControl chunkB = get( cl );
				
				//.mult( TerrainControl.getSettings().getChunkSizeX() )
				
				if ( chunkB != null )
				{
					BandageControl bc = getBandAid( chunk, chunkB );
					
					if ( bc == null )
					{
						bc = getBandAid( chunkB, chunk );
	
						if ( bc == null )
						{
							BandageControl bandAid = null;
							
							if ( xY.getX() < 0 )
							{
								bandAid = new BandageControl( TerrainControl.getInstance(), chunk.getLocation().getX(), chunk.getLocation().getY(), chunk.getLocation().getZ(), 0, 1 );
							}
							else if ( xY.getY() < 0 )
							{
								bandAid = new BandageControl( TerrainControl.getInstance(), chunk.getLocation().getX(), chunk.getLocation().getY(), chunk.getLocation().getZ(), 1, 0 );
							}
							else if ( xY.getX() > 0 )
							{
								bandAid = new BandageControl( TerrainControl.getInstance(), chunkB.getLocation().getX(), chunkB.getLocation().getY(), chunkB.getLocation().getZ(), 0, 1 );
							}
							else if ( xY.getY() > 0 )
							{
								bandAid = new BandageControl( TerrainControl.getInstance(), chunkB.getLocation().getX(), chunkB.getLocation().getY(), chunkB.getLocation().getZ(), 1, 0 );
							}
							
							setBandAid( chunk, chunkB, bandAid );
							bandAid.updateMesh();
						}
						else
						{
							bc.updateMesh();
						}
					}
					else
					{
						bc.updateMesh();
					}
				}
			}
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
	}

	public void updateChunk( ChunkControl squareChunk )
	{
		if ( squareChunk.updateSpatial() )
		{
			updateBandages( squareChunk );
			
			System.out.println( "Updating chunk: " + squareChunk.getLocation() );
			
			for ( int i = 0; i < chunkListeners.size(); i++ )
			{
				ChunkListener squareTerrainListener = (ChunkListener) chunkListeners.get( i );
				squareTerrainListener.onSpatialUpdated( squareChunk );
			}
		}
	}
}

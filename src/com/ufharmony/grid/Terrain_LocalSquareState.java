package com.ufharmony.grid;

import com.ufharmony.utils.Vector3Int;

public class Terrain_LocalSquareState
{
	private ChunkControl chunk;
	private Vector3Int localSquareLocation;
	
	public Terrain_LocalSquareState(ChunkControl chunk, Vector3Int localSquareLocation)
	{
		this.chunk = chunk;
		this.localSquareLocation = localSquareLocation;
	}
	
	public ChunkControl getChunk()
	{
		return chunk;
	}
	
	public Vector3Int getLocalSquareLocation()
	{
		return localSquareLocation;
	}
	
	public UniqueSquare getSquare()
	{
		return chunk.getSquare( localSquareLocation );
	}
	
	public void setSquare( Class<? extends Square> blockClass )
	{
		chunk.setSquare( localSquareLocation, blockClass );
	}
	
	public void removeSquare()
	{
		chunk.removeSquare( localSquareLocation );
	}
}

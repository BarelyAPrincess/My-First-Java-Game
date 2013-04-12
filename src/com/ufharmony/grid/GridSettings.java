package com.ufharmony.grid;

import com.jme3.app.Application;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;

public class GridSettings
{
	private AssetManager assetManager;
	private float squareSize = 3.0F;
	private int chunkSizeX = 16;
	private int chunkSizeY = 256;
	private int chunkSizeZ = 16;
	private Material squareMaterial;
	
	public GridSettings(Application application)
	{
		assetManager = application.getAssetManager();
	}
	
	public AssetManager getAssetManager()
	{
		return assetManager;
	}
	
	public float getSquareSize()
	{
		return squareSize;
	}
	
	public void setSquareSize( float squareSize )
	{
		this.squareSize = squareSize;
	}
	
	public int getChunkSizeX()
	{
		return chunkSizeX;
	}
	
	public void setChunkSizeX( int chunkSizeX )
	{
		this.chunkSizeX = chunkSizeX;
	}
	
	public int getChunkSizeY()
	{
		return chunkSizeY;
	}
	
	public void setChunkSizeY( int chunkSizeY )
	{
		this.chunkSizeY = chunkSizeY;
	}
	
	public int getChunkSizeZ()
	{
		return chunkSizeZ;
	}
	
	public void setChunkSizeZ( int chunkSizeZ )
	{
		this.chunkSizeZ = chunkSizeZ;
	}
	
	public Material getSquareMaterial()
	{
		return squareMaterial;
	}
	
	public void setDefaultSquareMaterial( String textureFilePath )
	{
		setSquareMaterial( new Chunk_Material( assetManager, textureFilePath ) );
	}
	
	public void setSquareMaterial( Material squareMaterial )
	{
		this.squareMaterial = squareMaterial;
	}
}

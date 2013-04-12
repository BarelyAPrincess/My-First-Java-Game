package com.ufharmony.grid;

import java.io.IOException;

import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import com.ufharmony.network.BitInputStream;
import com.ufharmony.network.BitOutputStream;
import com.ufharmony.network.BitSerializable;
import com.ufharmony.utils.Vector3Int;

public class ChunkControl extends AbstractControl implements BitSerializable
{
	private TerrainControl terrain;
	private Vector3Int location = new Vector3Int();
	private Vector3Int squareLocation = new Vector3Int();
	private UniqueSquare[][][] gridLayout;
	private boolean[][][] squares_IsOnSurface;
	private Node node = new Node();
	private Geometry optimizedGeometry;
	private boolean needsMeshUpdate;
	
	public ChunkControl(TerrainControl terrain, int x, int y, int z)
	{
		this.terrain = terrain;
		location.set( x, y, z );
		squareLocation.set( location.mult( terrain.getSettings().getChunkSizeX(), terrain.getSettings().getChunkSizeY(), terrain.getSettings().getChunkSizeZ() ) );
		node.setLocalTranslation( new Vector3f( squareLocation.getX(), squareLocation.getY(), squareLocation.getZ() ).mult( terrain.getSettings().getSquareSize() ) );
		// squareTypes = new
		// byte[terrain.getSettings().getChunkSizeX()][terrain.getSettings().getChunkSizeY()][terrain.getSettings().getChunkSizeZ()];
		squares_IsOnSurface = new boolean[terrain.getSettings().getChunkSizeX()][terrain.getSettings().getChunkSizeY()][terrain.getSettings().getChunkSizeZ()];
		
		gridLayout = new UniqueSquare[terrain.getSettings().getChunkSizeX()][terrain.getSettings().getChunkSizeY()][terrain.getSettings().getChunkSizeZ()];
	}
	
	public void setSpatial( Spatial spatial )
	{
		Spatial oldSpatial = this.spatial;
		super.setSpatial( spatial );
		if ( ( spatial instanceof Node ) )
		{
			Node parentNode = (Node) spatial;
			parentNode.attachChild( node );
		}
		else if ( ( oldSpatial instanceof Node ) )
		{
			Node oldNode = (Node) oldSpatial;
			oldNode.detachChild( node );
		}
	}
	
	protected void controlUpdate( float lastTimePerFrame )
	{
	}
	
	protected void controlRender( RenderManager renderManager, ViewPort viewPort )
	{
	}
	
	public Control cloneForSpatial( Spatial spatial )
	{
		throw new UnsupportedOperationException( "Not supported yet." );
	}
	
	public UniqueSquare getNeighborSquare( Vector3Int location, Square.Face face )
	{
		return terrain.getSquare( getNeighborSquareWorldLocation( location, face ) );
	}
	
	public Vector3Int getNeighborSquareWorldLocation( Vector3Int location, Square.Face face )
	{
		Vector3Int neighborLocation = Navigator.getNeighborSquareLocalLocation( location, face );
		neighborLocation.addLocal( squareLocation );
		return neighborLocation;
	}
	
	public Square getParentSquare( Vector3Int location )
	{
		if ( isValidSquareLocation( location ) )
			return gridLayout[location.getX()][location.getY()][location.getZ()].getParent();
		
		Vector3Int worldSquareLocation = squareLocation.add( location );
		return terrain.getSquare( worldSquareLocation ).getParent();
	}
	
	public UniqueSquare getSquare( Vector3Int location )
	{
		if ( isValidSquareLocation( location ) )
			return gridLayout[location.getX()][location.getY()][location.getZ()];
		
		Vector3Int worldSquareLocation = squareLocation.add( location );
		return terrain.getSquare( worldSquareLocation );
	}
	
	public void setSquare( Vector3Int location, Class<? extends Square> theClass )
	{
		if ( isValidSquareLocation( location ) )
		{
			gridLayout[location.getX()][location.getY()][location.getZ()] = new UniqueSquare( Square.getGlobalObject( theClass ) );
			
			updateSquareState( location );
			needsMeshUpdate = true;
		}
	}
	
	public void removeSquare( Vector3Int location )
	{
		if ( isValidSquareLocation( location ) )
		{
			gridLayout[location.getX()][location.getY()][location.getZ()] = null;
			updateSquareState( location );
			needsMeshUpdate = true;
		}
	}
	
	private boolean isValidSquareLocation( Vector3Int location )
	{
		return Util.isValidIndex( gridLayout, location );
	}
	
	public boolean updateSpatial()
	{
		if ( needsMeshUpdate )
		{
			if ( optimizedGeometry == null )
			{
				optimizedGeometry = new Geometry( "" );
				optimizedGeometry.setMaterial( terrain.getSettings().getSquareMaterial() );
				optimizedGeometry.setQueueBucket( RenderQueue.Bucket.Transparent );
				node.attachChild( optimizedGeometry );
			}
			optimizedGeometry.setMesh( Chunk_MeshOptimizer.generateOptimizedMesh( this ) );
			needsMeshUpdate = false;
			return true;
		}
		return false;
	}
	
	public void updateSquareState( Vector3Int location )
	{
		updateSquareInformation( location );
		for ( int i = 0; i < Square.Face.values().length; i++ )
		{
			Vector3Int neighborLocation = getNeighborSquareWorldLocation( location, Square.Face.values()[i] );
			ChunkControl chunk = terrain.getChunk( neighborLocation );
			if ( chunk != null )
				chunk.updateSquareInformation( neighborLocation.subtract( chunk.getSquareLocation() ) );
		}
	}
	
	private void updateSquareInformation( Vector3Int location )
	{
		UniqueSquare neighborSquare_Top = terrain.getSquare( getNeighborSquareWorldLocation( location, Square.Face.Top ) );
		squares_IsOnSurface[location.getX()][location.getY()][location.getZ()] = ( neighborSquare_Top == null ? true : false );
	}
	
	public boolean isSquareOnSurface( Vector3Int location )
	{
		return squares_IsOnSurface[location.getX()][location.getY()][location.getZ()];
	}
	
	public TerrainControl getTerrain()
	{
		return terrain;
	}
	
	public Vector3Int getLocation()
	{
		return location;
	}
	
	public Vector3Int getSquareLocation()
	{
		return squareLocation;
	}
	
	public Node getNode()
	{
		return node;
	}
	
	public Geometry getOptimizedGeometry()
	{
		return optimizedGeometry;
	}
	
	// TODO: Update to be compatible with new system, if needed.
	public void write( BitOutputStream outputStream )
	{
		for ( int x = 0; x < gridLayout.length; x++ )
			for ( int y = 0; y < gridLayout[0].length; y++ )
				for ( int z = 0; z < gridLayout[0][0].length; z++ )
					;
		// outputStream.writeBits( gridLayout[x][y][z], 8 );
		System.err.print( "old write method called!" );
	}
	
	// TODO: Update to be compatible with new system, if needed.
	public void read( BitInputStream inputStream ) throws IOException
	{
		for ( int x = 0; x < gridLayout.length; x++ )
		{
			for ( int y = 0; y < gridLayout[0].length; y++ )
			{
				for ( int z = 0; z < gridLayout[0][0].length; z++ )
				{
					// gridLayout[x][y][z] = ( (byte) inputStream.readBits( 8 ) );
				}
			}
		}
		Vector3Int tmpLocation = new Vector3Int();
		for ( int x = 0; x < gridLayout.length; x++ )
		{
			for ( int y = 0; y < gridLayout[0].length; y++ )
			{
				for ( int z = 0; z < gridLayout[0][0].length; z++ )
				{
					tmpLocation.set( x, y, z );
					updateSquareInformation( tmpLocation );
				}
			}
		}
		needsMeshUpdate = true;
		
		System.err.print( "old read method called!" );
	}
	
	private Vector3Int getNeededSquareChunks( Vector3Int squaresCount )
	{
		int chunksCountX = (int) Math.ceil( squaresCount.getX() / terrain.getSettings().getChunkSizeX() );
		int chunksCountY = (int) Math.ceil( squaresCount.getY() / terrain.getSettings().getChunkSizeY() );
		int chunksCountZ = (int) Math.ceil( squaresCount.getZ() / terrain.getSettings().getChunkSizeZ() );
		return new Vector3Int( chunksCountX, chunksCountY, chunksCountZ );
	}
	
	public Vector3Int getHighestSquareAt( Vector3Int l )
	{
		for ( int y = 256; y >= 0; y-- )
		{
			Vector3Int v = new Vector3Int( l.getX(), y, l.getZ() );
			
			if ( getSquare( v ) != null )
				return v;
		}
		
		return null;
	}
}

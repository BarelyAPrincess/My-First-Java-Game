package com.ufharmony.grid;

import java.io.IOException;
import java.util.ArrayList;

import com.jme3.light.AmbientLight;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import com.ufharmony.Main;
import com.ufharmony.blocks.BlockBase;
import com.ufharmony.blocks.BlockBedrock;
import com.ufharmony.blocks.BlockDirt;
import com.ufharmony.blocks.BlockGrass;
import com.ufharmony.blocks.BlockSponge;
import com.ufharmony.blocks.BlockStone;
import com.ufharmony.network.BitInputStream;
import com.ufharmony.network.BitOutputStream;
import com.ufharmony.network.BitSerializable;
import com.ufharmony.objects.ObjectBase;
import com.ufharmony.objects.ObjectTree;
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
	
	private ArrayList<Spatial> scheduledUpdates = new ArrayList<Spatial>();
	
	// Block Changes were made.
	private boolean needsMeshUpdate = true;
	
	public ChunkControl(TerrainControl terrain, int x, int y, int z)
	{
		this.terrain = terrain;
		location.set( x, y, z );
		squareLocation.set( location.mult( terrain.getSettings().getChunkSizeX(), terrain.getSettings().getChunkSizeY(), terrain.getSettings().getChunkSizeZ() ) );
		node.setLocalTranslation( new Vector3f( squareLocation.getX(), squareLocation.getY(), squareLocation.getZ() ).mult( terrain.getSettings().getSquareSize() ) );
		squares_IsOnSurface = new boolean[terrain.getSettings().getChunkSizeX()][terrain.getSettings().getChunkSizeY()][terrain.getSettings().getChunkSizeZ()];
		
		gridLayout = new UniqueSquare[terrain.getSettings().getChunkSizeX()][terrain.getSettings().getChunkSizeY()][terrain.getSettings().getChunkSizeZ()];
	}
	
	public void makeTerrain()
	{
		Noise noise = new Noise( null, 0.02f, 16, 16 );
		noise.initialise();
		float gridMinimum = noise.getMinimum();
		float gridLargestDifference = noise.getMaximum() - gridMinimum;
		float[][] grid = noise.getGrid();
		for ( int x = 0; x < grid.length; x++ )
		{
			float[] row = grid[x];
			for ( int z = 0; z < row.length; z++ )
			{
				int squareHeight = (int) ( ( row[z] - gridMinimum ) * 100.0F / gridLargestDifference / 100.0F * 16 ) + 1;
				Vector3Int tmpLocation = new Vector3Int();
				for ( int y = 0; y < squareHeight; y++ )
				{
					tmpLocation.set( location.getX() + x, location.getY() + y, location.getZ() + z );
					setSquare( tmpLocation, BlockStone.class );
				}
			}
		}
		
		for ( int x = 0; x < terrain.getSettings().getChunkSizeX(); x++ )
		{
			for ( int z = 0; z < terrain.getSettings().getChunkSizeZ(); z++ )
			{
				// Should return anything is there is no blocks?
				Vector3Int t = getHighestSquareAt( new Vector3Int( x, 0, z ) );
				
				if ( t != null )
				{
					setSquare( t, BlockGrass.class );
					
					for ( int y = 1; y < 4; y++ )
					{
						setSquare( t.subtract( new Vector3Int( 0, y, 0 ) ), BlockDirt.class );
					}
					
					if ( Util.r.nextInt( 100 ) == 0 )
						setSquare( t.subtract( new Vector3Int( 0, Util.r.nextInt( t.getY() + 1 ), 0 ) ), BlockSponge.class );
				}
				
				setSquare( new Vector3Int( t.getX(), 0, t.getZ() ), BlockBedrock.class );
				setSquare( new Vector3Int( t.getX(), 1, t.getZ() ), BlockBedrock.class );
				setSquare( new Vector3Int( t.getX(), 2, t.getZ() ), BlockBedrock.class );
				
				if ( Util.r.nextInt( 100 ) == 0 )
					setSquare( getHighestSquareAt( new Vector3Int( x, 0, z ) ).add( 0, 1, 0 ), ObjectTree.class );
			}
		}
		
		// setSquare( getHighestSquareAt( new Vector3Int( 5, 0, 0 ) ).add( 0, 1, 0 ), ObjectChest.class );
		// setSquare( getHighestSquareAt( new Vector3Int( 0, 0, 5 ) ).add( 0, 1, 0 ), ObjectDerpy.class );
	}
	
	public void setSpatial( Spatial spatial )
	{
		try
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
		catch ( IllegalStateException e )
		{
			// This chunk has already been added to the terrainNode.
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
	
	public Square getSquareInstance( Vector3Int location )
	{
		if ( isValidSquareLocation( location ) )
			return gridLayout[location.getX()][location.getY()][location.getZ()].getInstance();
		
		Vector3Int worldSquareLocation = squareLocation.add( location );
		return terrain.getSquare( worldSquareLocation ).getInstance();
	}
	
	public ObjectBase getObjectInstance( Vector3Int location )
	{
		if ( isValidSquareLocation( location ) && gridLayout[location.getX()][location.getY()][location.getZ()] != null )
			return gridLayout[location.getX()][location.getY()][location.getZ()].getObjectInstance();
		
		Vector3Int worldSquareLocation = squareLocation.add( location );
		
		if ( terrain.getSquare( worldSquareLocation ) != null )
			return terrain.getSquare( worldSquareLocation ).getObjectInstance();
		
		return null;
	}
	
	public BlockBase getBlockInstance( Vector3Int location )
	{
		if ( isValidSquareLocation( location ) && gridLayout[location.getX()][location.getY()][location.getZ()] != null )
			return gridLayout[location.getX()][location.getY()][location.getZ()].getBlockInstance();
		
		Vector3Int worldSquareLocation = squareLocation.add( location );
		
		if ( terrain.getSquare( worldSquareLocation ) != null )
			return terrain.getSquare( worldSquareLocation ).getBlockInstance();
		
		return null;
	}
	
	public UniqueSquare getSquare( Vector3Int location )
	{
		if ( isValidSquareLocation( location ) )
			return gridLayout[location.getX()][location.getY()][location.getZ()];
		
		// TODO: Make a away for out of bound chunk locations to be better handled.
		// Vector3Int worldSquareLocation = squareLocation.add( location );
		// return terrain.getSquare( worldSquareLocation );
		
		return null;
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
			System.out.println( "Updating chunk mesh at: " + getLocation() );
			
			TerrainControl squareTerrain = getTerrain();
			Vector3Int tmpLocation = new Vector3Int();
			
			for ( int x1 = 0; x1 < squareTerrain.getSettings().getChunkSizeX(); x1++ )
			{
				for ( int y1 = 0; y1 < squareTerrain.getSettings().getChunkSizeY(); y1++ )
				{
					for ( int z1 = 0; z1 < squareTerrain.getSettings().getChunkSizeZ(); z1++ )
					{
						tmpLocation.set( x1, y1, z1 );
						
						if ( getObjectInstance( tmpLocation ) != null )
						{
							UniqueSquare us = getSquare( tmpLocation );
							
							if ( !us.active )
							{
								Vector3f objectLocation = new Vector3f( x1, y1, z1 ).add( new Vector3f( 0.5F, 0.5F, 0.5F ) ).mult( squareTerrain.getSettings().getSquareSize() );
								
								Spatial derpy = us.getObjectInstance().getModel().clone();
								
								Material mat_default = us.getMaterial();
								
								if ( mat_default == null )
									mat_default = new Material( Main.getInstance().getAssetManager(), "Common/MatDefs/Misc/ShowNormals.j3md" );
								
								derpy.setMaterial( mat_default );
								
								derpy.setLocalTranslation( objectLocation.add( us.getOffset().divide( terrain.getSettings().getSquareSize() ) ) );
								
								derpy.setLocalScale( us.getScale() / terrain.getSettings().getSquareSize() );
								
								node.attachChild( derpy );
								
								AmbientLight al = new AmbientLight();
								
								al.setColor( us.getLevelColor() );
								
								derpy.addLight( al );
								
								// RigidBodyControl phy = new RigidBodyControl( 0.5f );
								// derpy.addControl( phy );
								// Main.getBullet().getPhysicsSpace().add( phy );
								
								us.active = true;
							}
						}
					}
				}
			}
			
			updateMesh();
			
			optimizedGeometry.setMesh( Chunk_MeshOptimizer.generateOptimizedMesh( this ) );
			needsMeshUpdate = false;
			return true;
		}
		
		return false;
	}
	
	public void updateMesh()
	{
		if ( needsMeshUpdate )
		{
			if ( optimizedGeometry == null )
			{
				optimizedGeometry = new Geometry( "" );
				optimizedGeometry.setMaterial( terrain.getSettings().getSquareMaterial() );
				optimizedGeometry.setQueueBucket( RenderQueue.Bucket.Transparent );
				node.attachChild( optimizedGeometry );
				
				setSpatial( Main.getTerrainNode() );
			}
		}
	}
	
	public void updateSquareState( Vector3Int location )
	{
		updateSquareInformation( location );
		for ( int i = 0; i < Square.Face.values().length; i++ )
		{
			Vector3Int neighborLocation = getNeighborSquareWorldLocation( location, Square.Face.values()[i] );
			
			if ( terrain.isValidChunkLocation( neighborLocation ) )
			{
				ChunkControl chunk = terrain.getChunk( neighborLocation );
				if ( chunk != null )
					chunk.updateSquareInformation( neighborLocation.subtract( chunk.getSquareLocation() ) );
			}
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
			
			if ( getSquare( v ) != null && getSquare( v ).getParentClass().equals( BlockBase.class ) )
				return v;
		}
		
		return new Vector3Int( l.getX(), 1, l.getZ() );
	}
}

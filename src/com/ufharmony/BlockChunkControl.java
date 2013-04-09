package com.ufharmony;

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

public class BlockChunkControl extends AbstractControl implements BitSerializable
{
	private BlockTerrainControl terrain;
	private Vector3Int location = new Vector3Int();
	private Vector3Int blockLocation = new Vector3Int();
	private byte[][][] blockTypes;
	private boolean[][][] blocks_IsOnSurface;
	private Node node = new Node();
	private Geometry optimizedGeometry;
	private boolean needsMeshUpdate;
	
	public BlockChunkControl(BlockTerrainControl terrain, int x, int y, int z)
	{
		this.terrain = terrain;
		location.set( x, y, z );
		blockLocation.set( location.mult( terrain.getSettings().getChunkSizeX(), terrain.getSettings().getChunkSizeY(), terrain.getSettings().getChunkSizeZ() ) );
		node.setLocalTranslation( new Vector3f( blockLocation.getX(), blockLocation.getY(), blockLocation.getZ() ).mult( terrain.getSettings().getBlockSize() ) );
		blockTypes = new byte[terrain.getSettings().getChunkSizeX()][terrain.getSettings().getChunkSizeY()][terrain.getSettings().getChunkSizeZ()];
		blocks_IsOnSurface = new boolean[terrain.getSettings().getChunkSizeX()][terrain.getSettings().getChunkSizeY()][terrain.getSettings().getChunkSizeZ()];
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
	
	public BlockType getNeighborBlock( Vector3Int location, Block.Face face )
	{
		return terrain.getBlock( getNeighborBlockWorldLocation( location, face ) );
	}
	
	public Vector3Int getNeighborBlockWorldLocation( Vector3Int location, Block.Face face )
	{
		Vector3Int neighborLocation = BlockNavigator.getNeighborBlockLocalLocation( location, face );
		neighborLocation.addLocal( blockLocation );
		return neighborLocation;
	}
	
	public BlockType getBlock( Vector3Int location )
	{
		if ( isValidBlockLocation( location ) )
		{
			byte blockType = blockTypes[location.getX()][location.getY()][location.getZ()];
			return BlockManager.getType( blockType );
		}
		Vector3Int worldBlockLocation = blockLocation.add( location );
		return terrain.getBlock( worldBlockLocation );
	}
	
	public void setBlock( Vector3Int location, Class<? extends Block> blockClass )
	{
		if ( isValidBlockLocation( location ) )
		{
			BlockType blockType = BlockManager.getType( blockClass );
			blockTypes[location.getX()][location.getY()][location.getZ()] = blockType.getType();
			updateBlockState( location );
			needsMeshUpdate = true;
		}
	}
	
	public void removeBlock( Vector3Int location )
	{
		if ( isValidBlockLocation( location ) )
		{
			blockTypes[location.getX()][location.getY()][location.getZ()] = 0;
			updateBlockState( location );
			needsMeshUpdate = true;
		}
	}
	
	private boolean isValidBlockLocation( Vector3Int location )
	{
		return Util.isValidIndex( blockTypes, location );
	}
	
	public boolean updateSpatial()
	{
		if ( needsMeshUpdate )
		{
			if ( optimizedGeometry == null )
			{
				optimizedGeometry = new Geometry( "" );
				optimizedGeometry.setMaterial( terrain.getSettings().getBlockMaterial() );
				optimizedGeometry.setQueueBucket( RenderQueue.Bucket.Transparent );
				node.attachChild( optimizedGeometry );
			}
			optimizedGeometry.setMesh( BlockChunk_MeshOptimizer.generateOptimizedMesh( this ) );
			needsMeshUpdate = false;
			return true;
		}
		return false;
	}
	
	public void updateBlockState( Vector3Int location )
	{
		updateBlockInformation( location );
		for ( int i = 0; i < Block.Face.values().length; i++ )
		{
			Vector3Int neighborLocation = getNeighborBlockWorldLocation( location, Block.Face.values()[i] );
			BlockChunkControl chunk = terrain.getChunk( neighborLocation );
			if ( chunk != null )
				chunk.updateBlockInformation( neighborLocation.subtract( chunk.getBlockLocation() ) );
		}
	}
	
	private void updateBlockInformation( Vector3Int location )
	{
		BlockType neighborBlock_Top = terrain.getBlock( getNeighborBlockWorldLocation( location, Block.Face.Top ) );
		blocks_IsOnSurface[location.getX()][location.getY()][location.getZ()] = ( neighborBlock_Top == null ? true : false );
	}
	
	public boolean isBlockOnSurface( Vector3Int location )
	{
		return blocks_IsOnSurface[location.getX()][location.getY()][location.getZ()];
	}
	
	public BlockTerrainControl getTerrain()
	{
		return terrain;
	}
	
	public Vector3Int getLocation()
	{
		return location;
	}
	
	public Vector3Int getBlockLocation()
	{
		return blockLocation;
	}
	
	public Node getNode()
	{
		return node;
	}
	
	public Geometry getOptimizedGeometry()
	{
		return optimizedGeometry;
	}
	
	public void write( BitOutputStream outputStream )
	{
		for ( int x = 0; x < blockTypes.length; x++ )
			for ( int y = 0; y < blockTypes[0].length; y++ )
				for ( int z = 0; z < blockTypes[0][0].length; z++ )
					outputStream.writeBits( blockTypes[x][y][z], 8 );
	}
	
	public void read( BitInputStream inputStream ) throws IOException
	{
		for ( int x = 0; x < blockTypes.length; x++ )
		{
			for ( int y = 0; y < blockTypes[0].length; y++ )
			{
				for ( int z = 0; z < blockTypes[0][0].length; z++ )
				{
					blockTypes[x][y][z] = ( (byte) inputStream.readBits( 8 ) );
				}
			}
		}
		Vector3Int tmpLocation = new Vector3Int();
		for ( int x = 0; x < blockTypes.length; x++ )
		{
			for ( int y = 0; y < blockTypes[0].length; y++ )
			{
				for ( int z = 0; z < blockTypes[0][0].length; z++ )
				{
					tmpLocation.set( x, y, z );
					updateBlockInformation( tmpLocation );
				}
			}
		}
		needsMeshUpdate = true;
	}
	
	private Vector3Int getNeededBlockChunks( Vector3Int blocksCount )
	{
		int chunksCountX = (int) Math.ceil( blocksCount.getX() / terrain.getSettings().getChunkSizeX() );
		int chunksCountY = (int) Math.ceil( blocksCount.getY() / terrain.getSettings().getChunkSizeY() );
		int chunksCountZ = (int) Math.ceil( blocksCount.getZ() / terrain.getSettings().getChunkSizeZ() );
		return new Vector3Int( chunksCountX, chunksCountY, chunksCountZ );
	}
	
	public Vector3Int getHighestSolidBlockAt( Vector3Int l )
	{
		for ( int y = 256; y >= 0; y-- )
		{
			Vector3Int v = new Vector3Int( l.getX(), y, l.getZ() );
			
			if ( getBlock( v ) != null && !getBlock( v ).getBlock().isLeaves() )
				return v;
		}
		
		return null;
	}
	
	public Vector3Int getHighestBlockAt( Vector3Int l )
	{
		for ( int y = 256; y >= 0; y-- )
		{
			Vector3Int v = new Vector3Int( l.getX(), y, l.getZ() );
			
			if ( getBlock( v ) != null )
				return v;
		}
		
		return null;
	}
}

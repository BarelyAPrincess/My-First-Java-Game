package com.ufharmony.grid;

import java.io.IOException;
import java.util.ArrayList;

import com.jme3.bullet.collision.shapes.MeshCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.light.AmbientLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
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
import com.ufharmony.blocks.BlockBrick;
import com.ufharmony.blocks.BlockDirt;
import com.ufharmony.blocks.BlockGrass;
import com.ufharmony.blocks.BlockSponge;
import com.ufharmony.blocks.BlockStone;
import com.ufharmony.network.BitInputStream;
import com.ufharmony.network.BitOutputStream;
import com.ufharmony.network.BitSerializable;
import com.ufharmony.objects.ObjectBase;
import com.ufharmony.objects.ObjectDerpy;
import com.ufharmony.objects.ObjectTree;
import com.ufharmony.utils.Vector3Int;

public class BandageControl extends AbstractControl
{
	private TerrainControl terrain;
	private Node node = new Node();
	private Geometry optimizedGeometry;
	private Vector3Int location = new Vector3Int();
	private Vector3f psyLocation = new Vector3f();
	public Vector2f direction = new Vector2f();
	
	public BandageControl(TerrainControl terrain, int x, int y, int z, int xd, int zd)
	{
		this.terrain = terrain;
		
		location = new Vector3Int( x, y, z ).mult( TerrainControl.getSettings().getChunkSizeX(), TerrainControl.getSettings().getChunkSizeY(), TerrainControl.getSettings().getChunkSizeZ() );
		direction.set( xd, zd ).mult( TerrainControl.getSettings().getChunkSizeX() );
		
		//.mult( new Vector3f( TerrainControl.getSettings().getChunkSizeX(), TerrainControl.getSettings().getChunkSizeY(), TerrainControl.getSettings().getChunkSizeZ() ) )
		psyLocation = new Vector3f( location.getX(), location.getY(), location.getZ() ).mult( TerrainControl.getSettings().getSquareSize() ).subtract( new Vector3f( TerrainControl.getSettings().getSquareSize(), 0, TerrainControl.getSettings().getSquareSize() ) ); 
		
		//if ( xd > 0 )
			//psyLocation = psyLocation.add( new Vector3f( TerrainControl.getSettings().getSquareSize() * 2, 0, 0 ) );
		
		node.setLocalTranslation( psyLocation );
	}
	
	public Vector3Int getMaxXY()
	{
		Vector3Int maxXY = new Vector3Int( (int) direction.getX(), 0, (int) direction.getY() ).mult( TerrainControl.getSettings().getChunkSizeX(), 0, TerrainControl.getSettings().getChunkSizeZ() );
		
		if ( direction.getY() > 0 )
			maxXY = maxXY.subtract( new Vector3Int( 0, 0, (int) ( TerrainControl.getSettings().getSquareSize() ) ) );
		
		if ( direction.getX() > 0 )
			maxXY = maxXY.subtract( new Vector3Int( (int) ( TerrainControl.getSettings().getSquareSize() ), 0, 0 ) );
		
		System.out.println( "Bandage Size: " + location + " / " + maxXY + " = " + location.add( maxXY ) );
		return maxXY;
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
	
	public void updateMesh()
	{
		if ( optimizedGeometry == null )
		{
			optimizedGeometry = new Geometry( "" );
			optimizedGeometry.setMaterial( terrain.getSettings().getSquareMaterial() );
			optimizedGeometry.setQueueBucket( RenderQueue.Bucket.Transparent );
			node.attachChild( optimizedGeometry );
			
			setSpatial( Main.getTerrainNode() );
		}
		
		optimizedGeometry.setMesh( Bandage_MeshOptimizer.generateOptimizedMesh( this ) );
		
		RigidBodyControl rigidBodyControl = (RigidBodyControl) optimizedGeometry.getControl( RigidBodyControl.class );
		if ( rigidBodyControl == null )
		{
			rigidBodyControl = new RigidBodyControl( 0.0F );
			optimizedGeometry.addControl( rigidBodyControl );
			Main.getBullet().getPhysicsSpace().add( rigidBodyControl );
		}
		rigidBodyControl.setCollisionShape( new MeshCollisionShape( optimizedGeometry.getMesh() ) );
	}
	
	public Node getNode()
	{
		return node;
	}
	
	public Geometry getOptimizedGeometry()
	{
		return optimizedGeometry;
	}
	
	public TerrainControl getTerrain()
	{
		return terrain;
	}
	
	public Vector3Int getLocation()
	{
		return location;
	}
	
	public Vector3f getPsyLocation()
	{
		return psyLocation;
	}
}

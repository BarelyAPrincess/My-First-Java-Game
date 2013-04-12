package com.ufharmony;

import java.util.Random;

import menu.elements.Panel;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.TextureKey;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.MeshCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.scene.shape.Sphere.TextureMode;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture;
import com.ufharmony.blocks.BlockBase;
import com.ufharmony.blocks.BlockBedrock;
import com.ufharmony.blocks.BlockDirt;
import com.ufharmony.blocks.BlockGrass;
import com.ufharmony.blocks.BlockSponge;
import com.ufharmony.blocks.BlockStone;
import com.ufharmony.blocks.BlockWood;
import com.ufharmony.gen.surface.GenTree;
import com.ufharmony.grid.ChunkListener;
import com.ufharmony.grid.Chunk_MeshOptimizer;
import com.ufharmony.grid.GridSettings;
import com.ufharmony.grid.Navigator;
import com.ufharmony.grid.BlockProperties;
import com.ufharmony.grid.TerrainControl;
import com.ufharmony.grid.ChunkControl;
import com.ufharmony.grid.TerrainHelper;
import com.ufharmony.objects.ObjectBase;
import com.ufharmony.objects.ObjectChest;
import com.ufharmony.states.MainMenu;
import com.ufharmony.utils.Vector3Int;

import de.lessvoid.nifty.Nifty;

public class Main extends SimpleApplication implements ActionListener
{
	public static Main instance = null;
	
	private BulletAppState bulletAppState;
	private CharacterControl player;
	private Vector3f walkDirection = new Vector3f();
	private boolean left = false, right = false, up = false, down = false;
	Boolean isRunning = true;
	
	public Random r = new Random();
	
	private RigidBodyControl ball_phy;
	private static final Sphere sphere;
	
	private final Vector3Int terrainSize = new Vector3Int( 256, 128, 256 );
	private boolean[] arrowKeys = new boolean[4];
	private GridSettings cubesSettings;
	private static TerrainControl blockTerrain;
	private Node terrainNode = new Node();
	
	private GenTree tree = new GenTree();
	
	private Panel mainPanel = new Panel();
	
	static
	{
		sphere = new Sphere( 32, 32, 1.0f, true, false );
		sphere.setTextureMode( TextureMode.Projected );
	}
	
	public static Main getInstance()
	{
		return instance;
	}
	
	public static BulletAppState getBullet()
	{
		return instance.bulletAppState;
	}
	
	public static void main( String[] args )
	{
		Main app = new Main();
		app.start();
	}
	
	public Main()
	{
		super();
		
		instance = this;
		
		settings = new AppSettings( true );
		settings.setWidth( 1280 );
		settings.setHeight( 720 );
		settings.setTitle( "United Federation of Harmony - Work In Progress" );
		settings.setFrameRate( 120 );
		
		showSettings = false;
	}
	
	public void initPlayer()
	{
		player = new CharacterControl( new CapsuleCollisionShape( 1.5F, 3F ), 0.05F );
		player.setJumpSpeed( 25.0F );
		player.setFallSpeed( 30.0F );
		player.setGravity( 80.0F );
		player.setPhysicsLocation( new Vector3f( 5.0F, terrainSize.getY() + 5, 5.0F ).mult( cubesSettings.getSquareSize() ) );
		bulletAppState.getPhysicsSpace().add( player );
	}
	
	public void simpleInitApp()
	{
		setPauseOnLostFocus( true );
		
		flyCam.setEnabled( false );
		flyCam.setDragToRotate( true );
		inputManager.setCursorVisible( true );
		
		bulletAppState = new BulletAppState();
		stateManager.attach( bulletAppState );
		
		setLegal();
		initCrossHairs();
		
		BlockBase.registerBlocks();
		ObjectBase.registerObjects();
		
		TerrainHelper.initializeEnvironment( this );
		TerrainHelper.initializeWater( this );
		
		setUpKeys();
		
		Box box1 = new Box( Vector3f.ZERO, 1, 1, 1 );
		Geometry blue = new Geometry( "Box", box1 );
		Material mat1 = new Material( assetManager, "Common/MatDefs/Misc/Unshaded.j3md" );
		mat1.setTexture( "ColorMap", assetManager.loadTexture( "/Textures/b1.png" ) );
		
		blue.setMaterial( mat1 );
		blue.move( 1, 3, 1 );
		
		Box box2 = new Box( Vector3f.ZERO, 1, 1, 1 );
		Geometry red = new Geometry( "Box", box2 );
		Material mat2 = new Material( assetManager, "Common/MatDefs/Misc/Unshaded.j3md" );
		mat2.setTexture( "ColorMap", assetManager.loadTexture( "/Textures/b2.png" ) );
		
		red.setMaterial( mat2 );
		red.move( 1, -1, 1 );
		
		rootNode.attachChild( pivot );
		
		pivot.attachChild( blue );
		pivot.attachChild( red );
		
		pivot.move( 10, 30, 10 );
		pivot.rotate( .4f, .4f, 0f );
		
		RigidBodyControl test_phy = new RigidBodyControl( 0.5f );
		pivot.addControl( test_phy );
		bulletAppState.getPhysicsSpace().add( test_phy );
		
		cubesSettings = TerrainHelper.getSettings( this );
		blockTerrain = new TerrainControl( cubesSettings, new Vector3Int( 7, 1, 7 ) );
		
		blockTerrain.setSquaresFromNoise( new Vector3Int(), terrainSize, 0.05F, BlockStone.class );
		
		for ( int x = 0; x < 256; x++ )
		{
			for ( int z = 0; z < 256; z++ )
			{
				Vector3Int t = blockTerrain.getChunk( new Vector3Int( 1, 0, 1 ) ).getHighestSquareAt( new Vector3Int( x, 0, z ) );
				
				if ( t != null )
				{
					blockTerrain.setSquare( t, BlockGrass.class );
					
					for ( int y = 1; y < 4; y++ )
					{
						blockTerrain.setSquare( t.subtract( new Vector3Int( 0, y, 0 ) ), BlockDirt.class );
					}
					
					blockTerrain.setSquare( new Vector3Int( t.getX(), 0, t.getZ() ), BlockBedrock.class );
					blockTerrain.setSquare( new Vector3Int( t.getX(), 1, t.getZ() ), BlockBedrock.class );
					blockTerrain.setSquare( new Vector3Int( t.getX(), 2, t.getZ() ), BlockBedrock.class );
					
					if ( r.nextInt( 100 ) == 0 )
						blockTerrain.setSquare( t.subtract( new Vector3Int( 0, r.nextInt( t.getY() ) + 1, 0 ) ), BlockSponge.class );
				}
			}
		}
		
		//blockTerrain.setSquare( blockTerrain.getChunk( new Vector3Int( 1, 0, 1 ) ).getHighestSquareAt( new Vector3Int( 10, 0, 10 ) ), ObjectChest.class );
		
		/*
		for ( int x = 0; x < 256; x++ )
		{
			for ( int z = 0; z < 256; z++ )
			{
				Vector3Int t = blockTerrain.getChunk( new Vector3Int( 1, 0, 1 ) ).getHighestSquareAt( new Vector3Int( x, 0, z ) );
				
				if ( t != null )
				{
					if ( r.nextInt( 150 ) == 0 )
						tree.generate( r, t );
				}
			}
		}
		*/
		
		blockTerrain.addChunkListener( new ChunkListener()
		{
			public void onSpatialUpdated( ChunkControl blockChunk )
			{
				Geometry optimizedGeometry = blockChunk.getOptimizedGeometry();
				RigidBodyControl rigidBodyControl = (RigidBodyControl) optimizedGeometry.getControl( RigidBodyControl.class );
				if ( rigidBodyControl == null )
				{
					rigidBodyControl = new RigidBodyControl( 0.0F );
					optimizedGeometry.addControl( rigidBodyControl );
					bulletAppState.getPhysicsSpace().add( rigidBodyControl );
				}
				rigidBodyControl.setCollisionShape( new MeshCollisionShape( optimizedGeometry.getMesh() ) );
			}
		} );
		
		terrainNode.addControl( blockTerrain );
		terrainNode.setShadowMode( RenderQueue.ShadowMode.CastAndReceive );
		rootNode.attachChild( terrainNode );
		
		initPlayer();
		cam.lookAtDirection( new Vector3f( 1.0F, 0.0F, 1.0F ), Vector3f.UNIT_Y );
	}
	
	private Nifty nifty;
	
	public void bindEntity( Spatial o )
	{
		rootNode.attachChild( o );
		bulletAppState.getPhysicsSpace().add( o );
	}
	
	private void setLegal()
	{
		guiNode.detachAllChildren();
		guiFont = assetManager.loadFont( "Interface/Fonts/Default.fnt" );
		BitmapText legal = new BitmapText( guiFont, false );
		legal.setSize( guiFont.getCharSet().getRenderedSize() );
		legal.setText( "Copyright 2013 Apple Bloom Company - Not Final Product - DO NOT REDISTRIBUTE" );
		legal.setLocalTranslation( 300, legal.getLineHeight(), 0 );
		guiNode.attachChild( legal );
	}
	
	protected void initCrossHairs()
	{
		guiFont = assetManager.loadFont( "Interface/Fonts/Default.fnt" );
		BitmapText ch = new BitmapText( guiFont, false );
		ch.setSize( guiFont.getCharSet().getRenderedSize() * 2 );
		ch.setText( "+" );
		ch.setLocalTranslation( settings.getWidth() / 2 - guiFont.getCharSet().getRenderedSize() / 3 * 2, settings.getHeight() / 2 + ch.getLineHeight() / 2, 0 );
		guiNode.attachChild( ch );
	}
	
	private void setUpLight()
	{
		AmbientLight al = new AmbientLight();
		al.setColor( ColorRGBA.White.mult( 1.3f ) );
		rootNode.addLight( al );
		
		DirectionalLight dl = new DirectionalLight();
		dl.setColor( ColorRGBA.White );
		dl.setDirection( new Vector3f( 2.8f, -2.8f, -2.8f ).normalizeLocal() );
		rootNode.addLight( dl );
	}
	
	private void setUpKeys()
	{
		inputManager.addMapping( "Left", new KeyTrigger( KeyInput.KEY_A ) );
		inputManager.addMapping( "Right", new KeyTrigger( KeyInput.KEY_D ) );
		inputManager.addMapping( "Up", new KeyTrigger( KeyInput.KEY_W ) );
		inputManager.addMapping( "Down", new KeyTrigger( KeyInput.KEY_S ) );
		inputManager.addMapping( "Jump", new KeyTrigger( KeyInput.KEY_SPACE ) );
		inputManager.addListener( this, "Left" );
		inputManager.addListener( this, "Right" );
		inputManager.addListener( this, "Up" );
		inputManager.addListener( this, "Down" );
		inputManager.addListener( this, "Jump" );
		
		inputManager.addMapping( "F5", new KeyTrigger( KeyInput.KEY_F5 ) );
		inputManager.addListener( this, "F5" );
		
		inputManager.addMapping( "Pause", new KeyTrigger( KeyInput.KEY_P ) );
		inputManager.addListener( this, "Pause" );
		
		inputManager.addMapping( "break", new MouseButtonTrigger( MouseInput.BUTTON_LEFT ) );
		inputManager.addListener( this, "break" );
		
		inputManager.addMapping( "place", new MouseButtonTrigger( MouseInput.BUTTON_RIGHT ) );
		inputManager.addListener( this, "place" );
		
		inputManager.addMapping( "Derp", new KeyTrigger( KeyInput.KEY_LCONTROL ) );
		inputManager.addListener( this, "Derp" );
		
		inputManager.addMapping( "MainMenu", new KeyTrigger( KeyInput.KEY_Q ) );
		inputManager.addListener( this, "MainMenu" );
		
		// inputManager.deleteMapping( "FLYCAM_ZoomIn" );
		// inputManager.deleteMapping( "FLYCAM_ZoomOut" );
		
		inputManager.addMapping( "inv", new MouseAxisTrigger( MouseInput.AXIS_WHEEL, true ) );
		inputManager.addListener( this, "inv" );
	}
	
	public static TerrainControl getWorld()
	{
		return blockTerrain;
	}
	
	public void onAction( String binding, boolean value, float tpf )
	{
		try
		{
			if ( binding.equals( "Left" ) )
			{
				left = value;
			}
			else if ( binding.equals( "Right" ) )
			{
				right = value;
			}
			else if ( binding.equals( "Up" ) )
			{
				up = value;
			}
			else if ( binding.equals( "Down" ) )
			{
				down = value;
			}
			else if ( binding.equals( "Jump" ) )
			{
				player.jump();
			}
			else if ( binding.equals( "F5" ) )
			{	
				
			}
			else if ( binding.equals( "Pause" ) )
			{
				isRunning = !isRunning;
			}
			else if ( binding.equals( "MainMenu" ) && !value )
			{
				System.out.println( "Trying to show main menu!" );
				
				stateManager.attach( new MainMenu( this ) );
				stateManager.getState( MainMenu.class ).setEnabled( true );
			}
			else if ( binding.equals( "inv" ) && !value )
			{
				System.out.println( "Scroll Wheel" );
			}
			else if ( binding.equals( "break" ) && !value )
			{
				Vector3Int blockLocation = getCurrentPointedBlockLocation( false );
				if ( ( blockLocation != null ) && ( blockLocation.getY() > 0 ) )
				{
					// blockTerrain.removeBlock( blockLocation );
					
					TerrainHelper.doExplosion( blockLocation );
				}
			}
			else if ( binding.equals( "place" ) && !value )
			{
				Vector3Int blockLocation = getCurrentPointedBlockLocation( true );
				if ( blockLocation != null )
					blockTerrain.setSquare( blockLocation, BlockWood.class );
			}
			else if ( binding.equals( "Derp" ) && !value )
			{
				Vector3Int blockLocation = getCurrentPointedBlockLocation( false );
				if ( ( blockLocation != null ) )
				{
					try
					{
						BlockProperties bp = Chunk_MeshOptimizer.squareProperties[blockLocation.getX()][blockLocation.getY()][blockLocation.getZ()];
						System.out.println( bp.side_top_left + " " + bp.side_top_right + " " + bp.side_top_front + " " + bp.side_top_back );
					}
					catch ( Exception e )
					{	
						
					}
				}
				
				// makeCannonBall();
				
				/*
				 * Spatial derpy = assetManager.loadModel( "Model/Derpy/derpy_all-in-one.OBJ" ); Material mat_default = new
				 * Material( assetManager, "Common/MatDefs/Misc/ShowNormals.j3md" ); derpy.setMaterial( mat_default );
				 * rootNode.attachChild( derpy );
				 * 
				 * derpy.setLocalTranslation( cam.getLocation() );
				 * 
				 * derpy.setLocalScale( 0.5f );
				 * 
				 * RigidBodyControl derpy_phy = new RigidBodyControl( 2f ); derpy.addControl( derpy_phy );
				 * bulletAppState.getPhysicsSpace().add( derpy_phy );
				 * 
				 * derpy_phy.setLinearVelocity( cam.getDirection().mult( 25 ) );
				 */
			}
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
	}
	
	private Vector3Int getCurrentPointedBlockLocation( boolean getNeighborLocation )
	{
		CollisionResults results = getRayCastingResults( terrainNode );
		if ( results.size() > 0 )
		{
			Vector3f collisionContactPoint = results.getClosestCollision().getContactPoint();
			return Navigator.getPointedSquareLocation( blockTerrain, collisionContactPoint, getNeighborLocation );
		}
		return null;
	}
	
	private CollisionResults getRayCastingResults( Node node )
	{
		Vector3f origin = cam.getWorldCoordinates( new Vector2f( settings.getWidth() / 2, settings.getHeight() / 2 ), 0.0F );
		Vector3f direction = cam.getWorldCoordinates( new Vector2f( settings.getWidth() / 2, settings.getHeight() / 2 ), 0.3F );
		direction.subtractLocal( origin ).normalizeLocal();
		Ray ray = new Ray( origin, direction );
		CollisionResults results = new CollisionResults();
		node.collideWith( ray, results );
		return results;
	}
	
	public Vector3f getTargetAtCrossHairs()
	{
		CollisionResults results = new CollisionResults();
		Ray ray = new Ray( cam.getLocation(), cam.getDirection() );
		rootNode.collideWith( ray, results );
		
		System.out.println( "----- Collisions? " + results.size() + "-----" );
		for ( int i = 0; i < results.size(); i++ )
		{
			float dist = results.getCollision( i ).getDistance();
			Vector3f pt = results.getCollision( i ).getContactPoint();
			String hit = results.getCollision( i ).getGeometry().getName();
			System.out.println( "* Collision #" + i );
			System.out.println( "  You shot " + hit + " at " + pt + ", " + dist + " wu away." );
			
			// CollisionResult closest = results.getClosestCollision();
		}
		
		if ( results.size() < 1 )
			return null;
		
		return results.getClosestCollision().getContactPoint();
	}
	
	public void makeCannonBall()
	{
		Material b1 = new Material( assetManager, "Common/MatDefs/Misc/Unshaded.j3md" );
		TextureKey key2 = new TextureKey( "/Textures/b1.png" );
		key2.setGenerateMips( true );
		Texture tex2 = assetManager.loadTexture( key2 );
		b1.setTexture( "ColorMap", tex2 );
		
		Geometry ball_geo = new Geometry( "cannon ball", sphere );
		ball_geo.setMaterial( b1 );
		rootNode.attachChild( ball_geo );
		ball_geo.setLocalTranslation( cam.getLocation() );
		ball_phy = new RigidBodyControl( 1f );
		ball_geo.addControl( ball_phy );
		bulletAppState.getPhysicsSpace().add( ball_phy );
		ball_phy.setLinearVelocity( cam.getDirection().mult( 25 ) );
	}
	
	public void simpleUpdate( float tpf )
	{
		float playerMoveSpeed = 16f * tpf;
		Vector3f camDir = cam.getDirection().mult( playerMoveSpeed );
		Vector3f camLeft = cam.getLeft().mult( playerMoveSpeed );
		walkDirection.set( 0, 0, 0 );
		if ( left )
		{
			walkDirection.addLocal( camLeft );
		}
		if ( right )
		{
			walkDirection.addLocal( camLeft.negate() );
		}
		if ( up )
		{
			walkDirection.addLocal( camDir );
		}
		if ( down )
		{
			walkDirection.addLocal( camDir.negate() );
		}
		player.setWalkDirection( walkDirection );
		cam.setLocation( player.getPhysicsLocation() );
	}
	
	Node pivot = new Node( "pivot" );
	
	public void simpleRender( RenderManager rm )
	{
		
	}
}

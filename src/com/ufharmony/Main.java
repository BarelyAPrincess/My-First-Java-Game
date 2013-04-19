package com.ufharmony;

import java.util.Random;

import com.jme3.app.Application;
import com.jme3.app.DebugKeysAppState;
import com.jme3.app.state.AppState;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.MeshCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapFont;
import com.jme3.input.controls.ActionListener;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.system.AppSettings;
import com.ufharmony.blocks.BlockBase;
import com.ufharmony.blocks.BlockWood;
import com.ufharmony.grid.BlockProperties;
import com.ufharmony.grid.ChunkControl;
import com.ufharmony.grid.ChunkListener;
import com.ufharmony.grid.Chunk_MeshOptimizer;
import com.ufharmony.grid.GridSettings;
import com.ufharmony.grid.Navigator;
import com.ufharmony.grid.TerrainControl;
import com.ufharmony.grid.TerrainHelper;
import com.ufharmony.objects.ObjectBase;
import com.ufharmony.objects.ObjectLamp;
import com.ufharmony.states.FlyCam;
import com.ufharmony.states.MainMenu;
import com.ufharmony.utils.Vector3Int;

public class Main extends Application implements ActionListener
{
	public static final String INPUT_MAPPING_CAMERA_POS = DebugKeysAppState.INPUT_MAPPING_CAMERA_POS;
	public static final String INPUT_MAPPING_MEMORY = DebugKeysAppState.INPUT_MAPPING_MEMORY;
	public static final String INPUT_MAPPING_HIDE_STATS = "SIMPLEAPP_HideStats";
	
	public static Node rootNode = new Node( "Root Node" );
	public static Node guiNode = new Node( "Gui Node" );
	public static CameraBase flyCam;
	public static BitmapFont guiFont;
   public static AppSettings settings;
	public Extras extras = new Extras( this );
	
	public static Main instance = null;
	
	private BulletAppState bulletAppState;
	private static CharacterControl player;
	private Vector3f walkDirection = new Vector3f();
	private boolean left = false, right = false, up = false, down = false;
	Boolean isRunning = true;
	
	public Random r = new Random();
	
	private boolean[] arrowKeys = new boolean[4];
	private GridSettings cubesSettings;
	private static TerrainControl blockTerrain;
	private static Node terrainNode = new Node();
	
	public static void main( String[] args )
	{
		Main app = new Main( new FlyCam(), new DebugKeysAppState() );
		app.start();
	}
	
	public Main(AppState... initialStates)
	{
		super();
		
		if ( initialStates != null )
		{
			for ( AppState a : initialStates )
			{
				if ( a != null )
				{
					stateManager.attach( a );
				}
			}
		}
		
		instance = this;
		
		settings = new AppSettings( true );
		settings.setWidth( 1280 );
		settings.setHeight( 720 );
		settings.setDepthBits( 24 );
		settings.setTitle( "United Federation of Harmony - Work In Progress" );
		settings.setFrameRate( 1200 );
	}
	
	public static CharacterControl getPlayer()
	{
		return player;
	}
	
	public static Main getInstance()
	{
		return instance;
	}
	
	public static BulletAppState getBullet()
	{
		return instance.bulletAppState;
	}
	
	protected BitmapFont loadGuiFont()
	{
		return assetManager.loadFont( "Interface/Fonts/Default.fnt" );
	}
	
	public void initPlayer()
	{
		player = new CharacterControl( new CapsuleCollisionShape( 1.5F, 3F ), 0.05F );
		player.setJumpSpeed( 25.0F );
		player.setFallSpeed( 30.0F );
		player.setGravity( 80.0F );
		player.setPhysicsLocation( new Vector3f( 5.0F, 64.0F, 5.0F ).mult( cubesSettings.getSquareSize() ) );
		bulletAppState.getPhysicsSpace().add( player );
	}
	
	public void releaseCursor()
	{
		flyCam.setEnabled( false );
		flyCam.setDragToRotate( true );
		inputManager.setCursorVisible( true );
	}
	
	@Override
	public void initialize()
	{
		super.initialize();
		
		guiFont = loadGuiFont();
		
		guiNode.setQueueBucket( Bucket.Gui );
		guiNode.setCullHint( CullHint.Never );
		viewPort.attachScene( rootNode );
		guiViewPort.attachScene( guiNode );
		
		if ( inputManager != null )
		{
			if ( stateManager.getState( FlyCam.class ) != null )
			{
				flyCam = new CameraBase( cam );
				flyCam.setMoveSpeed( 1f );
				stateManager.getState( FlyCam.class ).setCamera( flyCam );
			}
		}
		
		setPauseOnLostFocus( true );
		
		/*
		 * flyCam.setEnabled( false ); flyCam.setDragToRotate( true ); inputManager.setCursorVisible( true );
		 */
		
		bulletAppState = new BulletAppState();
		stateManager.attach( bulletAppState );
		
		extras.setLegal();
		extras.initCrossHairs();
		
		BlockBase.registerBlocks();
		ObjectBase.registerObjects();
		
		TerrainHelper.initializeEnvironment( this );
		
		extras.setUpKeys();
		
		cubesSettings = TerrainHelper.getSettings( this );
		blockTerrain = new TerrainControl( cubesSettings );
		
		blockTerrain.getChunkManager().addChunkListener( new ChunkListener()
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
		cam.lookAtDirection( new Vector3f( 10.0F, -10.0F, 10.0F ), Vector3f.UNIT_Y );
		
		blockTerrain.setSquare( new Vector3Int( 3, 4, 3 ), ObjectLamp.class );
	}
	
	public void bindEntity( Spatial o )
	{
		rootNode.attachChild( o );
		bulletAppState.getPhysicsSpace().add( o );
	}
	
	public static TerrainControl getWorld()
	{
		return blockTerrain;
	}
	
	public void onAction( String binding, boolean value, float tpf )
	{
		try
		{
			if ( binding.equals( "MainMenu2" ) )
			{
				stop();
			}
			else if ( binding.equals( "Left" ) )
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
			else if ( !value )
			{
				if ( binding.equals( "F5" ) )
				{
					
				}
				else if ( binding.equals( "Pause" ) )
				{
					isRunning = !isRunning;
				}
				else if ( binding.equals( "MainMenu" ) )
				{
					System.out.println( "Trying to show main menu!" );
					
					stateManager.attach( new MainMenu( this ) );
					stateManager.getState( MainMenu.class ).setEnabled( true );
				}
				else if ( binding.equals( "inv" ) )
				{
					System.out.println( "Scroll Wheel" );
				}
				else if ( binding.equals( "break" ) )
				{
					Vector3Int blockLocation = getCurrentPointedBlockLocation( false );
					if ( ( blockLocation != null ) && ( blockLocation.getY() > 0 ) )
					{
						// blockTerrain.removeBlock( blockLocation );
						
						TerrainHelper.doExplosion( blockLocation );
					}
				}
				else if ( binding.equals( "place" ) )
				{
					Vector3Int blockLocation = getCurrentPointedBlockLocation( true );
					if ( blockLocation != null )
						blockTerrain.setSquare( blockLocation, BlockWood.class );
				}
				else if ( binding.equals( "Derp" ) )
				{
					for ( Spatial s : terrainNode.getChildren() )
					{
						System.out.println( "Chunk loaded at " + s.getLocalTranslation().divide( TerrainControl.getSettings().getSquareSize() ) );
					}
					
					System.out.println( "There are " + terrainNode.getChildren().size() + " nodes attached to the terrain." );
					
					/*
					Vector3f playerLocation = player.getPhysicsLocation().divide( TerrainControl.getSettings().getSquareSize() ).divide( new Vector3f( TerrainControl.getSettings().getChunkSizeX(), TerrainControl.getSettings().getChunkSizeY(), TerrainControl.getSettings().getChunkSizeZ() ) );
					
					ChunkControl c = blockTerrain.getChunkManager().get( (int) playerLocation.getX(), (int) playerLocation.getY(), (int) playerLocation.getZ() );
					
					c.setSpatial( terrainNode );
					*/
					
					/*
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
					*/
					
					// makeCannonBall();
				}
			}
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
	}
	
	public static Node getTerrainNode ()
	{
		return terrainNode;
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
	
	public void simpleRender( RenderManager rm )
	{
	}
	
	@Override
	public void start()
	{
		boolean loadSettings = false;
		
		if ( settings == null )
		{
			setSettings( new AppSettings( true ) );
			loadSettings = true;
		}
		
		setSettings( settings );
		super.start();
	}
	
	public CameraBase getFlyByCamera()
	{
		return flyCam;
	}
	
	public static Node getGuiNode()
	{
		return guiNode;
	}
	
	public static Node getRootNode()
	{
		return rootNode;
	}
	
	public void updateEasy( float tpf )
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
		
		Vector3Int blockLocation = getCurrentPointedBlockLocation( false );
		if ( blockLocation != null )
			if ( blockTerrain.getSquare( blockLocation ) != null )
				blockTerrain.getSquare( blockLocation ).makeGlow();
	}
	
	@Override
	public void update()
	{
		super.update();
		
		if ( speed == 0 || paused )
		{
			return;
		}
		
		float tpf = timer.getTimePerFrame() * speed;
		
		stateManager.update( tpf );
		
		updateEasy( tpf );
		
		rootNode.updateLogicalState( tpf );
		guiNode.updateLogicalState( tpf );
		
		rootNode.updateGeometricState();
		guiNode.updateGeometricState();
		
		stateManager.render( renderManager );
		renderManager.render( tpf, context.isRenderable() );
		simpleRender( renderManager );
		stateManager.postRender();
	}
}

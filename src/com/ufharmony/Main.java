package com.ufharmony;

import java.util.Random;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.LoopMode;
import com.jme3.animation.SkeletonControl;
import com.jme3.app.Application;
import com.jme3.app.DebugKeysAppState;
import com.jme3.app.state.AppState;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.MeshCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.KinematicRagdollControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.joints.HingeJoint;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapFont;
import com.jme3.input.ChaseCamera;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.debug.Arrow;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture;
import com.ufharmony.blocks.BlockBase;
import com.ufharmony.blocks.BlockWood;
import com.ufharmony.grid.ChunkControl;
import com.ufharmony.grid.ChunkListener;
import com.ufharmony.grid.GridSettings;
import com.ufharmony.grid.Navigator;
import com.ufharmony.grid.TerrainControl;
import com.ufharmony.grid.TerrainHelper;
import com.ufharmony.objects.ObjectBase;
import com.ufharmony.states.FlyCam;
import com.ufharmony.states.MainMenu;
import com.ufharmony.utils.Vector3Int;

public class Main extends Application implements ActionListener, AnalogListener, AnimEventListener
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
	private static Node playerModel = new Node("Player");
	private static CharacterControl player;
	private static ChaseCamera chaseCam;
	private float playerSpeed = 0;
	private AnimChannel playerChannel;
   private AnimControl playerControl;
	
	private Vector3f walkDirection = new Vector3f();
	private boolean left = false, right = false, up = false, down = false;
	Boolean isRunning = true;
	
	public Random r = new Random();
	
	private boolean[] arrowKeys = new boolean[4];
	private GridSettings cubesSettings;
	private static TerrainControl blockTerrain;
	private static Node terrainNode = new Node();
	
	public static final int IDLE = 0;
	public static final int TURN180 = 1;
	public static final int TURN105 = 2;
	public static final int TURN45 = 3;
	public static final int TROT = 4;
	public static final int RUN = 5;
	public static final int SKID = 6;
	public static final int SKIDSTAND = 7;
	public static final int JUMP = 8;
	public static final int FLY = 10;
	public static final int ROLL = 11;
	public static final int SWIM = 12;
	public static final int FASTTURN = 13;
	public static final int FASTTURN105 = 14;
	public static final int EXTERNAL = 15;
	public static final int FLINCH = 16;
	
	private int playerState = IDLE;
	
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
	
	Spatial body;
	Spatial tail;
	
	public void initPlayer()
	{
		flyCam.setEnabled(false);
		
		body = assetManager.loadModel( "Model/Test/derpy.mesh.xml" );
		
		tail = assetManager.loadModel( "Model/Test/derpyTail.mesh.xml" );
      
		Material mat = new Material( assetManager, "Common/MatDefs/Light/Lighting.j3md" );
		
		Texture tex = assetManager.loadTexture( "Model/Test/rainbowDash.png" );
		
		mat.setTexture( "DiffuseMap", tex );
		mat.setTexture( "NormalMap", tex );
		mat.setBoolean( "UseMaterialColors", true );
		mat.setColor( "Specular", ColorRGBA.White );
		mat.setColor( "Diffuse", ColorRGBA.White );
		mat.setFloat( "Shininess", 5f );
		
		body.setMaterial( mat );
		body.setLocalRotation( new Quaternion().fromAngles( new float[]{ 0 - FastMath.PI/2, 0, 0 } ) );
		body.updateModelBound();
		
		//body.center();
		
		Material matt = new Material( assetManager, "Common/MatDefs/Light/Lighting.j3md" );
		
		Texture text = assetManager.loadTexture( "Model/Test/derpyHair.png" );
		
		matt.setTexture( "DiffuseMap", text );
		matt.setTexture( "NormalMap", text );
		matt.setBoolean( "UseMaterialColors", true );
		matt.setColor( "Specular", ColorRGBA.White );
		matt.setColor( "Diffuse", ColorRGBA.White );
		matt.setFloat( "Shininess", 5f );
		
		tail.setLocalRotation( new Quaternion().fromAngles( new float[]{ 0 - FastMath.PI/2, 0, 0 } ) );
		
		AnimControl control = tail.getControl(AnimControl.class);
		
		/*
		KinematicRagdollControl ragdoll = new KinematicRagdollControl(0.5f);
		ragdoll.addBoneName("Tail0");
		ragdoll.addBoneName("Tail1");
		ragdoll.addBoneName("Tail2");
		ragdoll.addBoneName("Tail3");
		ragdoll.addBoneName("Tail4");
      //ragdoll.addCollisionListener(this);
      tail.addControl(ragdoll);
      */
		
      //bulletAppState.getPhysicsSpace().add( ragdoll );
		
		playerControl = body.getControl(AnimControl.class);
      playerControl.addListener(this);
      playerChannel = playerControl.createChannel();
		
      playerChannel.setAnim("Stand");
      playerChannel.setLoopMode( LoopMode.Loop );
		
		//body.setLocalTranslation( 0, -11.5f, 0 );
      
      tail.setLocalRotation( new Quaternion().fromAngleAxis( FastMath.HALF_PI, new Vector3f( 1, 0, 0 ) ) );
      
		playerModel.attachChild( body );
		//playerModel.attachChild( tail );
		
		player = new CharacterControl( new CapsuleCollisionShape( 1.5f, 3f ), 0.5f );
		player.setJumpSpeed( 30.0F );
		player.setFallSpeed(30.0F);
		player.setGravity(80.0F);
      
      playerModel.addControl(player);
      
      SkeletonControl sc = body.getControl( SkeletonControl.class );
      
      Node n = sc.getAttachmentsNode( "Hip.001.R" );
      
      n.attachChild( tail );
      
		bulletAppState.getPhysicsSpace().add( player );
      
      player.setPhysicsLocation( new Vector3f( 7.0F, 32.0F, 7.0F ).mult( cubesSettings.getSquareSize() ) );
      playerModel.setLocalRotation( new Quaternion().fromAngles( new float[]{ 0 - FastMath.PI/2, 0, 0 } ) );
		
		rootNode.attachChild( playerModel );
		
		chaseCam = new ChaseCamera(cam, playerModel, inputManager);
		chaseCam.setSmoothMotion(true);
		
		chaseCam.setTrailingEnabled(false);
	}
	
	public void releaseCursor()
	{
		flyCam.setEnabled( false );
		flyCam.setDragToRotate( true );
		inputManager.setCursorVisible( true );
	}
	
	CameraNode camNode;
	
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
		
		// releaseCursor();
		
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
		//cam.lookAtDirection( new Vector3f( 10.0F, -10.0F, 10.0F ), Vector3f.UNIT_Y );
		
		//blockTerrain.setSquare( new Vector3Int( 3, 4, 3 ), ObjectLamp.class );
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
	
	public void updateEasy( float tpf )
	{
		/*
		Vector3f rotor = player.getViewDirection();
		
		Float n = (float) Math.atan2(rotor.getX(), rotor.getZ());
		Float e = (float) Math.atan2(walkDirection.getX(), walkDirection.getZ());
		Float angle = e - n;
		angle = (float) ( angle * 360 / (Math.PI*2) );
		
		System.out.println( "Rotation: " + angle );
		*/
		
		if ( !player.getViewDirection().equals( walkDirection.setY( 0f ) ) )
			player.setViewDirection( walkDirection.setY( 0f ) );
		
		player.setWalkDirection( walkDirection.setY( 0f ).mult( playerSpeed ) );
		playerChannel.setSpeed( playerSpeed );
		
		if ( !up && !down && !left && !right )
		{
			if ( playerSpeed > 0 )
			{
				playerSpeed = playerSpeed - 0.02f;
			}
			else
			{
				playerSpeed = 0f;
				updatePlayerState( IDLE );
			}
		}
	}
	
	String lastDirection = "";
	float turnAmount = 0;
	public void onAnalog( String name, float value, float tpf )
	{
		float playerMoveSpeed = 5f * tpf;
		
		Vector3f camDir = cam.getDirection().mult( playerMoveSpeed );
		Vector3f camLeft = cam.getLeft().mult( playerMoveSpeed );
		
		Vector3f tempWalkDirection = new Vector3f();
		if ( name.equals( "Left" ) )
		{
			if ( walkDirection.equals( tempWalkDirection.clone().addLocal( camDir ) ) )
			{
				if ( turnAmount < 1 )
					turnAmount = turnAmount + 0.02f;
				
				tempWalkDirection.addLocal( camDir );
				Quaternion q = new Quaternion().fromAngleAxis( FastMath.PI * turnAmount, new Vector3f( 0, 1, 0 ) );
				tempWalkDirection.addLocal( q.mult( camDir ) );
			}
			else
			{
				turnAmount = 0;
				tempWalkDirection.addLocal( camLeft );
			}
		}
		else if ( name.equals( "Right" ) )
		{
			tempWalkDirection.addLocal( camLeft.negate() );
		}
		else if ( name.equals( "Up" ) )
		{
			tempWalkDirection.addLocal( camDir );
		}
		else if ( name.equals( "Down" ) )
		{
			tempWalkDirection.addLocal( camDir.negate() );
		}
		else
		{
			turnAmount = 0;
			return;
		}
		
		if ( playerSpeed < 1 )
			playerSpeed = 1;
		
		if ( playerSpeed < 3 )
			playerSpeed = playerSpeed + 0.02f;
		
		if ( !lastDirection.equals( name ) )
		{
			playerSpeed = 1;
			lastDirection = name;
		}
		
		walkDirection = tempWalkDirection;
		player.setWalkDirection( walkDirection );
	}
	
	public void updatePlayerState ( int state )
	{
		if ( state == TROT )
		{
			//System.out.println( "Setting player animation to: Trot" );
			if ( playerChannel.getAnimationName() != "Trot" )
				playerChannel.setAnim("Trot");
		}
		else if ( state == RUN )
		{
			//System.out.println( "Setting player animation to: Trot" );
			if ( playerChannel.getAnimationName() != "Canter" )
				playerChannel.setAnim("Canter");
		}
		else if ( state == JUMP )
		{
			if ( playerState == TROT || playerState == RUN )
			{
				if ( playerChannel.getAnimationName() != "Canter" )
				playerChannel.setAnim("Canter");
			}
			else
			{
				if ( playerChannel.getAnimationName() != "Trot" )
				playerChannel.setAnim("Trot");
			}
		}
		else
		{
			//System.out.println( "Setting player animation to: Standing" );
			if ( playerChannel.getAnimationName() != "Stand" )
				playerChannel.setAnim("Stand");
		}
		
		playerState = state;
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
				if ( value )
					updatePlayerState( TROT );
			}
			else if ( binding.equals( "Right" ) )
			{
				right = value;
				if ( value )
					updatePlayerState( TROT );
			}
			else if ( binding.equals( "Up" ) )
			{
				up = value;
				if ( value )
					updatePlayerState( TROT );
			}
			else if ( binding.equals( "Down" ) )
			{
				down = value;
				if ( value )
					updatePlayerState( TROT );
			}
			else if ( binding.equals( "Jump" ) )
			{
				player.jump();
				if ( value )
					updatePlayerState( JUMP );
			}
			else
			{
				if ( !value )
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
					else if ( binding.equals( "break1" ) )
					{
						Vector3Int blockLocation = getCurrentPointedBlockLocation( false );
						if ( ( blockLocation != null ) && ( blockLocation.getY() > 0 ) )
						{
							// blockTerrain.removeBlock( blockLocation );
							
							TerrainHelper.doExplosion( blockLocation );
						}
					}
					else if ( binding.equals( "place1" ) )
					{
						Vector3Int blockLocation = getCurrentPointedBlockLocation( true );
						if ( blockLocation != null )
							blockTerrain.setSquare( blockLocation, BlockWood.class );
					}
					else if ( binding.equals( "Respawn" ) )
					{
						player.setPhysicsLocation( new Vector3f( 5.0F, 32.0F, 5.0F ).mult( cubesSettings.getSquareSize() ) );
					}
					else if ( binding.equals( "Derp" ) )
					{
						ChunkControl c = blockTerrain.getChunk( new Vector3Int( player.getPhysicsLocation().divide( cubesSettings.getSquareSize() ) ) );
						
						if ( c != null )
							c.makeTerrain();
						
						/*
						 * for ( Spatial s : terrainNode.getChildren() ) { System.out.println( "Chunk loaded at " +
						 * s.getLocalTranslation().divide( TerrainControl.getSettings().getSquareSize() ) ); }
						 * 
						 * System.out.println( "There are " + terrainNode.getChildren().size() +
						 * " nodes attached to the terrain." );
						 */
						
						/*
						 * Vector3f playerLocation = player.getPhysicsLocation().divide(
						 * TerrainControl.getSettings().getSquareSize() ).divide( new Vector3f(
						 * TerrainControl.getSettings().getChunkSizeX(), TerrainControl.getSettings().getChunkSizeY(),
						 * TerrainControl.getSettings().getChunkSizeZ() ) );
						 * 
						 * ChunkControl c = blockTerrain.getChunkManager().get( (int) playerLocation.getX(), (int)
						 * playerLocation.getY(), (int) playerLocation.getZ() );
						 * 
						 * c.setSpatial( terrainNode );
						 */
						
						/*
						 * Vector3Int blockLocation = getCurrentPointedBlockLocation( false ); if ( ( blockLocation != null )
						 * ) { try { BlockProperties bp =
						 * Chunk_MeshOptimizer.squareProperties[blockLocation.getX()][blockLocation.getY
						 * ()][blockLocation.getZ()]; System.out.println( bp.side_top_left + " " + bp.side_top_right + " " +
						 * bp.side_top_front + " " + bp.side_top_back ); } catch ( Exception e ) {
						 * 
						 * } }
						 */
						
						// makeCannonBall();
					}
				}
			}
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
	}
	
	public static Node getTerrainNode()
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

	@Override
	public void onAnimChange( AnimControl arg0, AnimChannel arg1, String arg2 )
	{
		
	}

	@Override
	public void onAnimCycleDone( AnimControl arg0, AnimChannel arg1, String arg2 )
	{
		
	}
}

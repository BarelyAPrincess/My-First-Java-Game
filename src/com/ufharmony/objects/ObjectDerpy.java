package com.ufharmony.objects;

import java.util.Collection;
import java.util.HashMap;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.Animation;
import com.jme3.animation.LoopMode;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.VertexBuffer.Format;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.VertexBuffer.Usage;
import com.jme3.scene.debug.SkeletonDebugger;
import com.jme3.scene.plugins.bvh.BVHAnimData;
import com.jme3.texture.Texture;
import com.jme3.util.BufferUtils;
import com.ufharmony.Main;
import com.ufharmony.grid.UniqueSquare;

public class ObjectDerpy extends ObjectBase implements AnimEventListener
{
	private AnimChannel channel;
	private AnimControl control;
	
	public ObjectDerpy(int objectId)
	{
		super( objectId );
		
		myModel = Main.getInstance().getAssetManager().loadModel( "Model/Test/derpy.mesh.xml" );
		
		myModel.setLocalRotation( new Quaternion().fromAngleAxis( FastMath.PI / 2, new Vector3f( 0, 1, 0 ) ) );
		
		/*
		control = myModel.getControl( AnimControl.class );
		control.addListener( this );
		channel = control.createChannel();
		channel.setAnim( "stand" );
		channel.setLoopMode( LoopMode.Cycle );
		*/
		
		// "Common/MatDefs/Light/Lighting.j3md"
		// "Common/MatDefs/Misc/Unshaded.j3md"
		Material mat = new Material( Main.getInstance().getAssetManager(), "Common/MatDefs/Light/Lighting.j3md" );
		
		Texture tex = Main.getInstance().getAssetManager().loadTexture( "Model/Test/rainbowDash.png" );
		
		mat.setTexture( "DiffuseMap", tex );
		mat.setTexture( "NormalMap", tex );
		mat.setBoolean( "UseMaterialColors", true );
		mat.setColor( "Specular", ColorRGBA.White );
		mat.setColor( "Diffuse", ColorRGBA.White );
		mat.setFloat( "Shininess", 5f );
		
		myModel.setMaterial( mat );
		
		/*
		String animName = "rainbowDashAttack";
		
		Main.getInstance().getAssetManager().registerLoader( BVHLoader.class, "bvh" );
		
		BVHAnimData animData = (BVHAnimData) Main.getInstance().getAssetManager().loadAsset( "Model/Test/" + animName + ".bvh" );
		
		HashMap<String, Animation> anims = new HashMap<String, Animation>();
		anims.put( animData.getAnimation().getName(), animData.getAnimation() );
		
		AnimControl ctrl = myModel.getControl( AnimControl.class );
		
		float targetHeight = ( (BoundingBox) myModel.getWorldBound() ).getYExtent();// BVHUtils.getSkeletonHeight(control.getSkeleton());
		float sourceHeight = BVHUtils.getSkeletonHeight( animData.getSkeleton() );
		float ratio = targetHeight / sourceHeight;
		final AnimChannel animChannel = createAnimSkeleton( animData, ratio, animName );
		
		Map<String, String> boneMapping = new HashMap<String, String>();
		boneMapping.put( "Hips", "IK.Master" );
		boneMapping.put( "Chest", "Stomach" );//
		boneMapping.put( "Neck", "Neck" );
		boneMapping.put( "Head", "Head" );
		boneMapping.put( "LeftCollar", "Clavicle.L" );
		boneMapping.put( "RightCollar", "Clavicle.R" );
		boneMapping.put( "LeftUpArm", "Humerus.L" );
		boneMapping.put( "RightUpArm", "Humerus.R" );
		boneMapping.put( "LeftLowArm", "Ulna.L" );
		boneMapping.put( "RightLowArm", "Ulna.R" );
		boneMapping.put( "LeftHand", "Hand.L" );
		boneMapping.put( "RightHand", "Hand.R" );
		boneMapping.put( "LeftUpLeg", "Thigh.L" );
		boneMapping.put( "RightUpLeg", "Thigh.R" );
		boneMapping.put( "LeftLowLeg", "Calf.L" );
		boneMapping.put( "RightLowLeg", "Calf.R" );
		boneMapping.put( "LeftFoot", "Foot.L" );
		boneMapping.put( "RightFoot", "Foot.R" );
		
		control.addAnim( BVHUtils.reTarget( myModel, animData, boneMapping, false ) );
		
		SkeletonDebugger skeletonDebug = new SkeletonDebugger( "skeleton", control.getSkeleton() );
		Material mat1 = new Material( Main.getInstance().getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md" );
		mat1.getAdditionalRenderState().setWireframe( true );
		mat1.setColor( "Color", ColorRGBA.Green );
		mat1.getAdditionalRenderState().setDepthTest( false );
		skeletonDebug.setMaterial( mat1 );
		// rootNode.attachChild( skeletonDebug );
		
		final AnimChannel channel = control.createChannel();
		control.addListener( this );
		
		channel.setAnim( animName, 0.50f );
		channel.setLoopMode( LoopMode.Cycle );
		channel.setSpeed( 0.5f );
		
		animChannel.setAnim( animName, 0.50f );
		animChannel.setLoopMode( LoopMode.Cycle );
		animChannel.setSpeed( 0.5f );
		*/
	}
	
	private AnimChannel createAnimSkeleton( BVHAnimData animData, float scale, String animName )
	{
		SkeletonDebugger skeletonDebug = new SkeletonDebugger( "skeleton", animData.getSkeleton() );
		Material mat = new Material( Main.getInstance().getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md" );
		mat.getAdditionalRenderState().setWireframe( true );
		mat.setColor( "Color", ColorRGBA.Red );
		mat.getAdditionalRenderState().setDepthTest( false );
		skeletonDebug.setMaterial( mat );
		skeletonDebug.setLocalScale( scale );
		// rootNode.attachChild( skeletonDebug );
		
		Mesh[] meshes = new Mesh[2];
		meshes[0] = skeletonDebug.getWires();
		createBindPose( skeletonDebug.getWires() );
		meshes[1] = skeletonDebug.getPoints();
		createBindPose( skeletonDebug.getPoints() );
		
		HashMap<String, Animation> anims = new HashMap<String, Animation>();
		anims.put( animData.getAnimation().getName(), animData.getAnimation() );
		
		// skeletonDebug, meshes,
		AnimControl ctrl = new AnimControl( animData.getSkeleton() );
		ctrl.setAnimations( anims );
		skeletonDebug.addControl( ctrl );
		
		for ( String anim : ctrl.getAnimationNames() )
		{
			System.out.println( anim );
		}
		
		ctrl.addListener( this );
		AnimChannel channel = ctrl.createChannel();
		
		return channel;
	}
	
	private void createBindPose( Mesh mesh )
	{
		VertexBuffer pos = mesh.getBuffer( Type.Position );
		if ( pos == null || mesh.getBuffer( Type.BoneIndex ) == null )
		{
			// ignore, this mesh doesn't have positional data
			// or it doesn't have bone-vertex assignments, so its not animated
			return;
		}
		
		VertexBuffer bindPos = new VertexBuffer( Type.BindPosePosition );
		bindPos.setupData( Usage.CpuOnly, 3, Format.Float, BufferUtils.clone( pos.getData() ) );
		mesh.setBuffer( bindPos );
		
		// XXX: note that this method also sets stream mode
		// so that animation is faster. this is not needed for hardware skinning
		pos.setUsage( Usage.Stream );
		
		VertexBuffer norm = mesh.getBuffer( Type.Normal );
		if ( norm != null )
		{
			VertexBuffer bindNorm = new VertexBuffer( Type.BindPoseNormal );
			bindNorm.setupData( Usage.CpuOnly, 3, Format.Float, BufferUtils.clone( norm.getData() ) );
			mesh.setBuffer( bindNorm );
			norm.setUsage( Usage.Stream );
		}
	}
	
	public void customizeMe( UniqueSquare us )
	{
		us.setScale( 1f );
		us.setOffset( new Vector3f( 0, 0.4f, 0 ) );
	}
	
	@Override
	public void onAnimChange( AnimControl arg0, AnimChannel arg1, String arg2 )
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onAnimCycleDone( AnimControl arg0, AnimChannel arg1, String arg2 )
	{
		// TODO Auto-generated method stub
		
	}
}

package com.ufharmony.grid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.SceneProcessor;
import com.jme3.shadow.PssmShadowRenderer;
import com.jme3.util.SkyFactory;
import com.ufharmony.Main;
import com.ufharmony.blocks.BlockBase;
import com.ufharmony.utils.ChunkPosition;
import com.ufharmony.utils.Vector3Int;

public class TerrainHelper
{
	private static final Vector3f lightDirection = new Vector3f( -0.8F, -1.0F, -0.8F ).normalizeLocal();
	
	public static GridSettings getSettings( Application application )
	{
		GridSettings settings = new GridSettings( application );
		settings.setDefaultSquareMaterial( "Textures/set1.png" );
		
		settings.setChunkSizeX( 16 );
		settings.setChunkSizeY( 256 );
		settings.setChunkSizeZ( 16 );
		settings.setSquareSize( 1.5f );
		
		return settings;
	}
	
	public static void doExplosion( Vector3Int l )
	{
		doExplosion( l.getX(), l.getY(), l.getZ() );
	}
	
	public static void doExplosion( int explosionX, int explosionY, int explosionZ )
	{
		int field_77289_h = 6;
		Random r = new Random();
		
		List<ChunkPosition> affectedSquarePositions = new ArrayList<ChunkPosition>();
		
		float explosionSize = 6;
		HashSet<ChunkPosition> var2 = new HashSet<ChunkPosition>();
		int var3;
		int var4;
		int var5;
		double var15;
		double var17;
		double var19;
		
		for ( var3 = 0; var3 < field_77289_h; ++var3 )
		{
			for ( var4 = 0; var4 < field_77289_h; ++var4 )
			{
				for ( var5 = 0; var5 < field_77289_h; ++var5 )
				{
					if ( var3 == 0 || var3 == field_77289_h - 1 || var4 == 0 || var4 == field_77289_h - 1 || var5 == 0 || var5 == field_77289_h - 1 )
					{
						double var6 = (double) ( (float) var3 / ( (float) field_77289_h - 1.0F ) * 2.0F - 1.0F );
						double var8 = (double) ( (float) var4 / ( (float) field_77289_h - 1.0F ) * 2.0F - 1.0F );
						double var10 = (double) ( (float) var5 / ( (float) field_77289_h - 1.0F ) * 2.0F - 1.0F );
						double var12 = Math.sqrt( var6 * var6 + var8 * var8 + var10 * var10 );
						var6 /= var12;
						var8 /= var12;
						var10 /= var12;
						float var14 = explosionSize * ( 0.7F + r.nextFloat() * 0.6F );
						var15 = explosionX;
						var17 = explosionY;
						var19 = explosionZ;
						
						for ( float var21 = 0.3F; var14 > 0.0F; var14 -= var21 * 0.75F )
						{
							int var22 = (int) (int) Math.floor( var15 );
							int var23 = (int) (int) Math.floor( var17 );
							int var24 = (int) (int) Math.floor( var19 );
							Square var26 = Main.getWorld().getInst( var22, var23, var24 );
							
							if ( var26 != null )
							{
								float var27 = 1f;// exploder != null ? exploder.func_82146_a( this, var26, var22, var23, var24 )
														// : var26.getExplosionResistance( exploder, worldObj, var22, var23, var24,
														// explosionX, explosionY, explosionZ );
								var14 -= ( var27 + 0.3F ) * var21;
							}
							
							if ( var14 > 0.0F )
							{
								var2.add( new ChunkPosition( var22, var23, var24 ) );
							}
							
							var15 += var6 * (double) var21;
							var17 += var8 * (double) var21;
							var19 += var10 * (double) var21;
						}
					}
				}
			}
		}
		
		affectedSquarePositions.addAll( var2 );
		explosionSize *= 2.0F;
		var3 = (int) Math.floor( explosionX - (double) explosionSize - 1.0D );
		var4 = (int) Math.floor( explosionX + (double) explosionSize + 1.0D );
		var5 = (int) Math.floor( explosionY - (double) explosionSize - 1.0D );
		int var6;
		int var29 = (int) Math.floor( explosionY + (double) explosionSize + 1.0D );
		int var7 = (int) Math.floor( explosionZ - (double) explosionSize - 1.0D );
		int var30 = (int) Math.floor( explosionZ + (double) explosionSize + 1.0D );
		
		for ( ChunkPosition cp : affectedSquarePositions )
		{
			var4 = cp.x;
			var5 = cp.y;
			var6 = cp.z;
			Square var25 = Main.getWorld().getInst( var4, var5, var6 );
			
			if ( var25 != null )
			{
				Main.getWorld().removeSquare( var4, var5, var6 );
				//var25.onSquareDestroyedByExplosion();
			}
		}
	}
	
	public static void initializeEnvironment( SimpleApplication simpleApplication )
	{
		DirectionalLight directionalLight = new DirectionalLight();
		directionalLight.setDirection( lightDirection );
		directionalLight.setColor( new ColorRGBA( 1.0F, 1.0F, 1.0F, 1.0F ) );
		simpleApplication.getRootNode().addLight( directionalLight );
		simpleApplication.getRootNode().attachChild( SkyFactory.createSky( simpleApplication.getAssetManager(), "/Textures/Sky/default.jpg", true ) );
		
		PssmShadowRenderer pssmShadowRenderer = new PssmShadowRenderer( simpleApplication.getAssetManager(), 2048, 3 );
		pssmShadowRenderer.setDirection( lightDirection );
		pssmShadowRenderer.setShadowIntensity( 0.3F );
		simpleApplication.getViewPort().addProcessor( pssmShadowRenderer );
	}
	
	public static void initializeWater( SimpleApplication simpleApplication )
	{
		// WaterFilter waterFilter = new WaterFilter( simpleApplication.getRootNode(), lightDirection );
		// getFilterPostProcessor( simpleApplication ).addFilter( waterFilter );
	}
	
	private static FilterPostProcessor getFilterPostProcessor( SimpleApplication simpleApplication )
	{
		List sceneProcessors = simpleApplication.getViewPort().getProcessors();
		for ( int i = 0; i < sceneProcessors.size(); i++ )
		{
			SceneProcessor sceneProcessor = (SceneProcessor) sceneProcessors.get( i );
			if ( ( sceneProcessor instanceof FilterPostProcessor ) )
			{
				return (FilterPostProcessor) sceneProcessor;
			}
		}
		FilterPostProcessor filterPostProcessor = new FilterPostProcessor( simpleApplication.getAssetManager() );
		simpleApplication.getViewPort().addProcessor( filterPostProcessor );
		return filterPostProcessor;
	}
}

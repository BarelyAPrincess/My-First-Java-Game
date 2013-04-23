package com.ufharmony.grid;

import java.util.Random;

import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.debug.Arrow;
import com.ufharmony.Main;
import com.ufharmony.utils.Vector3Int;

public class Util
{
	public static Random r = new Random();
	private static final float MAX_FLOAT_ROUNDING_DIFFERENCE = 1.0E-04F;
	
	public static boolean isValidIndex( byte[][][] array, Vector3Int index )
	{
		return ( index.getX() >= 0 ) && ( index.getX() < array.length ) && ( index.getY() >= 0 ) && ( index.getY() < array[0].length ) && ( index.getZ() >= 0 ) && ( index.getZ() < array[0][0].length );
	}
	
	public static boolean isValidIndex( Object[][][] array, Vector3Int index )
	{
		return ( index.getX() >= 0 ) && ( index.getX() < array.length ) && ( index.getY() >= 0 ) && ( index.getY() < array[0].length ) && ( index.getZ() >= 0 ) && ( index.getZ() < array[0][0].length );
	}
	
	public static Vector3f compensateFloatRoundingErrors( Vector3f vector )
	{
		return new Vector3f( compensateFloatRoundingErrors( vector.getX() ), compensateFloatRoundingErrors( vector.getY() ), compensateFloatRoundingErrors( vector.getZ() ) );
	}
	
	public static float compensateFloatRoundingErrors( float number )
	{
		float remainder = number % 1.0F;
		if ( ( remainder < 1.0E-04F ) || ( remainder > 0.9999F ) )
		{
			number = Math.round( number );
		}
		return number;
	}
	
	static void makeArrow( Node n, ColorRGBA c, Vector3f l )
	{
		if ( n == null || c == null || l == null )
			return;
		
		Arrow arrow = new Arrow( new Vector3f( 0, 1, 0 ) );
		Geometry marker = new Geometry( "Marker" );
		marker.setMesh( arrow );
		
		Material mat = new Material( Main.getInstance().getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md" );
		mat.setColor( "Color", c );
		mat.getAdditionalRenderState().setBlendMode( BlendMode.Alpha );
		
		marker.setMaterial( mat );
		
		n.attachChild( marker );
		
		marker.setLocalTranslation( l );
	}
}

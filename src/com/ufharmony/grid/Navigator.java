package com.ufharmony.grid;

import com.jme3.math.Vector3f;
import com.ufharmony.utils.Vector3Int;

public class Navigator
{
	public static Vector3Int getNeighborSquareLocalLocation( Vector3Int location, Square.Face face )
	{
		Vector3Int neighborLocation = getNeighborSquareLocation_Relative( face );
		neighborLocation.addLocal( location );
		return neighborLocation;
	}
	
	public static Vector3Int getNeighborSquareLocation_Relative( Square.Face face )
	{
		Vector3Int neighborLocation = new Vector3Int();
		switch ( face )
		{
			case Top:
				neighborLocation.set( 0, 1, 0 );
				break;
			case Bottom:
				neighborLocation.set( 0, -1, 0 );
				break;
			case West:
				neighborLocation.set( -1, 0, 0 );
				break;
			case East:
				neighborLocation.set( 1, 0, 0 );
				break;
			case North:
				neighborLocation.set( 0, 0, 1 );
				break;
			case South:
				neighborLocation.set( 0, 0, -1 );
		}
		
		return neighborLocation;
	}
	
	public static Vector3Int getPointedSquareLocation( TerrainControl squareTerrain, Vector3f collisionContactPoint, boolean getNeighborLocation )
	{
		Vector3f collisionLocation = Util.compensateFloatRoundingErrors( collisionContactPoint );
		Vector3Int squareLocation = new Vector3Int( (int) ( collisionLocation.getX() / squareTerrain.getSettings().getSquareSize() ), (int) ( collisionLocation.getY() / squareTerrain.getSettings().getSquareSize() ), (int) ( collisionLocation.getZ() / squareTerrain.getSettings().getSquareSize() ) );
		
		if ( ( squareTerrain.getSquare( squareLocation ) != null ) == getNeighborLocation )
		{
			if ( collisionLocation.getX() % squareTerrain.getSettings().getSquareSize() == 0.0F )
			{
				squareLocation.subtractLocal( 1, 0, 0 );
			}
			else if ( collisionLocation.getY() % squareTerrain.getSettings().getSquareSize() == 0.0F )
			{
				squareLocation.subtractLocal( 0, 1, 0 );
			}
			else if ( collisionLocation.getZ() % squareTerrain.getSettings().getSquareSize() == 0.0F )
			{
				squareLocation.subtractLocal( 0, 0, 1 );
			}
		}
		return squareLocation;
	}
}

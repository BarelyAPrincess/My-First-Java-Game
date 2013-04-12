package com.ufharmony.grid;

import com.jme3.math.Vector3f;

public class BlockProperties
{
	public boolean dummyInst = false;
	
	public BlockProperties ()
	{
		
	}
	
	public BlockProperties ( boolean fake )
	{
		if ( fake )
		{
			side_top_front = true;
			side_top_back = true;
			side_top_left = true;
			side_top_right = true;
			
			side_bottom_front = true;
			side_bottom_back = true;
			side_bottom_left = true;
			side_bottom_right = true;
			
			dummyInst = true;
		}
	}
	
	public boolean isTopAllTrue ()
	{
		return ( side_top_front == true && side_top_back == true && side_top_right == true && side_top_left == true );
	}
	
	public BlockProperties corner_top_1( Vector3f vec )
	{
		corner_top_1 = vec;
		return this;
	}
	
	public BlockProperties corner_top_2( Vector3f vec )
	{
		corner_top_2 = vec;
		return this;
	}
	
	public BlockProperties corner_top_3( Vector3f vec )
	{
		corner_top_3 = vec;
		return this;
	}
	
	public BlockProperties corner_top_4( Vector3f vec )
	{
		corner_top_4 = vec;
		return this;
	}
	
	public boolean side_top_front = false;
	public boolean side_top_back = false;
	public boolean side_top_left = false;
	public boolean side_top_right = false;
	
	public boolean side_bottom_front = false;
	public boolean side_bottom_back = false;
	public boolean side_bottom_left = false;
	public boolean side_bottom_right = false;
	
	public Vector3f corner_top_1;
	public Vector3f corner_top_2;
	public Vector3f corner_top_3;
	public Vector3f corner_top_4;
	
	public Vector3f corner_bottom_1;
	public Vector3f corner_bottom_2;
	public Vector3f corner_bottom_3;
	public Vector3f corner_bottom_4;
	
	public BlockSkin blockSkin;
	
	public int x;
	public int y;
	public int z;
}

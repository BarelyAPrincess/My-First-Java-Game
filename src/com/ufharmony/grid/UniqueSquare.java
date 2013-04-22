package com.ufharmony.grid;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.ufharmony.blocks.BlockBase;
import com.ufharmony.objects.ObjectBase;
import com.ufharmony.utils.Vector3Int;

public class UniqueSquare
{
	private Class<? extends Square> parentClass = null;
	private Square squareInstance = null;
	
	public boolean active = false;

	private float lightLevel = 0.0f;
	private ColorRGBA lightColor = new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f);
	public float scale = 1.0f;
	public Vector3f offset = new Vector3f();
	public Material material = null;
	
	// Is my top point visible?
	public boolean isTopVisible = false;
	
	// My XYZ location
	public Vector3Int location = new Vector3Int();
	
	// My location in the 3d Space
	public Vector3f locationReal = new Vector3f();
	
	public UniqueSquare( Square ob, Vector3Int location )
	{
		updateInformation( location );
		
		this.type = ob.getId();
		
		if ( ob instanceof BlockBase )
			this.skin = ((BlockBase) ob).getSkin();
		
		if ( ob instanceof ObjectBase )
		{
			
		}
		
		squareInstance = ob;
		
		parentClass = ob.getParentClass();
		
		ob.customizeMe( this );
	}
	
	public void updateInformation( Vector3Int loc )
	{
		//UniqueSquare neighborSquare_Top = TerrainControl.getInstance().getSquare( GridUtils.getNeighborSquareWorldLocation( location, Square.Face.Top ) );
		//isTopVisible = ( neighborSquare_Top == null ? true : false );
		
		location = loc;
		
		float squareSize = TerrainControl.getSettings().getSquareSize();
		
		locationReal = new Vector3f( loc.getX(), loc.getY(), loc.getZ() ).mult( new Vector3f( squareSize, squareSize / 2f, squareSize ) );
	}
	
	public void makeGlow()
	{
		
	}
	
	public float getScale()
	{
		return scale;
	}
	
	public void setScale( float s )
	{
		scale = s;
	}
	
	public Vector3f getOffset()
	{
		return offset;
	}
	
	public void setOffset( Vector3f o )
	{
		offset = o;
	}
	
	public Material getMaterial()
	{
		return material;
	}
	
	public void setMaterial( Material m )
	{
		material = m;
	}
	
	public Class<? extends Square> getParentClass()
	{
		return parentClass;
	}
	
	public ObjectBase getObjectInstance()
	{
		if ( squareInstance instanceof ObjectBase )
			return (ObjectBase) squareInstance;
		
		return null;
	}
	
	public BlockBase getBlockInstance()
	{
		if ( squareInstance instanceof ObjectBase )
			return (BlockBase) squareInstance;
		
		return null;
	}
	
	public Square getInstance()
	{
		return squareInstance;
	}
	
	private byte type;
	private BlockSkin skin;
	
	public byte getType()
	{
		return type;
	}
	
	public BlockBase getBlock()
	{
		return BlockBase.getBlock( type );
	}
	
	public BlockSkin getSkin()
	{
		return skin;
	}
	
	public void setLightLevel( float level )
	{
		lightLevel = level;
	}
	
	public float getLightLevel()
	{
		return lightLevel;
	}
	
	public void setLevelColor( ColorRGBA color )
	{
		lightColor = color;
	}
	
	public ColorRGBA getLevelColor()
	{
		return lightColor;
	}
}
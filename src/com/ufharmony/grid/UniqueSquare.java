package com.ufharmony.grid;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.ufharmony.blocks.BlockBase;
import com.ufharmony.objects.ObjectBase;

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
	
	public UniqueSquare( Square ob )
	{
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
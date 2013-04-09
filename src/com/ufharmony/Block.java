package com.ufharmony;

public class Block
{
  private BlockType type;

  public Block()
  {
    type = BlockManager.getType(getClass());
  }

  public BlockType getType()
  {
    return type;
  }

  public static enum Face
  {
    Top, Bottom, Left, Right, Front, Back;
  }
}
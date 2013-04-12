package com.ufharmony.grid;

import com.ufharmony.utils.Vector3Int;

public class BlockSkin
{
  private Skin_TextureLocation[] textureLocations;
  private boolean isTransparent;

  public BlockSkin(Skin_TextureLocation textureLocation, boolean isTransparent)
  {
    this(new Skin_TextureLocation[] { textureLocation }, isTransparent);
  }

  public BlockSkin(Skin_TextureLocation[] textureLocations, boolean isTransparent) {
    this.textureLocations = textureLocations;
    this.isTransparent = isTransparent;
  }

  public Skin_TextureLocation getTextureLocation(ChunkControl chunk, Vector3Int blockLocation, Square.Face face)
  {
    return textureLocations[getTextureLocationIndex(chunk, blockLocation, face)];
  }

  protected int getTextureLocationIndex(ChunkControl chunk, Vector3Int blockLocation, Square.Face face) {
    if (textureLocations.length == 6) {
      return face.ordinal();
    }
    return 0;
  }

  public boolean isTransparent() {
    return isTransparent;
  }
}
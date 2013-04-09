package com.ufharmony;

public class BlockSkin
{
  private BlockSkin_TextureLocation[] textureLocations;
  private boolean isTransparent;

  public BlockSkin(BlockSkin_TextureLocation textureLocation, boolean isTransparent)
  {
    this(new BlockSkin_TextureLocation[] { textureLocation }, isTransparent);
  }

  public BlockSkin(BlockSkin_TextureLocation[] textureLocations, boolean isTransparent) {
    this.textureLocations = textureLocations;
    this.isTransparent = isTransparent;
  }

  public BlockSkin_TextureLocation getTextureLocation(BlockChunkControl chunk, Vector3Int blockLocation, Block.Face face)
  {
    return textureLocations[getTextureLocationIndex(chunk, blockLocation, face)];
  }

  protected int getTextureLocationIndex(BlockChunkControl chunk, Vector3Int blockLocation, Block.Face face) {
    if (textureLocations.length == 6) {
      return face.ordinal();
    }
    return 0;
  }

  public boolean isTransparent() {
    return isTransparent;
  }
}
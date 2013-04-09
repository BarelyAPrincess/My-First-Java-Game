package com.ufharmony;

import com.jme3.app.Application;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;

public class CubesSettings
{
  private AssetManager assetManager;
  private float blockSize = 3.0F;
  private int chunkSizeX = 16;
  private int chunkSizeY = 256;
  private int chunkSizeZ = 16;
  private Material blockMaterial;

  public CubesSettings(Application application)
  {
    assetManager = application.getAssetManager();
  }

  public AssetManager getAssetManager()
  {
    return assetManager;
  }

  public float getBlockSize() {
    return blockSize;
  }

  public void setBlockSize(float blockSize) {
    this.blockSize = blockSize;
  }

  public int getChunkSizeX() {
    return chunkSizeX;
  }

  public void setChunkSizeX(int chunkSizeX) {
    this.chunkSizeX = chunkSizeX;
  }

  public int getChunkSizeY() {
    return chunkSizeY;
  }

  public void setChunkSizeY(int chunkSizeY) {
    this.chunkSizeY = chunkSizeY;
  }

  public int getChunkSizeZ() {
    return chunkSizeZ;
  }

  public void setChunkSizeZ(int chunkSizeZ) {
    this.chunkSizeZ = chunkSizeZ;
  }

  public Material getBlockMaterial() {
    return blockMaterial;
  }

  public void setDefaultBlockMaterial(String textureFilePath) {
    setBlockMaterial(new BlockChunk_Material(assetManager, textureFilePath));
  }

  public void setBlockMaterial(Material blockMaterial) {
    this.blockMaterial = blockMaterial;
  }
}
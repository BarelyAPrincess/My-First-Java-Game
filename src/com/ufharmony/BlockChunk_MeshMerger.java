package com.ufharmony;

public abstract interface BlockChunk_MeshMerger
{
  public abstract boolean shouldFaceBeAdded(BlockChunkControl paramBlockChunkControl, Vector3Int paramVector3Int, Block.Face paramFace);
}
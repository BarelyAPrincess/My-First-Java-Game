package com.ufharmony.grid;

import com.ufharmony.utils.Vector3Int;

public abstract interface Chunk_MeshMerger
{
  public abstract boolean shouldFaceBeAdded(ChunkControl paramBlockChunkControl, Vector3Int paramVector3Int, Square.Face paramFace);
}
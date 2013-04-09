package com.ufharmony.network;

import java.io.IOException;

public abstract interface BitSerializable
{
  public abstract void write(BitOutputStream paramBitOutputStream);

  public abstract void read(BitInputStream paramBitInputStream)
    throws IOException;
}
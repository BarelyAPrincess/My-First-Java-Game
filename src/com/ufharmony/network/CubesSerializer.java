package com.ufharmony.network;

import com.ufharmony.grid.TerrainControl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class CubesSerializer
{
  public static byte[] writeToBytes(TerrainControl blockTerrain)
  {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    BitOutputStream bitOutputStream = new BitOutputStream(byteArrayOutputStream);
    blockTerrain.write(bitOutputStream);
    bitOutputStream.close();
    return byteArrayOutputStream.toByteArray();
  }

  public static void readFromBytes(TerrainControl blockTerrain, byte[] bytes) {
    BitInputStream bitInputStream = new BitInputStream(new ByteArrayInputStream(bytes));
    try {
      blockTerrain.read(bitInputStream);
    } catch (IOException ex) {
      System.out.println("Error while unserializing terrain.");
      ex.printStackTrace();
    }
  }
}
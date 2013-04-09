package com.ufharmony.network;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class BitInputStream
{
	private InputStream in;
	private int lastByte;
	private int bits = 0;
	
	public BitInputStream(InputStream in)
	{
		this.in = in;
	}
	
	/*
	public <Class extends Enum<List>> List readEnum( Enum<Class> enumClass ) throws IOException
	{
		Enum[] enumConstants = (Enum[]) enumClass.getEnumConstants();
		int bitsCount = BitUtil.getNeededBitsCount( enumConstants.length );
		return enumConstants[ readBits( bitsCount ) ];
	}
	*/
	
	public String readString_UTF8( int maximumBytesCountBits ) throws IOException
	{
		int bytesCount = readBits( maximumBytesCountBits );
		byte[] bytes = readBytes( bytesCount );
		return new String( bytes, "UTF-8" );
	}
	
	public byte[] readBytes( int bytesCount ) throws IOException
	{
		byte[] bytes = new byte[bytesCount];
		for ( int i = 0; i < bytes.length; i++ )
		{
			bytes[i] = ( (byte) readBits( 8 ) );
		}
		return bytes;
	}
	
	public float readFloat() throws IOException
	{
		return Float.intBitsToFloat( readBits( 32 ) );
	}
	
	public int readInteger() throws IOException
	{
		return readBits( 32 );
	}
	
	public boolean readBoolean() throws IOException
	{
		return readBits( 1 ) == 1;
	}
	
	public int readBits( int count ) throws IOException
	{
		if ( count == 0 )
		{
			throw new IllegalArgumentException( "Cannot read 0 bits." );
		}
		if ( count > 32 )
		{
			throw new IllegalArgumentException( "Bit count overflow: " + count );
		}
		
		int result = 0;
		
		int remainingCount = count;
		while ( remainingCount > 0 )
		{
			if ( bits == 0 )
			{
				int b = in.read();
				if ( b < 0 )
				{
					throw new IOException( "End of stream reached." );
				}
				lastByte = b;
				bits = 8;
			}
			
			int bitsToCopy = bits < remainingCount ? bits : remainingCount;
			
			int sourceShift = bits - bitsToCopy;
			
			int targetShift = remainingCount - bitsToCopy;
			
			result |= lastByte >> sourceShift << targetShift;
			
			remainingCount -= bitsToCopy;
			bits -= bitsToCopy;
			
			lastByte &= 255 >> 8 - bits;
		}
		return result;
	}
	
	public long readLongBits( int count ) throws IOException
	{
		if ( count == 0 )
		{
			throw new IllegalArgumentException( "Cannot read 0 bits." );
		}
		if ( count > 64 )
		{
			throw new IllegalArgumentException( "Bit count overflow: " + count );
		}
		
		long result = 0L;
		
		int remainingCount = count;
		while ( remainingCount > 0 )
		{
			if ( bits == 0 )
			{
				int b = in.read();
				if ( b < 0 )
				{
					throw new IOException( "End of stream reached." );
				}
				lastByte = b;
				bits = 8;
			}
			
			int bitsToCopy = bits < remainingCount ? bits : remainingCount;
			
			int sourceShift = bits - bitsToCopy;
			
			int targetShift = remainingCount - bitsToCopy;
			
			result |= lastByte >> sourceShift << targetShift;
			
			remainingCount -= bitsToCopy;
			bits -= bitsToCopy;
			
			lastByte &= 255 >> 8 - bits;
		}
		
		return result;
	}
	
	public void close()
	{
		try
		{
			in.close();
		}
		catch ( IOException ex )
		{
			System.out.println( "Error while closing bit stream: " + ex.toString() );
		}
	}
}

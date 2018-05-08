package com.anta40.capuploader.card.cap.structure;

import java.io.IOException;
import java.io.InputStream;

import com.anta40.capuploader.utils.ByteUtils;

public class Header {
	 private int size;
	  private int magic;
	  private byte minorVersion;
	  private byte majorVersion;
	  private byte flag;
	  private PackageInfo packageInfo;

	    public Header(InputStream inputstream)
	        throws IOException
	    {
	        size = 0;
	        magic = 0;
	        minorVersion = 0;
	        majorVersion = 0;
	        flag = 0;
	        packageInfo = null;
	        byte byte0 = (byte)(inputstream.read() & 0xff);
	        if(byte0 != 1)
	            throw new IOException("Invalid tag for Header component");
	        byte abyte0[] = new byte[4];
	        if(inputstream.read(abyte0, 0, 2) != 2)
	            throw new IOException("Error reading size");
	        size = ByteUtils.bytesToInt(abyte0, 0, 2);
	        if(inputstream.read(abyte0, 0, 4) != 4)
	            throw new IOException("Error reading magic");
	        magic = ByteUtils.bytesToInt(abyte0, 0, 4);
	        minorVersion = (byte)(inputstream.read() & 0xff);
	        majorVersion = (byte)(inputstream.read() & 0xff);
	        flag = (byte)(inputstream.read() & 0xff);
	        packageInfo = new PackageInfo(inputstream);
	    }

	    public int getMagic()
	    {
	        return magic;
	    }

	    public byte getMajorVersion()
	    {
	        return majorVersion;
	    }

	    public byte getMinorVersion()
	    {
	        return minorVersion;
	    }

	    public byte getFlag()
	    {
	        return flag;
	    }

	    public PackageInfo getPackageInfo()
	    {
	        return packageInfo;
	    }

	    public int getDataSize()
	    {
	        return size;
	    }
}

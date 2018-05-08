package com.anta40.capuploader.card.cap.structure;

import java.io.IOException;
import java.io.InputStream;

public class PackageInfo {
	 private byte minorVersion;
	  private byte majorVersion;
	  private byte[] aid;

	    public PackageInfo(InputStream inputstream)
	        throws IOException
	    {
	        aid = null;
	        minorVersion = (byte)(inputstream.read() & 0xff);
	        majorVersion = (byte)(inputstream.read() & 0xff);
	        byte byte0 = (byte)(inputstream.read() & 0xff);
	        aid = new byte[byte0];
	        if(inputstream.read(aid) != byte0) {
	          aid = null;
	        }
	    }

	    public byte[] getAID()
	    {
	        return aid;
	    }

	    public byte getMinorVersion()
	    {
	        return minorVersion;
	    }

	    public byte getMajorVersion()
	    {
	        return majorVersion;
	    }

}

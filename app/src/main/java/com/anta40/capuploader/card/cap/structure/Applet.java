package com.anta40.capuploader.card.cap.structure;

import java.io.IOException;
import java.io.InputStream;

import com.anta40.capuploader.utils.ByteUtils;

public class Applet {
	private int appletCount;
	 // private int size;
	  private AID[] aid;
	  private int installOffset[];

	    public Applet(InputStream inputstream)
	        throws IOException
	    {
	        appletCount = 0;
	  //      size = 0;
	        aid = null;
	        installOffset = null;
	        byte byte0 = (byte)(inputstream.read() & 0xff);
	        if(byte0 != 3)
	            throw new IOException("Invalid tag for StaticField component");
	        byte abyte0[] = new byte[2];
	        if(inputstream.read(abyte0, 0, 2) != 2)
	            throw new IOException("Error reading StaticField's component");
	   //     size = CardUtils.bytesToInt(abyte0, 0, 2);
	        appletCount = (byte)(inputstream.read() & 0xff);
	        if(appletCount > 0)
	        {
	            aid = new AID[appletCount];
	            installOffset = new int[appletCount];
	            for(int i = 0; i < appletCount; i++)
	            {
	                aid[i] = new AID(inputstream);
	                if(inputstream.read(abyte0, 0, 2) != 2)
	                    throw new IOException("Error reading StaticField's component");
	                installOffset[i] = ByteUtils.bytesToInt(abyte0, 0, 2);
	            }

	        }
	    }

	    public int getAppletCount()
	    {
	        return appletCount;
	    }

	    public int getInstallOffset(int i)
	    {
	        return installOffset[i];
	    }

	    public AID getAID(int i)
	    {
	        return aid[i];
	    }

	    public int[] getInstallOffset()
	    {
	        return installOffset;
	    }

	    public AID[] getAIDs()
	    {
	        return aid;
	    }

}

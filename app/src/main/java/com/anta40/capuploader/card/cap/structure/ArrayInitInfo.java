package com.anta40.capuploader.card.cap.structure;

import java.io.IOException;
import java.io.InputStream;

import com.anta40.capuploader.utils.ByteUtils;

public class ArrayInitInfo {
	 private byte tag;
	    private byte values[];
	    private static final int u2_SIZE = 2;
	    private static final byte BOOLEAN_TAG = 2;
	    private static final byte BYTE_TAG = 3;
	    private static final byte SHORT_TAG = 4;
	    private static final byte INT_TAG = 5;

		
	    public ArrayInitInfo(InputStream inputstream)
	        throws IOException
	    {
	        tag = 0;
	        values = null;
	        byte abyte0[] = new byte[2];
	        tag = (byte)(inputstream.read() & 0xff);
	        if(inputstream.read(abyte0, 0, 2) != 2)
	            throw new IOException("Error reading Static field size's image size");
	        int i = ByteUtils.bytesToInt(abyte0, 0, 2);
	        values = new byte[i];
	        if(inputstream.read(values, 0, i) != i)
	            throw new IOException("Error reading Static field size's image size");
	        else
	            return;
	    }

	    public byte getTag()
	    {
	        return tag;
	    }

	    public int getSize()
	    {
	        return values.length;
	    }

	    public byte[] getValues()
	    {
	        return values;
	    }
}

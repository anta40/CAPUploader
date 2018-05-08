package com.anta40.capuploader.card.cap.structure;

import java.io.IOException;
import java.io.InputStream;

import com.anta40.capuploader.utils.ByteUtils;

public class StaticField {
	 private int size;
	    private int imageSize;
	    private int referenceCount;
	    private ArrayInitInfo arrayInitInfos[];
	    private int defaultValueCount;
	    private byte nonDefaultValues[];
	    public static final byte STATICFIELD_FILE_TAG = 8;
	    public static final byte u2_DATA_SIZE = 2;

	    public StaticField(InputStream inputstream)
	        throws IOException
	    {
	        size = 0;
	        imageSize = 0;
	        referenceCount = 0;
	        arrayInitInfos = null;
	        nonDefaultValues = null;
	        byte byte0 = (byte)(inputstream.read() & 0xff);
	        if(byte0 != 8)
	            throw new IOException("Invalid tag for StaticField component");
	        byte abyte0[] = new byte[2];
	        if(inputstream.read(abyte0, 0, 2) != 2)
	            throw new IOException("Error reading StaticField's component");
	        size = ByteUtils.bytesToInt(abyte0, 0, 2);
	        if(inputstream.read(abyte0, 0, 2) != 2)
	            throw new IOException("Error reading StaticField's component");
	        imageSize = ByteUtils.bytesToInt(abyte0, 0, 2);
	        if(inputstream.read(abyte0, 0, 2) != 2)
	            throw new IOException("Error reading StaticField's component");
	        referenceCount = ByteUtils.bytesToInt(abyte0, 0, 2);
	        if(inputstream.read(abyte0, 0, 2) != 2)
	            throw new IOException("Error reading StaticField's component");
	        int i = ByteUtils.bytesToInt(abyte0, 0, 2);
	        if(i > 0)
	        {
	            arrayInitInfos = new ArrayInitInfo[i];
	            for(int j = 0; j < i; j++)
	                arrayInitInfos[j] = new ArrayInitInfo(inputstream);

	        }
	        if(inputstream.read(abyte0, 0, 2) != 2)
	            throw new IOException("Error reading StaticField's component");
	        defaultValueCount = ByteUtils.bytesToInt(abyte0, 0, 2);
	        if(inputstream.read(abyte0, 0, 2) != 2)
	            throw new IOException("Error reading StaticField's component");
	        int k = ByteUtils.bytesToInt(abyte0, 0, 2);
	        if(k > 0)
	        {
	            nonDefaultValues = new byte[k];
	            if(inputstream.read(nonDefaultValues, 0, k) != k)
	                throw new IOException("Error reading StaticField's component");
	        }
	    }

	    public int getImageSize()
	    {
	        return imageSize;
	    }

	    public int getReferenceCount()
	    {
	        return referenceCount;
	    }

	    public int getSize()
	    {
	        return size;
	    }

	    public ArrayInitInfo[] getArrayInitInfos()
	    {
	        return arrayInitInfos;
	    }

	    public int getDefaultValueCount()
	    {
	        return defaultValueCount;
	    }

	    public byte[] getNonDefaultValues()
	    {
	        return nonDefaultValues;
	    }

	    public int getNonDefaultValuesSize()
	    {
	        if(nonDefaultValues != null)
	            return nonDefaultValues.length;
	        else
	            return 0;
	    }
}

package com.anta40.capuploader.card.cap.structure;

import java.io.IOException;
import java.io.InputStream;

public class AID {
	private byte[] aid;

	/**
	 * @param aid
	 */
	public AID(byte[] aid) {
		super();
		this.aid = aid;
	}


	public AID(InputStream inputstream)
	throws IOException
	{
		aid = null;
		byte byte0 = (byte)(inputstream.read() & 0xff);
		aid = new byte[byte0];
		if(inputstream.read(aid) != byte0)
			throw new IOException("Expecting AID length " + byte0);
		else
			return;
	}


	public byte[] getAid() {
		return aid;
	}
	public void setAid(byte[] aid) {
		this.aid = aid;
	}
}

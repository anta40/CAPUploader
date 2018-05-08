package com.anta40.capuploader;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import android.nfc.tech.IsoDep;


public class Iso7816 {
	public static final byte[] EMPTY = { 0 };

	protected byte[] data;

	protected Iso7816() {
		data = Iso7816.EMPTY;
	}

	protected Iso7816(byte[] bytes) {
		data = (bytes == null) ? Iso7816.EMPTY : bytes;
	}

	public boolean match(byte[] bytes) {
		return match(bytes, 0);
	}

	public boolean match(byte[] bytes, int start) {
		final byte[] data = this.data;
		if (data.length <= bytes.length - start) {
			for (final byte v : data) {
				if (v != bytes[start++])
					return false;
			}
		}
		return true;
	}

	public boolean match(byte tag) {
		return (data.length == 1 && data[0] == tag);
	}

	public boolean match(short tag) {
		final byte[] data = this.data;
		if (data.length == 2) {
			final byte d0 = (byte) (0x000000FF & tag);
			final byte d1 = (byte) (0x000000FF & (tag >> 8));
			return (data[0] == d0 && data[1] == d1);
		}
		return false;
	}

	public int size() {
		return data.length;
	}

	public byte[] getBytes() {
		return data;
	}

	@Override
	public String toString() {
		return Util.toHexString(data, 0, data.length);
	}

	public final static class ID extends Iso7816 {
		public ID(byte[] bytes) {
			super(bytes);
		}
	}

	public final static class Response extends Iso7816 {
		public static final byte[] EMPTY = {};
		public static final byte[] ERROR = { 0x6F, 0x00 }; // SW_UNKNOWN

		public Response(byte[] bytes) {
			super((bytes == null || bytes.length < 2) ? Response.ERROR : bytes);
		}

		public byte getSw1() {
			return data[data.length - 2];
		}

		public byte getSw2() {
			return data[data.length - 1];
		}

		public short getSw12() {
			final byte[] d = this.data;
			int n = d.length;
			return (short) ((d[n - 2] << 8) | (0xFF & d[n - 1]));
		}



		public boolean isOkey() {
			return equalsSw12(SW_NO_ERROR);
		}

		public boolean equalsSw12(short val) {
			return getSw12() == val;
		}

		public int size() {
			return data.length - 2;
		}

		public byte[] getBytes() {
			return isOkey() ? Arrays.copyOfRange(data, 0, size())
					: Response.EMPTY;
		}

		public String appendSw1Sw2Meaning()
		{
			String sw1sw2 = this.toString().substring(this.toString().length()-4, this.toString().length());
			
			// General only
			if (sw1sw2.equals("9000"))
				return "No Further Qualification";
			else if (sw1sw2.equals("6700"))
				return "Wrong length, no further indication";
			else if (sw1sw2.equals("6B00"))
				return "Wrong parameters P1-P2";
			else if (sw1sw2.equals("6D00"))
				return "Instruction code not supported or invalid";
			else if (sw1sw2.equals("6E00"))
				return "Class not supported";
			else if (sw1sw2.equals("6F00"))
				return "No precise diagnosis";

			//0x6100
			else if ((sw1sw2.substring(0, 2)).equals("61"))
				return "SW2 encodes the number of data bytes still available (see text below of ISO7816-4)";

			// 0x6200
			else if (sw1sw2.equals("6200"))
				return "No information given";
			else if (sw1sw2.equals("6202"))
				return "Triggering by the card (see 9.6.1 of ISO7816-4)";
			else if (sw1sw2.equals("6280"))
				return "Triggering by the card (see 9.6.1 of ISO7816-4)";
			else if (sw1sw2.equals("6281"))
				return "Part of returned data may be corrupted";
			else if (sw1sw2.equals("6282"))
				return "End of file or record reached before reading Ne bytes";
			else if (sw1sw2.equals("6283"))
				return "Selected file deactivated";
			else if (sw1sw2.equals("6284"))
				return "File control information not formatted according to 5.3.3 of ISO7816-4";

			//0x6300
			else if (sw1sw2.equals("6300"))
				return "No information given ";
			else if (sw1sw2.equals("6381"))
				return "File filled up by the last write";
			else if ((sw1sw2.substring(0, 2)).equals("63C"))
				return "Counter in the range 0 to 15 encoded by + 'X' (="+ sw1sw2.substring(sw1sw2.length()-2,sw1sw2.length()) +") " +
				"exact meaning depending on the command)";

			//0x6400
			else if (sw1sw2.equals("6400"))
				return "Execution error ";
			else if (sw1sw2.equals("6401"))
				return "Immediate response required by the card";
			else if (sw1sw2.equals("6402"))
				return "Triggering by the card (see 9.6.1 of ISO7816-4)";
			else if (sw1sw2.equals("6480"))
				return "Triggering by the card (see 9.6.1 of ISO7816-4)";

			//0x6500
			else if (sw1sw2.equals("6500"))
				return "No information given";
			else if (sw1sw2.equals("6581"))
				return "Memory failure";

			//0x6600
			else if (sw1sw2.substring(0,2).equals("66"))
				return "Security-related issues";

			//0x6800
			else if (sw1sw2.equals("6800"))
				return "No information given";
			else if (sw1sw2.equals("6881"))
				return "Logical channel not supported";
			else if (sw1sw2.equals("6882"))
				return "Secure messaging not supported";
			else if (sw1sw2.equals("6883"))
				return "Last command of the chain expected";
			else if (sw1sw2.equals("6884"))
				return "Command chaining not supported";

			//0x6900
			else if (sw1sw2.equals("6900"))
				return "No information given";
			else if (sw1sw2.equals("6981"))
				return "Command incompatible with file structure";
			else if (sw1sw2.equals("6982"))
				return "Security status not satisfied";
			else if (sw1sw2.equals("6983"))
				return "Authentication method blocked";
			else if (sw1sw2.equals("6984"))
				return "Reference data not usable";
			else if (sw1sw2.equals("6985"))
				return "Conditions of use not satisfied";
			else if (sw1sw2.equals("6986"))
				return "Command not allowed (no current EF)";
			else if (sw1sw2.equals("6987"))
				return "Expected secure messaging data objects missing";
			else if (sw1sw2.equals("6988"))
				return "Incorrect secure messaging data objects";

			//0x6A00
			else if (sw1sw2.equals("6A00"))
				return "No information given";
			else if (sw1sw2.equals("6A80"))
				return "Incorrect parameters in the command data field";
			else if (sw1sw2.equals("6A81"))
				return "Function not supported";
			else if (sw1sw2.equals("6A82"))
				return "File or application not found";
			else if (sw1sw2.equals("6A83"))
				return "Record not found";
			else if (sw1sw2.equals("6A84"))
				return "Not enough memory space in the file";
			else if (sw1sw2.equals("6A85"))
				return "Nc inconsistent with TLV structure ";
			else if (sw1sw2.equals("6A86"))
				return "Incorrect parameters P1-P2";
			else if (sw1sw2.equals("6A87"))
				return "Nc inconsistent with parameters P1-P2";
			else if (sw1sw2.equals("6A88"))
				return "Referenced data or reference data not found (exact meaning depending on the command)";
			else if (sw1sw2.equals("6A89"))
				return "File already exists";
			else if (sw1sw2.equals("6A8A"))
				return "DF name already exists";
			else
				return "Sw1Sw2 unidentified yet";
		}

	}

	public final static class BerT extends Iso7816 {
		// tag template
		public static final byte TMPL_FCP = 0x62; // File Control Parameters
		public static final byte TMPL_FMD = 0x64; // File Management Data
		public static final byte TMPL_FCI = 0x6F; // FCP and FMD

		// proprietary information
		public final static BerT CLASS_PRI = new BerT((byte) 0xA5);
		// short EF identifier
		public final static BerT CLASS_SFI = new BerT((byte) 0x88);
		// dedicated file name
		public final static BerT CLASS_DFN = new BerT((byte) 0x84);
		// application data object
		public final static BerT CLASS_ADO = new BerT((byte) 0x61);
		// application id
		public final static BerT CLASS_AID = new BerT((byte) 0x4F);

		public static int test(byte[] bytes, int start) {
			int len = 1;
			if ((bytes[start] & 0x1F) == 0x1F) {
				while ((bytes[start + len] & 0x80) == 0x80)
					++len;

				++len;
			}
			return len;
		}

		public static BerT read(byte[] bytes, int start) {
			return new BerT(Arrays.copyOfRange(bytes, start,
					start + test(bytes, start)));
		}

		public BerT(byte tag) {
			this(new byte[] { tag });
		}

		public BerT(short tag) {
			this(new byte[] { (byte) (0x000000FF & (tag >> 8)),
					(byte) (0x000000FF & tag) });
		}

		public BerT(byte[] bytes) {
			super(bytes);
		}

		public boolean hasChild() {
			return ((data[0] & 0x20) == 0x20);
		}
	}

	public final static class BerL extends Iso7816 {
		private final int val;

		public static int test(byte[] bytes, int start) {
			int len = 1;
			if ((bytes[start] & 0x80) == 0x80) {
				len += bytes[start] & 0x07;
			}
			return len;
		}

		public static int calc(byte[] bytes, int start) {
			if ((bytes[start] & 0x80) == 0x80) {
				int v = 0;

				int e = start + bytes[start] & 0x07;
				while (++start <= e) {
					v <<= 8;
					v |= bytes[start] & 0xFF;
				}

				return v;
			}

			return bytes[start];
		}

		public static BerL read(byte[] bytes, int start) {
			return new BerL(Arrays.copyOfRange(bytes, start,
					start + test(bytes, start)));
		}

		public BerL(byte[] bytes) {
			super(bytes);
			val = calc(bytes, 0);
		}

		public int toInt() {
			return val;
		}
	}

	public final static class BerV extends Iso7816 {
		public static BerV read(byte[] bytes, int start, int len) {
			return new BerV(Arrays.copyOfRange(bytes, start, start + len));
		}

		public BerV(byte[] bytes) {
			super(bytes);
		}
	}

	public final static class BerTLV extends Iso7816 {
		public static int test(byte[] bytes, int start) {
			final int lt = BerT.test(bytes, start);
			final int ll = BerL.test(bytes, start + lt);
			final int lv = BerL.calc(bytes, start + lt);

			return lt + ll + lv;
		}

		public static BerTLV read(Iso7816 obj) {
			return read(obj.getBytes(), 0);
		}

		public static BerTLV read(byte[] bytes, int start) {
			int s = start;
			final BerT t = BerT.read(bytes, s);
			s += t.size();

			final BerL l = BerL.read(bytes, s);
			s += l.size();

			final BerV v = BerV.read(bytes, s, l.toInt());
			s += v.size();

			final BerTLV tlv = new BerTLV(t, l, v);
			tlv.data = Arrays.copyOfRange(bytes, start, s);

			return tlv;
		}

		public static ArrayList<BerTLV> readList(Iso7816 obj) {
			return readList(obj.getBytes());
		}

		public static ArrayList<BerTLV> readList(final byte[] data) {
			final ArrayList<BerTLV> ret = new ArrayList<BerTLV>();

			int start = 0;
			int end = data.length - 3;
			while (start < end) {
				final BerTLV tlv = read(data, start);
				ret.add(tlv);

				start += tlv.size();
			}

			return ret;
		}

		public final BerT t;
		public final BerL l;
		public final BerV v;

		public BerTLV(BerT t, BerL l, BerV v) {
			this.t = t;
			this.l = l;
			this.v = v;
		}

		public BerTLV getChildByTag(BerT tag) {
			if (t.hasChild()) {
				final byte[] raw = v.getBytes();
				int start = 0;
				int end = raw.length;
				while (start < end) {
					if (tag.match(raw, start))
						return read(raw, start);

					start += test(raw, start);
				}
			}

			return null;
		}

		public BerTLV getChild(int index) {
			if (t.hasChild()) {
				final byte[] raw = v.getBytes();
				int start = 0;
				int end = raw.length;

				int i = 0;
				while (start < end) {
					if (i++ == index)
						return read(raw, start);

					start += test(raw, start);
				}
			}

			return null;
		}
	}

	public final static class Tag {
		private final IsoDep nfcTag;
		private ID id;

		public Tag(IsoDep tag) {
			nfcTag = tag;
			id = new ID(tag.getTag().getId());
		}

		public ID getID() {
			return id;
		}

		public Response verify() {
			final byte[] cmd = { (byte) 0x00, // CLA Class
					(byte) 0x20, // INS Instruction
					(byte) 0x00, // P1 Parameter 1
					(byte) 0x00, // P2 Parameter 2
					(byte) 0x02, // Lc
					(byte) 0x12, (byte) 0x34, };

			return new Response(transceive(cmd));
		}

		public Response initPurchase(boolean isEP) {
			final byte[] cmd = {
					(byte) 0x80, // CLA Class
					(byte) 0x50, // INS Instruction
					(byte) 0x01, // P1 Parameter 1
					(byte) (isEP ? 2 : 1), // P2 Parameter 2
					(byte) 0x0B, // Lc
					(byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00,
					(byte) 0x00, (byte) 0x11, (byte) 0x22, (byte) 0x33,
					(byte) 0x44, (byte) 0x55, (byte) 0x66, (byte) 0x0F, // Le
			};

			return new Response(transceive(cmd));
		}

		public Response getBalance(boolean isEP) {
			final byte[] cmd = { (byte) 0x80, // CLA Class
					(byte) 0x5C, // INS Instruction
					(byte) 0x00, // P1 Parameter 1
					(byte) (isEP ? 2 : 1), // P2 Parameter 2
					(byte) 0x04, // Le
			};

			return new Response(transceive(cmd));
		}

		public Response readRecord(int sfi, int index) {
			final byte[] cmd = { (byte) 0x00, // CLA Class
					(byte) 0xB2, // INS Instruction
					(byte) index, // P1 Parameter 1
					(byte) ((sfi << 3) | 0x04), // P2 Parameter 2
					(byte) 0x00, // Le
			};

			return new Response(transceive(cmd));
		}

		public Response readRecord(int sfi) {
			final byte[] cmd = { (byte) 0x00, // CLA Class
					(byte) 0xB2, // INS Instruction
					(byte) 0x01, // P1 Parameter 1
					(byte) ((sfi << 3) | 0x05), // P2 Parameter 2
					(byte) 0x00, // Le
			};

			return new Response(transceive(cmd));
		}

		public Response readBinary(int sfi) {
			final byte[] cmd = { (byte) 0x00, // CLA Class
					(byte) 0xB0, // INS Instruction
					(byte) (0x00000080 | (sfi & 0x1F)), // P1 Parameter 1
					(byte) 0x00, // P2 Parameter 2
					(byte) 0x00, // Le
			};

			return new Response(transceive(cmd));
		}

		public Response readData(int sfi) {
			final byte[] cmd = { (byte) 0x80, // CLA Class
					(byte) 0xCA, // INS Instruction
					(byte) 0x00, // P1 Parameter 1
					(byte) (sfi & 0x1F), // P2 Parameter 2
					(byte) 0x00, // Le
			};

			return new Response(transceive(cmd));
		}

		public Response selectByID(byte... name) {
			ByteBuffer buff = ByteBuffer.allocate(name.length + 6);
			buff.put((byte) 0x00) // CLA Class
			.put((byte) 0xA4) // INS Instruction
			.put((byte) 0x00) // P1 Parameter 1
			.put((byte) 0x00) // P2 Parameter 2
			.put((byte) name.length) // Lc
			.put(name).put((byte) 0x00); // Le

			return new Response(transceive(buff.array()));
		}

		public Response selectByName(byte... name) {
			ByteBuffer buff = ByteBuffer.allocate(name.length + 6);
			buff.put((byte) 0x00) // CLA Class
			.put((byte) 0xA4) // INS Instruction
			.put((byte) 0x04) // P1 Parameter 1
			.put((byte) 0x00) // P2 Parameter 2
			.put((byte) name.length) // Lc
			.put(name).put((byte) 0x00); // Le

			return new Response(transceive(buff.array()));
		}

		public void connect() {
			try {
				nfcTag.connect();
				nfcTag.setTimeout(10000);
			} catch (Exception e) {
			}
		}

		public boolean isConnected(){
			try {
				return nfcTag.isConnected();
			}  catch (Exception e) {}

			return false;
		}

		public void close() {
			try {
				nfcTag.close();
			} catch (Exception e) {
			}
		}

		public byte[] transceive(final byte[] cmd) {

			if (!isValidApdu(cmd))
				return null;

			try {
				return nfcTag.transceive(cmd);
			} catch (Exception e) {
				return Response.ERROR;
			}
		}

		public Response transceive(String cmd) {
			byte[] cmdBytes = Util.stoh(cmd);

			if (!isValidApdu(cmdBytes))
					return null;

			try {
				return new Response(nfcTag.transceive(cmdBytes));
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return null;
		}

		public boolean isValidApdu(final byte[] cmd){

			if (cmd == null || cmd.length < 5)				
				return false;

			return true;
		}
	}

	public static final short SW_NO_ERROR = (short) 0x9000;
	public static final short SW_BYTES_REMAINING_00 = 0x6100;
	public static final short SW_WRONG_LENGTH = 0x6700;
	public static final short SW_SECURITY_STATUS_NOT_SATISFIED = 0x6982;
	public static final short SW_FILE_INVALID = 0x6983;
	public static final short SW_DATA_INVALID = 0x6984;
	public static final short SW_CONDITIONS_NOT_SATISFIED = 0x6985;
	public static final short SW_COMMAND_NOT_ALLOWED = 0x6986;
	public static final short SW_APPLET_SELECT_FAILED = 0x6999;
	public static final short SW_WRONG_DATA = 0x6A80;
	public static final short SW_FUNC_NOT_SUPPORTED = 0x6A81;
	public static final short SW_FILE_NOT_FOUND = 0x6A82;
	public static final short SW_RECORD_NOT_FOUND = 0x6A83;
	public static final short SW_INCORRECT_P1P2 = 0x6A86;
	public static final short SW_WRONG_P1P2 = 0x6B00;
	public static final short SW_CORRECT_LENGTH_00 = 0x6C00;
	public static final short SW_INS_NOT_SUPPORTED = 0x6D00;
	public static final short SW_CLA_NOT_SUPPORTED = 0x6E00;
	public static final short SW_UNKNOWN = 0x6F00;
	public static final short SW_FILE_FULL = 0x6A84;

}

package com.anta40.capuploader;

import java.util.Locale;

public final class Util {
	private final static char[] HEX = { '0', '1', '2', '3', '4', '5', '6', '7',
		'8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

private Util() {
}

public static byte[] toBytes(int a) {
	return new byte[] { (byte) (0x000000ff & (a >>> 24)),
			(byte) (0x000000ff & (a >>> 16)),
			(byte) (0x000000ff & (a >>> 8)), (byte) (0x000000ff & (a)) };
}

public static int toInt(byte[] b, int s, int n) {
	int ret = 0;

	final int e = s + n;
	for (int i = s; i < e; ++i) {
		ret <<= 8;
		ret |= b[i] & 0xFF;
	}
	return ret;
}

public static int toIntR(byte[] b, int s, int n) {
	int ret = 0;

	for (int i = s; (i >= 0 && n > 0); --i, --n) {
		ret <<= 8;
		ret |= b[i] & 0xFF;
	}
	return ret;
}

public static int toInt(byte... b) {
	int ret = 0;
	for (final byte a : b) {
		ret <<= 8;
		ret |= a & 0xFF;
	}
	return ret;
}

public static String toHexString(byte[] d, int s, int n) {
	final char[] ret = new char[n * 2];
	final int e = s + n;

	int x = 0;
	for (int i = s; i < e; ++i) {
		final byte v = d[i];
		ret[x++] = HEX[0x0F & (v >> 4)];
		ret[x++] = HEX[0x0F & v];
	}
	return new String(ret);
}

public static String toHexStringR(byte[] d, int s, int n) {
	final char[] ret = new char[n * 2];

	int x = 0;
	for (int i = s + n - 1; i >= s; --i) {
		final byte v = d[i];
		ret[x++] = HEX[0x0F & (v >> 4)];
		ret[x++] = HEX[0x0F & v];
	}
	return new String(ret);
}

public static int parseInt(String txt, int radix, int def) {
	int ret;
	try {
		ret = Integer.valueOf(txt, radix);
	} catch (Exception e) {
		ret = def;
	}

	return ret;
}

public static String toAmountString(float value) {
	return String.format("%.2f", value);
}

private static String numbers = "0123456789abcdef";

public static byte[] stoh(String s) {
	s = s.replaceAll(" ", "").toLowerCase();
	if (s == null)
		return null;
	if (s.length() % 2 != 0)
		throw new RuntimeException("invalid length");
	byte[] result = new byte[s.length() / 2];
	for (int i = 0; i < s.length(); i += 2) {
		int i1 = numbers.indexOf(s.charAt(i));
		if (i1 == -1)
			throw new RuntimeException("invalid number");
		int i2 = numbers.indexOf(s.charAt(i + 1));
		if (i2 == -1)
			throw new RuntimeException("invalid number");
		result[i / 2] = (byte) ((i1 << 4) | i2);
	}
	return result;
}

public static String htos(byte[] bytes) {
	StringBuffer sb = new StringBuffer();
	for (int i = 0; i < bytes.length; i++) {
		String tmp = Integer.toHexString(((int) bytes[i]) & 0xFF);
		while (tmp.length() < 2)
			tmp = "0" + tmp;
		if (i!=bytes.length-1)
			sb.append(tmp + " ");
		else
			sb.append(tmp);

	}
	return sb.toString().toUpperCase(Locale.ENGLISH);
}

public static String htos2(byte[] bytes) {
	StringBuffer sb = new StringBuffer();
	for (int i = 0; i < bytes.length; i++) {
		String tmp = Integer.toHexString(((int) bytes[i]) & 0xFF);
		while (tmp.length() < 2)
			tmp = "0" + tmp;
		sb.append(tmp);

	}
	return sb.toString().toUpperCase(Locale.ENGLISH);
}

 public static String removeAllWhiteSpace(String rawString) {
        return rawString.replaceAll("\\s", "");
    }

 public static String convertHexToString(String hex){
	 
	 hex = removeAllWhiteSpace(hex);
	 
	  StringBuilder sb = new StringBuilder();
//	  StringBuilder temp = new StringBuilder();
 
	  //49204c6f7665204a617661 split into two characters 49, 20, 4c...
	  for( int i=0; i<hex.length()-1; i+=2 ){
 
	      //grab the hex in pairs
	      String output = hex.substring(i, (i + 2));
	      //convert hex to decimal
	      int decimal = Integer.parseInt(output, 16);
	      //convert the decimal to character
	      sb.append((char)decimal);
 
//	      temp.append(decimal);
	  }
 
	  return sb.toString();
  }

}

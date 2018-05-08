package com.anta40.capuploader.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

public class FileUtil {
	private static String readFileToString( String file ) throws IOException {
	    BufferedReader reader = new BufferedReader( new FileReader (file));
	    String         line = null;
	    StringBuilder  sb = new StringBuilder();
	    String         ls = System.getProperty("line.separator");

	    while( ( line = reader.readLine() ) != null ) {
	        sb.append( line );
	        sb.append( ls );
	    }

	    return sb.toString();
	}
	
	private static String[] readFileToArray(String file) throws IOException {
		BufferedReader reader = new BufferedReader( new FileReader (file));
	    String         line = null;
	    Vector<String> vect = new Vector<String>();

	    while( ( line = reader.readLine() ) != null ) {
	        vect.add(line);
	    }

	    reader.close();
	    return vect.toArray(new String[vect.size()]);
	}
}

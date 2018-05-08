package com.anta40.capuploader;

import java.io.IOException;
import java.io.OutputStream;

import android.widget.EditText;

public class EditTextOutputStream extends OutputStream {
	
	private EditText edit;

	public EditTextOutputStream(EditText edit){
		this.edit = edit;
	}
	
	@Override
	public void write(int oneByte) throws IOException {
		edit.append(String.valueOf((char) oneByte));
	}

	public void write(Object obj){
		edit.append(obj.toString());
	}
	
	public void write(String str){
		edit.append(str);
	}
	
	public void write (byte b){
		edit.append(String.valueOf ((char) b));
	}
	
	public EditText getEditText(){
		return edit;
	}
}

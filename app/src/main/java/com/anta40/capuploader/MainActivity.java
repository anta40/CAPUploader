package com.anta40.capuploader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Vector;

import com.anta40.capuploader.card.CardWorker;
import com.anta40.capuploader.utils.ByteUtils;
import com.anta40.capuploader.utils.CryptoUtil;

import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

	private Button btnSelect, btnDispatcher, btnUpload, btnAuthenticate, btnAPDU;
	private EditText edtCAP, edtAPDU;
	private EditText edtInstallParam;
	private EditText edtKey1, edtKey2, edtKey3, edtConnStat, edtLog;
	private String[][] TECHLISTS;
	public static IntentFilter[] FILTERS;
	private NfcAdapter nfcAdapter;
	private static final int SELECT_CAP_FILE = 0;
	private static final int SELECT_APDU_FILE = 1;
	private IsoDep isodep;
	private Iso7816.Tag isodepCard;
	private PendingIntent pendingIntent;
	private Tag tag;
	private CardWorker cw;
	private String pathToCAPFile;
	private String pathToAPDUFile;
	private CheckBox cbAutoload, cbAPDUOnly;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		btnSelect = (Button) findViewById(R.id.btn_select_cap);
		btnSelect.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				selectCAP();				
			}
		});
		
		btnUpload = (Button) findViewById(R.id.btn_upload);
		btnUpload.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				edtLog.setText("");
				doUploadCAP();
			}
		});
		
		btnAuthenticate = (Button) findViewById(R.id.btn_authenticate);
		btnAuthenticate.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				doAuthenticate();
				
			}
		});
		
		btnDispatcher = (Button) findViewById(R.id.btn_dispatcher);
		btnDispatcher.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showDispatcher(v);
			}
		});
		
		btnAPDU = (Button) findViewById(R.id.btn_select_apdu);
		btnAPDU.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				selectAPDUFile();
			}
		});
		
		TECHLISTS = new String[][] { { IsoDep.class.getName() },};
		
		try {
			FILTERS = new IntentFilter[] { new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED, "*/*") };
		} catch (MalformedMimeTypeException e) {
			e.printStackTrace();
		}
		
		nfcAdapter = NfcAdapter.getDefaultAdapter(this);
				
		edtCAP = (EditText) findViewById(R.id.txt_cap_file);
		edtAPDU = (EditText) findViewById(R.id.txt_apdu_file);
	
		edtInstallParam = (EditText) findViewById(R.id.txt_install_param);
		edtKey1 = (EditText) findViewById(R.id.txt_auth_s_mac);
		edtKey2 = (EditText) findViewById(R.id.txt_auth_s_senc);
		edtKey3 = (EditText) findViewById(R.id.txt_auth_dek);
		edtConnStat = (EditText) findViewById(R.id.txt_conn_status);
		edtLog = (EditText) findViewById(R.id.txt_log);
		
		edtLog.setFocusable(true);
		edtLog.setFocusableInTouchMode(true);
		edtLog.requestFocus();
		
		cbAutoload = (CheckBox) findViewById(R.id.checkbox_autoload);
		cbAutoload.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				cbAPDUOnly.setChecked(false);
				
			}
		});
		
		
		cbAPDUOnly = (CheckBox) findViewById(R.id.checkbox_apdu);
		cbAPDUOnly.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				cbAutoload.setChecked(false);
				
			}
		});
		
		cw = new CardWorker();
		cw.setEditText(edtLog);
		//cw.setIsoDep(isodep);
		
		pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
				getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		
		onNewIntent(getIntent());
	}

	
	private void selectCAP(){
		Intent intent = new Intent();
		intent.setType("*/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, 
				"Select CAP file (*.cap)"), SELECT_CAP_FILE);
	}
	
	private void selectAPDUFile(){
		Intent intent = new Intent();
		intent.setType("*/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, 
				"Select APDU file (*.apdu)"), SELECT_APDU_FILE);
	}
	
	private String[] getAuthKeysArray(){
		String[] tmpArray = new String[3];
		tmpArray[0] = edtKey1.getText().toString();
		tmpArray[1] = edtKey2.getText().toString();
		tmpArray[2] = edtKey3.getText().toString();
		return tmpArray;
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if ((requestCode == SELECT_CAP_FILE) && (resultCode == -1)) {
			Uri uri = data.getData();

			try {
				pathToCAPFile = FileUtils.getPath(this, uri);
				edtCAP.setText(pathToCAPFile);
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
    	}
		else if ((requestCode == SELECT_APDU_FILE) && (resultCode == -1)){
			Uri uri = data.getData();
			try {
				pathToAPDUFile = FileUtils.getPath(this, uri);
				edtAPDU.setText(pathToAPDUFile);
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void showDispatcher(View view){
		Intent intent = new Intent(this, DispatcherActivity.class);
		startActivity(intent);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if (nfcAdapter != null){
			nfcAdapter.enableForegroundDispatch(this, pendingIntent, FILTERS, TECHLISTS);
		}
		
		if (isodepCard != null && !isodepCard.isConnected()){
			edtConnStat.setHint("Disconnected");
		}
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		
		if (isodepCard != null && isodepCard.isConnected()){
			isodepCard.close();
			edtConnStat.setHint("Disconnected");
		}
		else if (isodepCard == null || !isodepCard.isConnected()){
			edtConnStat.setHint("Disconnected");
		}
		
		final Parcelable p = (Parcelable) intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		
		if (p == null)	// not parcelable
			return;

		tag = (Tag) p;		
		isodep = IsoDep.get(tag);		
		isodep.setTimeout(10000);

		if (isodep == null)	// not isodep
		{
			edtConnStat.setHint("Not ISODep card");
			return;
		}
		
		isodepCard = new Iso7816.Tag(isodep);
		isodepCard.connect();
		edtConnStat.setHint("Connected");
	
		if (cbAutoload.isChecked()){
			if (isodep != null){
				doAuthenticate();
				doUploadCAP();
			}
		}
		
		if (cbAPDUOnly.isChecked()){
			if (isodep != null){
				cw.setIsoDep(isodep);
				loadAPDUFile();
			}
		}
		
		setIntent(intent);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if (nfcAdapter != null) nfcAdapter.disableForegroundDispatch(this);
	}
	
	public void doAuthenticate(){
		try {
			cw.setIsoDep(isodep);
			cw.authenticate(getAuthKeysArray(), false);
		}
		catch (Exception exc){
			edtLog.append("Authentication error\n");
		}
		finally {
//			Toast.makeText(getApplicationContext(), "Authentication succesful", 
//					Toast.LENGTH_LONG).show();
			//cw.disconnectCard();
		}
	}
	
	public void loadAPDUFile(){
		int idx = nthLastIndexOf(pathToAPDUFile, '/', 2) + 1;
		String path = pathToAPDUFile.substring(idx);
		String[] commands = readFileToArray(path);
		byte[] cmd, resp;
		
		for (String command:commands){
			cmd = ByteUtils.stoh(command);
			resp = cw.cardTransmit(cmd, 0, cmd.length);
		}
	}
	
	 private String[] readFileToArray(String path) {
			try {
				String tmp = Environment.getExternalStorageDirectory().getAbsolutePath();
				BufferedReader reader = new BufferedReader(new FileReader(new File(
						Environment.getExternalStorageDirectory(),
						path)));
			    String line = null;
			    Vector<String> vect = new Vector<String>();
		
			    while ((line = reader.readLine()) != null ) {
			        vect.add(line);
			    }
		
			    reader.close();
			    return vect.toArray(new String[vect.size()]);
			}
			catch (IOException e){
				return null;
			}
		}
	
	public void doUploadCAP(){
		if (isodep != null && isodep.isConnected()){
			if (edtCAP.getText().toString().isEmpty()){
				Toast.makeText(getApplicationContext(), "Please select CAP file first.", 
						Toast.LENGTH_LONG).show();
				return;
			}
			else {
				try {
					cw = new CardWorker();
					cw.setTextArea(edtLog);
					int idx = nthLastIndexOf(pathToCAPFile, '/', 2) + 1; // penting!!!
					String path = pathToCAPFile.substring(idx);
					File capFile = new File(Environment.getExternalStorageDirectory(), path);
					String[] authKeys = getAuthKeysArray();
					cw.setIsoDep(isodep);
					String installParam = edtInstallParam.getText().toString();
					cw.uploadCAP(capFile, authKeys, false, ByteUtils.stoh(installParam));
//					cw.uploadCAP(capFile, authKeys, false, ByteUtils.stoh("0000"));
				}
				catch (Exception e){
					Toast.makeText(getApplicationContext(), "Uploading CAP error: "+e.getMessage(), 
							Toast.LENGTH_LONG).show();
				}
				finally {
//					Toast.makeText(getApplicationContext(), "Uploading CAP successful", 
//							Toast.LENGTH_LONG).show();
//					if (cw != null) cw.disconnectCard();
				}
			}
		}
		else {
			Toast.makeText(getApplicationContext(), "Please connect to card first.", Toast.LENGTH_LONG).show();
			return;
		}
	}
	
	private int nthLastIndexOf(String str, char c, int n) {
        if (str == null || n < 1) {
        	return -1;
        }
        int pos = str.length();
        while (n-- > 0 && pos != -1){
        	pos = str.lastIndexOf(c, pos - 1);
        }
        return pos;
    }
}

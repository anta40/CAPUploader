package com.anta40.capuploader;

import com.anta40.capuploader.Iso7816.Response;

import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.os.Bundle;
import android.os.Parcelable;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class DispatcherActivity extends Activity {
	
	private Button btnSend, btnClear;
	private EditText edtAPDU, edtOutput, edtStatus;
	private IsoDep isodep;
	private Tag tag;
	private NfcAdapter nfcAdapter;
	private String[][] TECHLISTS;
	private IntentFilter[] FILTERS;
	private Iso7816.Tag isodepCard;
	private PendingIntent pendingIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dispatcher);
		
		TECHLISTS = new String[][] { { IsoDep.class.getName() },};
		
		try {
			FILTERS = new IntentFilter[] { new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED, "*/*") };
		} catch (MalformedMimeTypeException e) {
			e.printStackTrace();
		}
		
		btnSend = (Button) findViewById(R.id.btn_send);
		btnSend.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				sendAPDU(edtAPDU.getText().toString());				
			}
		});
			
		btnClear = (Button) findViewById(R.id.btn_clear_all);
		btnClear.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				edtAPDU.setText("");
				edtOutput.setText("");
			}
		});
		
		edtAPDU = (EditText) findViewById(R.id.txt_input_apdu);
		edtAPDU.setText("00A4040008A000000003000000");
		edtOutput = (EditText) findViewById(R.id.txt_result);
		edtStatus = (EditText) findViewById(R.id.txt_status);
		
		nfcAdapter = NfcAdapter.getDefaultAdapter(this);
		
		if (nfcAdapter == null){
			edtOutput.setHint("NFC not supported");
			return;
		} else if(!nfcAdapter.isEnabled()){
			edtOutput.setHint("NFC is disabled");			
		} else {
			edtOutput.setHint("Put card under your phone");			
		}
		
		pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
				getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

		onNewIntent(getIntent());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.dispatcher, menu);
		return true;
	}

	
	@Override
	protected void onResume() {
		super.onResume();
		
		if (nfcAdapter != null){
			nfcAdapter.enableForegroundDispatch(this, pendingIntent, FILTERS, TECHLISTS);
		}
		
		if (isodepCard != null && !isodepCard.isConnected()){
			edtStatus.setHint("Disconnected");
		}
		
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		if (isodepCard != null && isodepCard.isConnected()){
			isodepCard.close();

			edtStatus.setHint("Disconnected");
		}

		final Parcelable p = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

		if (p == null)	// not parcelable
			return;

		tag = (Tag) p;		
		isodep = IsoDep.get(tag);		

		if (isodep == null)	// not isodep
		{
			edtStatus.setHint("Not ISODep card");
			return;
		}

		isodepCard = new Iso7816.Tag(isodep);
		isodepCard.connect();

		edtStatus.setText("Connected\n");
		String humanReadable = "";
		if (isodep.getHiLayerResponse() != null){
			humanReadable = " (" +Util.convertHexToString(Util.htos(isodep.getHiLayerResponse())) +")";
			
			edtStatus.append("Type B Card! ISOdep Info: " + Util.htos(isodep.getHiLayerResponse())  + "\n");

			NfcB nfcbCard = NfcB.get(tag);

			edtStatus.append("Protocol Info: " + Util.htos(nfcbCard.getProtocolInfo())  + "\n");
			edtStatus.append("Application Data: " + Util.htos(nfcbCard.getApplicationData())  + "\n");
		}

		if (isodep.getHistoricalBytes()!= null) {
			humanReadable = " (" +Util.convertHexToString(Util.htos(isodep.getHistoricalBytes())) +")";
			
			edtStatus.append("Type A Card! ISOdep  Info: " + Util.htos(isodep.getHistoricalBytes()) + humanReadable + "\n");
			
			NfcA nfcaCard = NfcA.get(tag);

			edtStatus.append("ATQA Info: " + Util.htos(nfcaCard.getAtqa())  + "\n");
			edtStatus.append("SAK: " + nfcaCard.getSak() + "\n");
			
			sendAPDU(null);
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if (nfcAdapter != null) nfcAdapter.disableForegroundDispatch(this);
	}
	
	public void sendAPDU(String cmd) {
		if (isodepCard == null || !isodepCard.isConnected()){
			edtOutput.setText("");
			edtOutput.setHint("Card not connected");
			edtStatus.setText("Disconnected");
			return;
		}

		if (isodepCard.isConnected()){

			String multilines = edtAPDU.getText().toString();
			String delimiter = "\n";		
			String[] cmds = multilines.split(delimiter);

			edtOutput.setText("");
			for (int i = 0 ; i< cmds.length; i++){

				Response resp = isodepCard.transceive(cmds[i]);

				if (resp != null) {
					edtOutput.append("=> " + cmds[i] + "\n");
					edtOutput.append("<= " + resp.toString() + " ("+ resp.appendSw1Sw2Meaning() + ") "+ "\n\n");
				}
			}
		} else {
			edtOutput.setText("");
			edtOutput.setHint("Card not connected");
			edtStatus.setText("Disconnected");
		}
	}
}

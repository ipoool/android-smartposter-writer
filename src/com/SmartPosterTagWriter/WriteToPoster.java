package com.SmartPosterTagWriter;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.TagLostException;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class WriteToPoster extends Activity implements Runnable {

	WebView 				myWebView;
	TextView 				myTextView;
	ImageView				myImageView;

	Tag						mTagFromIntent	= null;
	MifareClassic			mClassic		= null;
	private NfcAdapter		mAdapter;
	private PendingIntent	mPendingIntent;

	String[][]				mTechLists		= new String[][] { new String[] { MifareClassic.class.getName() } };
	String 					url;

	/**
	 * This method is called when the activity is first created.
	 **/
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.writer);

		View view = this.getWindow().getDecorView();
		view.setBackgroundColor(100);

		setPoster();

		mAdapter = NfcAdapter.getDefaultAdapter(this);
		mPendingIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

		createTag(getIntent());	
	}

	private void setPoster()
	{
		Intent intent = getIntent();
		url = intent.getStringExtra("url"); //if it's a string you stored.

		myWebView = (WebView) findViewById(R.id.webview);
		myWebView.getSettings().setLoadWithOverviewMode(true);
		myWebView.getSettings().setUseWideViewPort(true);
		if(!url.toString().contains("http://")) {
			url="http://"+url;
		}
		myWebView.setWebViewClient(new WebViewClientSubClass());
		myWebView.loadUrl(url);

		myTextView = (TextView) findViewById(R.id.textView);
		myTextView.setText(url + "...");

		myImageView = (ImageView) findViewById(R.id.overlay);
		myImageView.setImageAlpha(150);
	}

	private void createTag(final Intent intent) {
		final Thread thread = new Thread(this);

		mTagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

		if (mTagFromIntent != null) {
			mClassic = MifareClassic.get(mTagFromIntent);

			if (mClassic != null) {
				/* Start the thread to read the DESFire */
				thread.start();
			}
		}
	}

	/** Called on thread.start() */
	@Override
	public void run() {
		writePoster();
		/* Open dialog from handleMessage */
		handler.sendEmptyMessage(1);
	}

	private final Handler	handler	= new Handler(new Handler.Callback() {
		/* Handles the Message queue */
		@Override
		public boolean handleMessage(final Message msg) {

			AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(WriteToPoster.this);
			myAlertDialog.setMessage("Smart Poster will be stored on this tag!");
			
			myAlertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface arg0, int arg1) {
					finish();
				}
			});

			myAlertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface arg0, int arg1) {
					// do something when the Cancel button is clicked
				}
			});

			myAlertDialog.show();

			return true;
		}
	});	

	private void writePoster() 
	{
		boolean succes = false;
		try {
			/* Connect to the Tag */
			mClassic.connect();

			/* Authenticate the MAD Sector, sector 0, with key A */
			succes = mClassic.authenticateSectorWithKeyA(0, MifareClassic.KEY_MIFARE_APPLICATION_DIRECTORY );

			if (succes) {
				/* Authentication succeeded proceed to write */
			}
			else {
				/* Authentication failed */				
			}

			mClassic.close();
		}

		/* Catch the TagLostException */
		catch (final TagLostException tag) {
			tag.printStackTrace();
		}
		
		/* Catch the IOException */
		catch (final IOException e) {
			e.printStackTrace();
		}

		String text = url;		
		writeRecord(1, text);		
	}

	/**
	 * This method is used to write "URI record only" to NDEF
	 */
	private void writeRecord(int recordId, String text) {
		NdefRecord uriRecord = NdefRecord.createUri(url);
		NdefMessage  message = new NdefMessage(uriRecord);

		// Get an instance of Ndef for the tag.
		Ndef ndef = Ndef.get(mTagFromIntent);

		// Enable I/O
		try {
			ndef.connect();
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		// Write the message
		try {
			ndef.writeNdefMessage(message);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		// Close the connection
		try {
			ndef.close();		
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** Called when a new tag is in the NFC-field */
	@Override
	protected void onNewIntent(final Intent intent) {
		Toast.makeText(this, "Tag Detected", Toast.LENGTH_SHORT).show();
		createTag(intent);
	}

	@Override
	public void onPause() {
		overridePendingTransition(R.anim.hold, R.anim.push_out_to_left);
		super.onPause();		
		mAdapter.disableForegroundDispatch(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		mAdapter.enableForegroundDispatch(this, mPendingIntent, null, mTechLists);
	}
	
	//web browser client
	private class WebViewClientSubClass extends WebViewClient {
	    @Override
	    public boolean shouldOverrideUrlLoading(WebView view, String url) {
	        view.loadUrl(url);
	        return true;
	    }
	}
}

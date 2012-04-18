package com.VoiceRuralCDN;

//import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.*;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class VoiceRecog extends Activity implements OnClickListener{
	
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
    TextView tv,v1,v2,v3;
    Button b;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.interfaces);
        tv = (TextView) findViewById( R.id.tv1);
        b = (Button) findViewById(R.id.b2);
        v1 = (TextView) findViewById(R.id.up);
        v2 = (TextView) findViewById(R.id.down);
        v3 = (TextView) findViewById(R.id.conf);
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() != 0) {
        	b.setOnClickListener(this);
        	v1.setOnClickListener(this);
        	v2.setOnClickListener(this);
        	v3.setOnClickListener(this);
        } else {
            tv.setText("Recognizer not present");
        }
        
        ConnectivityManager conMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

      //mobile
     android.net.NetworkInfo.State mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();

      //wifi
      android.net.NetworkInfo.State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
      
      if (mobile == NetworkInfo.State.CONNECTED || mobile == NetworkInfo.State.CONNECTING) {
    	    //mobile
    	} else if (wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING) {
    	    //wifi
    	}
    	else{
    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Internet not enabled. App works with GPRS or wifi");
        builder.setInverseBackgroundForced(true);
        builder.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
        	  Intent enableBtIntent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
  			startActivityForResult(enableBtIntent, 1);
          }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
          }
        });
        AlertDialog alert = builder.create();
        alert.show();
    	}
    }

	private void startVoiceRecognitionActivity() {
	    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
	    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
	            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
	    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speech recognition demo");
	    startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
	}
	
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.b2)
			startVoiceRecognitionActivity();
		if (v.getId() == R.id.up)
			upload(v);
		if (v.getId() == R.id.down)
			download(v);
		if (v.getId() == R.id.conf)
			conference(v);
	}

	public void upload (View v){
		startActivity(new Intent(this,Upload.class));
	}
	
	public void download(View v){
		startActivity(new Intent(this,TabOptions.class));
	}
	
	public void conference (View v){
		startActivity(new Intent(this,Conference.class));
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
	        // Fill the list view with the strings the recognizer thought it could have heard
	        ArrayList<String> matches = data.getStringArrayListExtra(
	                RecognizerIntent.EXTRA_RESULTS);
	        String heard = matches.get(0);
	        if (heard.equalsIgnoreCase("upload"))
	        	startActivity(new Intent(this,Upload.class));
	        else if (heard.contains("download"))
	        	startActivity(new Intent(this,TabOptions.class));
	        else if (heard.contains("conference"))
	        	startActivity(new Intent(this,Conference.class));
	        else 
	        	tv.setText("You said "+heard+" : Not a recognized command");//startActivity(new Intent(this,Download.class));
	    }
	}
}
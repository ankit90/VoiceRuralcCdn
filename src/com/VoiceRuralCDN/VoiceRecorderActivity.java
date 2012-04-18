package com.VoiceRuralCDN;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import android.app.Activity;
import android.database.Cursor;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import java.util.Random;

public class VoiceRecorderActivity extends Activity implements OnClickListener {
    /** Called when the activity is first created. */
	
	MediaRecorder recorder ;
	Socket socket = null;
    DataOutputStream dataOutputStream = null;
	Button rec,recstop,send;
	TextView tv;
	String mFileName,name;
	Random r = new Random();
	private NotesDbAdapter mDbHelper;
    private Cursor mNotesCursor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recorder);
        rec = (Button) findViewById(R.id.StartRecord);
        recstop = (Button) findViewById(R.id.StopRecord);
        send = (Button) findViewById(R.id.SendRecord);
        tv = (TextView) findViewById(R.id.RecordResult);
        Bundle s = getIntent().getExtras();
        mFileName = s.getString("RecordComment");
        name = mFileName;
        rec.setOnClickListener(this);
        recstop.setOnClickListener(this);
        send.setOnClickListener(this);
    }
    
    public void record(View v) throws IllegalStateException, IOException{
    	recorder=new MediaRecorder();
    	recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
    	recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
    	//mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "_" + r.nextInt() +".3gp";
    	recorder.setOutputFile(mFileName);
    	recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
    	try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e("Audio Recorder Test", "prepare() failed");
        }
    	recorder.start(); 
    }
    
    public void stop(View v){
    	recorder.stop();
    	recorder.release();
    	recorder = null;
    	tv.setText("saved voice comment as " +mFileName);
    }

    public void send(){
    	String [] temp = name.split("/");
    	String [] temp1 = mFileName.split("/");
    	File myFile = new File (mFileName);
    	try{
			socket = new Socket("192.168.1.185", 2004);
			dataOutputStream = new DataOutputStream(socket.getOutputStream());
		  	String msg =  "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root>" +
		  				  "<Message><size>1</size><fileName>"+"f"+"</fileName>" +
		  				  "<Title>"+temp[temp.length-1]+"</Title><Desc>"+temp1[temp1.length-1]+"</Desc>"+
	           			  "<Tags>"+(int)myFile.length()+"</Tags><Time_stamp>"+"t"+"</Time_stamp>"+
	                      "<Conference_stamp>"+"c"+"</Conference_stamp>"
	                      +"<Function>voice</Function>" + "</Message></root>\n";
		    
		    dataOutputStream.writeBytes(msg);
		    dataOutputStream.flush();
		    
		    byte [] mybytearray  = new byte [(int)myFile.length()];
	        FileInputStream fis = new FileInputStream(myFile);
	        BufferedInputStream bis = new BufferedInputStream(fis);
	        bis.read(mybytearray,0,mybytearray.length);

	        dataOutputStream.write(mybytearray,0,mybytearray.length);
	        
	        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
	        tv.setText(dataInputStream.readLine());

			dataOutputStream.close();
			dataInputStream.close();
			socket.close();
		} catch (Exception e) {
			  // TODO Auto-generated catch block
		 }
		
		mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();
        mNotesCursor =mDbHelper.searchName(temp[temp.length-1]);
        if(mNotesCursor!=null && mNotesCursor.getCount()!=0){
        	if (mNotesCursor.getString(6).equalsIgnoreCase("default"))
        	mDbHelper.updateNote(mNotesCursor.getInt(0), mNotesCursor.getString(1),
        						 mNotesCursor.getString(2), mNotesCursor.getString(3),
        						 temp1[temp1.length-1], mNotesCursor.getString(5)
        						 ,mNotesCursor.getString(6),mNotesCursor.getString(7));
        	else
        		mDbHelper.updateNote(mNotesCursor.getInt(0), mNotesCursor.getString(1),
						 mNotesCursor.getString(2), mNotesCursor.getString(3),
						 mNotesCursor.getString(4) + "~"+temp1[temp1.length-1], mNotesCursor.getString(5)
						 ,mNotesCursor.getString(6),mNotesCursor.getString(7));
        }
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.StartRecord:
			try {
				record(v);
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case R.id.StopRecord:
			stop(v);
			break;
		case R.id.SendRecord:
			send();
			break;
		}
	}
}
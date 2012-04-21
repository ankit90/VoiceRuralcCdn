package com.VoiceRuralCDN;

import java.io.*;
import java.net.Socket;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

public class DisplayInfo extends Activity{
	
	TextView tv;
	Socket socket = null;
    DataOutputStream dataOutputStream = null;
    DataInputStream dataInputStream = null;
    String title,desc,tags; Button download;
    private NotesDbAdapter mDbHelper;
    String message ="";
    private Cursor mNotesCursor;
	    /** Called when the activity is first created. */
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.info);
	        Bundle s = getIntent().getExtras();
	        title = s.getString("TITLE");
	        desc = s.getString("DESC");
	        tags = s.getString("TAGS");
	        ((TextView)findViewById(R.id.text1)).setText(title);
	        ((TextView)findViewById(R.id.text2)).setText(desc);
	        ((TextView)findViewById(R.id.text3)).setText(tags);
	        tv =(TextView)findViewById(R.id.tata);
	        download = (Button)findViewById(R.id.button1);
	        
	        download.setOnClickListener(buttonSendOnClickListener);
	        mDbHelper = new NotesDbAdapter(this);
	        mDbHelper.open();
	    }
	    
		Button.OnClickListener buttonSendOnClickListener
		 = new Button.OnClickListener(){

		@Override
		public void onClick(View arg0) {
		 // TODO Auto-generated method stub
			mNotesCursor = mDbHelper.searchName(title);
			final String type =mNotesCursor.getString(5);
			final int rowid= mNotesCursor.getInt(0);
			final String comments=mNotesCursor.getString(4);
			final String audio=mNotesCursor.getString(6);
			mDbHelper.updateNote(rowid, title, desc, tags, comments, "3",audio,mNotesCursor.getString(7));
			new Thread(new Runnable() {
		        public void run() {    
		        	download(type,rowid,comments,audio);
		        }
		    }).start();
		 
		}};
		
		
		public void download(String type,int rowid,String comments,String audio){
		
			if(type.equalsIgnoreCase("0") || type.equalsIgnoreCase("3"))
			{
			try {
				String file=Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+title;
				File f = new File(file);
				long d=0;
				if (f.exists())
					d=f.length();
				desc = d+"";
				socket = new Socket("192.168.1.185", 2004);
				dataOutputStream = new DataOutputStream(socket.getOutputStream());
			  	dataInputStream = new DataInputStream(socket.getInputStream());
			  	String msg =  "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root>" +
			  				  "<Message><size>1</size><fileName>"+"f"+"</fileName>" +
			  				  "<Title>"+title+"</Title><Desc>"+desc+"</Desc>"+
		           			  "<Tags>"+tags+"</Tags><Time_stamp>"+"t"+"</Time_stamp>"+
		                      "<Conference_stamp>"+"c"+"</Conference_stamp>"
		                      +"<Function>download</Function>" + "</Message></root>\n";
			    dataOutputStream.writeBytes(msg);
			    dataOutputStream.flush();
			    String rec = dataInputStream.readLine();
			    long size = Long.parseLong(rec);
			    int filesize = 5242880;
			    FileOutputStream fos;
                if (f.exists())
                    fos = new FileOutputStream(file,true);
                else
                    fos = new FileOutputStream(file);
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                long t=0;
                int bytesRead;
    		    int current = 0;
    		    byte [] mybytearray;
                long loop = (size-d)/filesize;
                long rem = (size-d)%filesize;
                while(t<loop){
                    mybytearray  = new byte [filesize];
                    bytesRead = dataInputStream.read(mybytearray,0,filesize);
				    current = bytesRead;
				    do {
				      if(current>=filesize)
				        break;
				       bytesRead =
				    	   dataInputStream.read(mybytearray, current, (mybytearray.length-current));
				       if(bytesRead >= 0) current += bytesRead;
				    } while(bytesRead > -1);
				    bos.write(mybytearray, 0 , current);
				    bos.flush();
				    t++;
				    }
                if(rem!=0){
                    mybytearray  = new byte [filesize];
                    bytesRead = dataInputStream.read(mybytearray,0,filesize);
				    current = bytesRead;
				    do {
				      if(current>=size-t*filesize-d)
				        break;
				       bytesRead =
				    	   dataInputStream.read(mybytearray, current, (mybytearray.length-current));
				       if(bytesRead >= 0) current += bytesRead;
				    } while(bytesRead > -1);
                    bos.write(mybytearray, 0 , current);
				    bos.flush();
                }
                dataOutputStream.writeBytes("File " + title +" received succesfully.\n");
                dataOutputStream.flush();
                bos.close();
                message = "Video downloaded successfully";
				mDbHelper.updateNote(rowid, title, desc, tags, comments, "1",audio,mNotesCursor.getString(7));
				dataInputStream.close();
				dataOutputStream.close();
				socket.close();
				
			} catch (Exception e) {
				  // TODO Auto-generated catch block
				 //tv.setText(e.getMessage()+" Some connection Problem");
			 }
			}
			else{
				message="Video already Downloaded";
			}
			
			
			runOnUiThread(new Runnable() {
	            public void run() {
	            	Toast.makeText(DisplayInfo.this, message, Toast.LENGTH_LONG).show();
	            }
	        });
			
		}
		public void displayfinal(View v,String message)
		{
			Intent foo = new Intent(this,VideoDownloaded.class);
			foo.putExtra("message", message);
	    	startActivity(foo);
		}
		
		 @Override
		    protected void onDestroy() {
		        super.onDestroy();
		        mDbHelper.close();

		    }
		    @Override
		    protected void onPause() {
		        super.onPause();
		        mDbHelper.close();

		    }
		    @Override
		    protected void onStop() {
		        super.onStop();
		        mDbHelper.close();

		    }
}

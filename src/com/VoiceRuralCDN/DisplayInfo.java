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
import android.view.View;

public class DisplayInfo extends Activity{
	
	TextView tv;
	Socket socket = null;
    DataOutputStream dataOutputStream = null;
    DataInputStream dataInputStream = null;
    String title,desc,tags; Button download;
    private NotesDbAdapter mDbHelper;
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
			String message ="";
			mNotesCursor = mDbHelper.searchName(title);
			String type =mNotesCursor.getString(5);
			int rowid= mNotesCursor.getInt(0);
			String comments=mNotesCursor.getString(4);
			String audio=mNotesCursor.getString(6);
			if(type.equalsIgnoreCase("0"))
			{
			try {
				
				socket = new Socket("192.168.1.185", 2004);
				dataOutputStream = new DataOutputStream(socket.getOutputStream());
			  	dataInputStream = new DataInputStream(socket.getInputStream());
			  	String msg =  "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root>" +
			  				  "<Message><size>1</size><fileName>"+"f"+"</fileName>" +
			  				  "<Title>"+title+"</Title><Desc>"+desc+"</Desc>"+
		           			  "<Tags>"+tags+"</Tags><Time_stamp>"+"t"+"</Time_stamp>"+
		                      "<Conference_stamp>"+"c"+"</Conference_stamp>"
		                      +"<Function>download</Function>" + "</Message></root>\n";
			  	//textIn.setText(dataInputStream.readUTF());
			    
			    dataOutputStream.writeBytes(msg);
			    dataOutputStream.flush();
			    String rec = dataInputStream.readLine();
			    
			    int size = Integer.parseInt(rec);
			    int filesize = 5242880;
			    if (size<=filesize)
			    {
			    	//download.setText(rec);
			    	int bytesRead;
				    int current = 0;
				    byte [] mybytearray  = new byte [filesize];
				    try{
//				    	InputStream is = connection.getInputStream();
				    	String file=Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+title;
					    FileOutputStream fos = new FileOutputStream(file);
					    BufferedOutputStream bos = new BufferedOutputStream(fos);
					    bytesRead = dataInputStream.read(mybytearray,0,filesize);
					    current = bytesRead;
					    do {
					      //System.out.println("neww");
					      if(current>=size)
					        break;
					       bytesRead =
					          dataInputStream.read(mybytearray, current, (mybytearray.length-current));
					       if(bytesRead >= 0) current += bytesRead;
					       //System.out.println("dsd");
					    } while(bytesRead > -1);
					    bos.write(mybytearray, 0 , current);
					    bos.flush();
                        //System.out.println("hereeeee011");
                        dataOutputStream.writeBytes("File " + title +" received succesfully.\n");
                        dataOutputStream.flush();
                        bos.close();
                        message = "Video downloaded successfully";
					    //connection.close();
				    }catch(IOException Exception){
						tv.setText(Exception.getMessage());
					}
				    
				    mDbHelper.updateNote(rowid, title, desc, tags, comments, "1",audio,mNotesCursor.getString(7));
			    }
			    else
			    	{
			    		message = "Video will be sent through USB keys";
			    	}
				dataInputStream.close();
				dataOutputStream.close();
				socket.close();
				
			} catch (Exception e) {
				  // TODO Auto-generated catch block
				 tv.setText(e.getMessage()+" Some connection Problem");
			 }
			}
			else{
				message="Video already Downloaded";
			}
			displayfinal(arg0,message);
		 
		}};
		
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

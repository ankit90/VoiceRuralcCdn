package com.VoiceRuralCDN;

import java.io.*;
import java.net.Socket;
import java.util.Properties;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class ViewVoice extends ListActivity{
	Socket socket = null;
    DataOutputStream dataOutputStream = null;
    DataInputStream dataInputStream = null;
    String file;//,filename;
    String [] comments;
    private NotesDbAdapter mDbHelper;
    private Cursor mNotesCursor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voice_list);
        Bundle s = getIntent().getExtras();
        String videocomments = s.getString("VoiceComments");
//        filename = s.getString("filename");
        comments = videocomments.split("~");
//        strings = new String[comments.length];
//        if(!comments[0].equals(""))
//        for(int i=0;i<comments.length;i++){
//        	strings[i] = comments[i].substring(0,comments[i].length()-2);
//        }
        setListAdapter(new ArrayAdapter(this,R.layout.voice_row,comments));
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        String selection = l.getItemAtPosition(position).toString();
    	File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+selection);
    	if (!f.exists())
    	{
	    	try{
	    		Resources resources = this.getResources();
				AssetManager assetManager = resources.getAssets();
				// Read from the /assets directory
				InputStream in = assetManager.open("config.properties");
				Properties properties = new Properties();
				properties.load(in);
				final int filesize = Integer.parseInt(properties.getProperty("SizeLimit"));
				socket = new Socket(properties.getProperty("ServerIp"),Integer.parseInt(properties.getProperty("ServerPort")));
				dataOutputStream = new DataOutputStream(socket.getOutputStream());
			  	dataInputStream = new DataInputStream(socket.getInputStream());
			  	String msg =  "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root>" +
			  				  "<Message><size>1</size><fileName>"+"f"+"</fileName>" +
			  				  "<Title>"+selection+"</Title><Desc>d</Desc>"+
		           			  "<Tags>t</Tags><Time_stamp>"+"t"+"</Time_stamp>"+
		                      "<Conference_stamp>"+"c"+"</Conference_stamp>"
		                      +"<Function>voice_download</Function>" + "</Message></root>\n";
			  	dataOutputStream.writeBytes(msg);
			  	dataOutputStream.flush();
			  	int size = Integer.parseInt(dataInputStream.readLine());
			  	int bytesRead;
			    int current = 0;
			    byte [] mybytearray  = new byte [filesize];
			    	file=Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+selection;
				    FileOutputStream fos = new FileOutputStream(file);
				    BufferedOutputStream bos = new BufferedOutputStream(fos);
				    bytesRead = dataInputStream.read(mybytearray,0,filesize);
				    current = bytesRead;
				    do {
				      if(current>=size)
				        break;
				       bytesRead =
				          dataInputStream.read(mybytearray, current, (mybytearray.length-current));
				       if(bytesRead >= 0) current += bytesRead;
				    } while(bytesRead > -1);
				    bos.write(mybytearray, 0 , current);
				    bos.flush();
	                bos.close();
	                dataInputStream.close();
					dataOutputStream.close();
					socket.close();
	    	}catch(IOException Exception){
			}

//	    	mDbHelper = new NotesDbAdapter(this);
//	        mDbHelper.open();
//	        mNotesCursor =mDbHelper.searchName(filename);
//	        String temp1="";
//	        if(mNotesCursor!=null && mNotesCursor.getCount()!=0){
//	        	temp1 = mNotesCursor.getString(6);
//	        }
//	        String [] com1 = temp1.split("~");
//	        if(!com1[0].equals(""))
//	        for(int i=0;i<com1.length;i++){
//	        	if(com1[i].equalsIgnoreCase(strings[i] + " 0")){
//	        		com1[i] = strings[i] + " 1";
//	        		break;
//	        	}
//	        }
//	        temp1="";
//	        if(!com1[0].equals(""))
//	        for(int i=0;i<com1.length;i++){
//	        	temp1 = temp1+com1[i];
//	        }
//	        mDbHelper.updateNote(mNotesCursor.getInt(0), mNotesCursor.getString(1), mNotesCursor.getString(2), mNotesCursor.getString(3),
//	        					mNotesCursor.getString(4), mNotesCursor.getString(5), temp1, mNotesCursor.getString(7));
    	}
    	else
    	{
    		file=Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+comments[position];
    	}

    	Intent intent = new Intent(this,playComment.class);
    	intent.putExtra("Voicename", file);
    	startActivity(intent);
    }

}
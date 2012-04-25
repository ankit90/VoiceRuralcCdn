package com.VoiceRuralCDN;

import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.Properties;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
    int servport=0;
    String server="";
    private Cursor mNotesCursor;
	    /** Called when the activity is first created. */
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        mDbHelper = new NotesDbAdapter(this);
	        mDbHelper.open();
	        setContentView(R.layout.info);
	        Bundle s = getIntent().getExtras();
	        title = s.getString("TITLE");
	        desc = s.getString("DESC");
	        tags = s.getString("TAGS");
	        ((TextView)findViewById(R.id.text1)).setText(title);
	        ((TextView)findViewById(R.id.text2)).setText(desc);
	        ((TextView)findViewById(R.id.text3)).setText(tags);
	        ((TextView)findViewById(R.id.text4)).setText(s.getString("CONF"));
	        String comments=s.getString("COMM");
	        String [] arr = comments.split("~");
	        String comm="";
	        for(int i=0;i<arr.length;i++){
	        	if(i==0)
	        		comm=arr[i];
	        	else
	        		comm=comm+"\n"+arr[i];
	        }
	        	
	        ((TextView)findViewById(R.id.text5)).setText(comm);
	        tv =(TextView)findViewById(R.id.tata);
	        download = (Button)findViewById(R.id.button1);
	        
	        download.setOnClickListener(buttonSendOnClickListener);
	        
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
			tv.setText("Your video has been queued for download !!");
			
			if(getNetwork()==true){
			new Thread(new Runnable() {
		        public void run() {    
		        	download(type,rowid,comments,audio);
		        }
		    }).start();
			}
			
			String time=mNotesCursor.getString(7);
        	String [] arr=time.split(" ");
        	String [] arr1=arr[0].split(":");
        	String [] arr2=arr[1].split("-");
        	
//        	Date d = new Date(Integer.parseInt(arr2[2]),
//        						Integer.parseInt(arr2[1]),
//        						Integer.parseInt(arr2[0]),
//        						Integer.parseInt(arr1[0]),
//        						Integer.parseInt(arr1[1]));
//			Intent intent = new Intent(Intent.ACTION_EDIT);
//	        intent.setType("vnd.android.cursor.item/event");
//	        intent.putExtra("beginTime", d.getSeconds()*1000);
//	        intent.putExtra("allDay", true);
//	        intent.putExtra("rrule", "FREQ=YEARLY");
//	        intent.putExtra("endTime", (d.getSeconds()*1000)+(60*60*1000));
//	        intent.putExtra("title", "Conference for Video : "+title);
//	        startActivity(intent);
		}};
		
		public boolean getNetwork(){

			 ConnectivityManager conMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		     android.net.NetworkInfo.State mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();

		      android.net.NetworkInfo.State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		      
		      if (mobile == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTED) {
		    	  return true;
		      }
		      else
		    	  return false;
			
		}
		public void download(String type,int rowid,String comments,String audio){
		
			try {		
			if(type.equalsIgnoreCase("0") || type.equalsIgnoreCase("3"))
			{
				
				String file=Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+title;
				File f = new File(file);
				String descc="";
				long d=0;
				if (f.exists())
					d=f.length();
				descc = d+"";
				
				Resources resources = this.getResources();
				AssetManager assetManager = resources.getAssets();
				// Read from the /assets directory
				InputStream in = assetManager.open("config.properties");
				Properties properties = new Properties();
				properties.load(in);
				final int filesize = Integer.parseInt(properties.getProperty("SizeLimit"));
				socket = new Socket(properties.getProperty("ServerIp"),Integer.parseInt(properties.getProperty("ServerPort")));
				if(socket==null)
				{
					runOnUiThread(new Runnable() {
			            public void run() {
			            	Toast.makeText(DisplayInfo.this, "No Network Connection", Toast.LENGTH_LONG).show();
			            }
			        });
				}
				else{
				dataOutputStream = new DataOutputStream(socket.getOutputStream());
			  	dataInputStream = new DataInputStream(socket.getInputStream());
			  	String msg =  "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root>" +
			  				  "<Message><size>1</size><fileName>"+"f"+"</fileName>" +
			  				  "<Title>"+title+"</Title><Desc>"+descc+"</Desc>"+
		           			  "<Tags>"+tags+"</Tags><Time_stamp>"+"t"+"</Time_stamp>"+
		                      "<Conference_stamp>"+"c"+"</Conference_stamp>"
		                      +"<Function>download</Function>" + "</Message></root>\n";
			    dataOutputStream.writeBytes(msg);
			    dataOutputStream.flush();
			    String rec = dataInputStream.readLine();
			    long size = Long.parseLong(rec);
			    boolean flag=opportunistic_networking(size,(long)filesize);
			    dataOutputStream.writeBoolean(flag);
			    dataOutputStream.flush();
			    if(flag==true){
			    	
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
				mDbHelper.updateNote(rowid, title, desc, tags, comments, "1",audio,mNotesCursor.getString(7));
				dataInputStream.close();
				dataOutputStream.close();
				socket.close();
				runOnUiThread(new Runnable() {
		            public void run() {
		            	Toast.makeText(DisplayInfo.this, "Video downloaded successfully", Toast.LENGTH_LONG).show();
		            }
		        });
			   }
			    else{
			    	dataInputStream.close();
					dataOutputStream.close();
					socket.close();
			    }
			  }
			}
			else{
				runOnUiThread(new Runnable() {
		            public void run() {
		            	Toast.makeText(DisplayInfo.this, "Video already Downloaded", Toast.LENGTH_LONG).show();
		            }
		        });
			}
			
			} catch (final Exception e) {
				  // TODO Auto-generated catch block
				 //tv.setText(e.getMessage()+" Some connection Problem");
				runOnUiThread(new Runnable() {
		            public void run() {
		            	Toast.makeText(DisplayInfo.this, e.getMessage(), Toast.LENGTH_LONG).show();
		            }
		        });
			 }
			
		}
		public boolean opportunistic_networking(long filesize,long limit){
			
			 ConnectivityManager conMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		      //mobile
		     android.net.NetworkInfo.State mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();

		      //wifi
		      android.net.NetworkInfo.State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		      
		      if (mobile == NetworkInfo.State.CONNECTED) {
		    	    //mobile
		    	  if(filesize<limit)
		    		  return true;
		    	  else
		    		  return false;
		    	  
		    	} else if (wifi == NetworkInfo.State.CONNECTED) {
		    	    //wifi
		    		return true;
		    	}
		    	else{
		    		return false;
		    	}
		      
		}
		public void displayfinal(View v,String message)
		{
			Intent foo = new Intent(this,VideoDownloaded.class);
			foo.putExtra("message", message);
	    	startActivity(foo);
		}
		
		
}

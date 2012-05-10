package com.VoiceRuralCDN;

//import java.net.NetworkInterface;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.*;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class VoiceRecog extends Activity implements OnClickListener{
	
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
    TextView tv,v1,v2,v3,v4;
    Button b;
    private NotesDbAdapter mDbHelper;
    private int SharedDB=0;
    private int SharedUpload=0;
    private int SharedDownload=0;
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menutext:
            	startActivity(new Intent(this,Settings.class));
                break;
        }
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.interfaces);
        
        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();
        tv = (TextView) findViewById( R.id.tv1);
        b = (Button) findViewById(R.id.b2);
        v1 = (TextView) findViewById(R.id.up);
        v2 = (TextView) findViewById(R.id.down);
        v3 = (TextView) findViewById(R.id.conf);
        v4 = (TextView) findViewById(R.id.capture);
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() != 0) {
        	b.setOnClickListener(this);
        	v1.setOnClickListener(this);
        	v2.setOnClickListener(this);
        	v3.setOnClickListener(this);
        	v4.setOnClickListener(this);
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
      
      
    if(getNetwork()){
    	
      if(SharedDB==0){
    	new Thread(new Runnable() {
	        public void run() {    
//	        	String schema=getDBschema();
//	            syncSchema(schema);
	        	SharedDB=1;
	  				db();
	  			SharedDB=0;
	        }
	    }).start();
      }
     if(SharedUpload==0){ 
      new Thread(new Runnable() {
	        public void run() {    
	        	SharedUpload=1;
	        	upload_thread();
	        	SharedUpload=0;
	            
	        }
	    }).start();
     }
     if(SharedDownload==0){ 
      new Thread(new Runnable() {
	        public void run() {    
	        	SharedDownload=1;
	        	download_thread();
	        	SharedDownload=0;
	            
	        }
	    }).start();
     }
    }
    
      
    }
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
    private void upload_thread(){
    	try {
    		Cursor c = mDbHelper.getUploadQueue();
    		
    		if(c.moveToFirst()){
    			
    			int count =c.getCount();
		    	
    			while(count > 0){
    				
		    			Socket socket = null;
		    		    DataOutputStream dataOutputStream = null;
		    		    DataInputStream dataInputStream = null;
						final int rowid= c.getInt(0);
						final String Video_name = c.getString(1); 
						String Video_desc = c.getString(2);
						String Video_tags = c.getString(3);
						final String Video_path=c.getString(4);
						final String type =c.getString(5);
						final String audio=c.getString(6);
						String time = c.getString(7);
						
						Resources resources = this.getResources();
						AssetManager assetManager = resources.getAssets();
						// Read from the /assets directory
						InputStream in = assetManager.open("config.properties");
						Properties properties = new Properties();
						properties.load(in);
						final int filesize = Integer.parseInt(properties.getProperty("SizeLimit"));
						
								Date d = new Date();   
		    	  	
								String mFileName =Video_path ;
								File myFile = new File (mFileName);
								long totalsize = myFile.length();
			    		 	
								boolean flag= opportunistic_networking(totalsize,(long)filesize);
								
								if(flag==true)
								{
									SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
									
									final String server=pref.getString("ip", "192.168.1.185");//properties.getProperty("ServerIp");
									final String port =pref.getString("port","2004");//Integer.parseInt(properties.getProperty("ServerPort"));
									
									socket = new Socket(server,Integer.parseInt(port));
						    		
									
									if(socket==null){
						    			runOnUiThread(new Runnable() {
						    	            public void run() {
						    	            	Toast.makeText(VoiceRecog.this,"No Network Connection", Toast.LENGTH_LONG).show();
						    	            }
						    	        });
									}
									else{
				    		 	String msg =  "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root>" +
				    			  "<Message><size>0</size><fileName>"+Video_name+"</fileName>" +
				    			  "<Title>"+myFile.length()+"</Title><Desc>"+Video_desc+"</Desc>"+
				     			  "<Tags>"+Video_tags+"</Tags><Time_stamp>"+d.toString()+"</Time_stamp>"+
				                "<Conference_stamp>"+time+"</Conference_stamp>"
				                +"<Function>upload</Function>" +"</Message></root>\n";
				    		 	dataOutputStream = new DataOutputStream(socket.getOutputStream());
				    	        dataInputStream = new DataInputStream(socket.getInputStream());
				    	    	dataOutputStream.writeBytes(msg);
				    		    dataOutputStream.flush();
				    		    long downloaded_size = Long.parseLong(dataInputStream.readLine());
				    		    long loop = (totalsize-downloaded_size)/(long)filesize;
				    		    long rem = (totalsize-downloaded_size)%(long)filesize;
				    		    FileInputStream fis = new FileInputStream(myFile);
				    		    BufferedInputStream bis = new BufferedInputStream(fis);
				    		    long l = downloaded_size/(long)filesize;
				    		    long r = downloaded_size%(long)filesize;
				    		    long t1=0;
				    		    while(t1<l){
				    		    	byte [] my  = new byte [filesize];
				    			    bis.read(my,0,my.length);
				    			    t1++;
				    		    }
				    		    if(r != 0){
				    		    	byte [] my  = new byte [(int)(downloaded_size-t1*filesize)];
				    			    bis.read(my,0,my.length);
				    		    }
				    		    long t=0;
				    		    while(t<loop){
				    		    	byte [] mybytearray  = new byte [filesize];
				    		    	bis.read(mybytearray,0,mybytearray.length);
				    		        dataOutputStream.write(mybytearray,0,mybytearray.length);
				    		        dataOutputStream.flush();
				    		        t++;
				    		    }
				    		    if(rem != 0){
				    		    	byte [] mybytearray  = new byte [(int)(totalsize-downloaded_size-loop*filesize)];
				    		    	bis.read(mybytearray,0,mybytearray.length);
				    		        dataOutputStream.write(mybytearray,0,mybytearray.length);
				    		        dataOutputStream.flush();
				    		    }
				
				    		    String confirmation=(dataInputStream.readLine());
				    			mDbHelper.updateNote(rowid, Video_name,Video_desc, Video_tags, "default", "0", "default",
				    					 time);
				    	        
				    	        
				    	        runOnUiThread(new Runnable() {
				    	            public void run() {
				    	            	Toast.makeText(VoiceRecog.this, Video_name+" - has been Uploaded" , Toast.LENGTH_LONG).show();
				    	            }
				    	        });
							}
				    	    }
								dataInputStream.close();
				    			dataOutputStream.close();
				    	        socket.close();
				    	        
				    	        c.moveToNext();
					    		count--;
					    		//Thread.sleep(100000);      
    			}
		    		
					
		    		
		    		}
    		
    	 } catch (final Exception e) {
    		 runOnUiThread(new Runnable() {
    	            public void run() {
    	            	Toast.makeText(VoiceRecog.this, e.getMessage(), Toast.LENGTH_LONG).show();
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
	      SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);		        
		  String option1=pref.getString("list1","2");//properties.getProperty("ServerIp");
    if (mobile == NetworkInfo.State.CONNECTED) {
	    	    //mobile
	    	  
	    	  
	    	  if(filesize < limit)
	    		  return true;
	    	  else if( filesize >= limit && option1.equals("1")){
	    		  return true;
	    	  }
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
    private void download_thread(){
    	try {
			
    		Cursor c = mDbHelper.getDownloadQueue();
    		
    		if(c.moveToFirst()){
    			
    			int count = c.getCount();
    			
    			while(count > 0){
	    			Socket socket = null;
	    		    DataOutputStream dataOutputStream = null;
	    		    DataInputStream dataInputStream = null;
					final int rowid= c.getInt(0);
					String title = c.getString(1); 
					String desc = c.getString(2);
					String tags = c.getString(3);
					final String comments=c.getString(4);
					final String type =c.getString(5);
					final String audio=c.getString(6);
					
					String file=Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+title;
					File f = new File(file);
					long d=0;
					String descc="";
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
						SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
				        
						String server=pref.getString("ip", "192.168.1.185");//properties.getProperty("ServerIp");
						String port =pref.getString("port","2004");//Integer.parseInt(properties.getProperty("ServerPort"));
						socket = new Socket(server,Integer.parseInt(port));
						if(socket==null)
						{
							runOnUiThread(new Runnable() {
					            public void run() {
					            	Toast.makeText(VoiceRecog.this, "No Network Connection", Toast.LENGTH_LONG).show();
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
					    boolean flag= opportunistic_networking(size,(long)filesize);
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
								mDbHelper.updateNote(rowid, title, desc, tags, comments, "1",audio,c.getString(7));
								
								runOnUiThread(new Runnable() {
						            public void run() {
						            	Toast.makeText(VoiceRecog.this, "Video downloaded successfully", Toast.LENGTH_LONG).show();
						            }
						        });
					   }
					    
					    	dataInputStream.close();
							dataOutputStream.close();
							socket.close();
					    
					  }
					
					count--;
					c.moveToNext();
					//Thread.sleep(10000);
					
    				}
    				}
					} catch (Exception e) {
						  // TODO Auto-generated catch block
						 //tv.setText(e.getMessage()+" Some connection Problem");
						runOnUiThread(new Runnable() {
				            public void run() {
				            	Toast.makeText(VoiceRecog.this, "Network Problem", Toast.LENGTH_LONG).show();
				            }
				        });
					 }
    }
	private void startVoiceRecognitionActivity() {
	    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
	    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
	            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
	    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speech recognition demo");
	    startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
	}
    
    public void db(){
    	
    	try {
    	InputStream is = null;
		JSONObject jObj = null;
		String json = "";
		//Toast.makeText(VoiceRecog.this, "In db", Toast.LENGTH_LONG).show();
		
	
			// defaultHttpClient
    		
    		Resources resources = this.getResources();
			AssetManager assetManager = resources.getAssets();
			// Read from the /assets directory
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
			String server=pref.getString("ip", "192.168.1.185");//properties.getProperty("ServerIp");
		
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost("http://"+server+"/script.php");

			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();			

				BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			json = sb.toString();

			JSONArray newarray = new JSONArray(json);
			for(int i = 0; i < newarray.length(); i++){
				
				JSONObject c = newarray.getJSONObject(i);
				String title = c.getString("title");
				String desc = c.getString("desc");
				String tags = c.getString("tags");
				String comments = c.getString("comments");
				String conf = c.getString("conference_time");
				String audio = c.getString("audio_comments");
				
				Cursor cur =mDbHelper.searchName(title);
	    		if(!cur.moveToFirst()){
	    				mDbHelper.createNote(title,desc,tags, comments,"0",audio,conf);
	    		}
	    		else if(cur!=null && (cur.getString(5).equalsIgnoreCase("1")||
	    				cur.getString(5).equalsIgnoreCase("0"))){
	    			mDbHelper.updateNote(cur.getLong(0),title,desc,tags,comments,cur.getString(5),audio,conf);
	    		}
				
			}

			
		} catch (final Exception e) {
			
			runOnUiThread(new Runnable() {
	            public void run() {
	            	Toast.makeText(VoiceRecog.this, e.getMessage(), Toast.LENGTH_LONG).show();
	            }
	        });
			
		}

		
		

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
		if (v.getId() == R.id.capture)
			capture(v);
	}

	private void capture(View v) {
		// TODO Auto-generated method stub
		startActivity(new Intent(this,Capture.class));
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
	        else if (heard.contains("capture"))
	        	startActivity(new Intent(this,Capture.class));
	        else 
	        	tv.setText("You said "+heard+" : Not a recognized command");//startActivity(new Intent(this,Download.class));
	    }
	}
}
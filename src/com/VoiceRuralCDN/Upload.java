package com.VoiceRuralCDN;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.*;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import java.io.*;
import java.net.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;


//import com.browsing.R;
//import java.sql.Connection;


import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


public class Upload extends Activity implements OnClickListener {

	EditText name;
	EditText tags;
	EditText desc;
	EditText path;
	private String Video_name;
	private String Video_tags;
	private String Video_desc;
	private String Video_path;
	TextView textIn;
	
    private TextView mDateDisplay,home;
    private NotesDbAdapter mDbHelper;
    private int mYear;
    private int mMonth;
    private int mDay;

    static final int DATE_DIALOG_ID = 0;
    private TextView mTimeDisplay;
    Socket socket = null;
    DataOutputStream dataOutputStream = null;
    DataInputStream dataInputStream = null;

    private int mHour;
    private int mMinute;

    static final int TIME_DIALOG_ID = 1;
    private Button mStartActivityButton;
    private static final int REQUEST_PICK_FILE = 1;
    String path1="";

  
    private File selectedFile;
	/** Called when the activity is first created. */


	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.begin);
        mDbHelper = new NotesDbAdapter(this);
	    mDbHelper.open();
        name = (EditText)findViewById(R.id.name);
        tags = (EditText)findViewById(R.id.tags);
        desc = (EditText)findViewById(R.id.body);
        //path = (EditText)findViewById(R.id.path);
        mStartActivityButton = (Button)findViewById(R.id.browse);
        Button buttonSend = (Button)findViewById(R.id.send);
        home = (TextView)findViewById(R.id.button1);
        textIn = (TextView)findViewById(R.id.textin);
        buttonSend.setOnClickListener(buttonSendOnClickListener);
        home.setOnClickListener(this);
        mStartActivityButton.setOnClickListener(this);  
     // capture our View elements
        mDateDisplay = (TextView) findViewById(R.id.date);
        

        // add a click listener to the button
        mDateDisplay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });

        // get the current date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        // capture our View elements
        mTimeDisplay = (TextView) findViewById(R.id.time);
        

        // add a click listener to the button
        mTimeDisplay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(TIME_DIALOG_ID);
            }
        });

        // get the current time
        final Calendar c1 = Calendar.getInstance();
        mHour = c1.get(Calendar.HOUR_OF_DAY);
        mMinute = c1.get(Calendar.MINUTE);

        // display the current date (this method is below)
        updateDisplay();
        

    }

	@Override
		public void onClick(View v) {
        switch(v.getId()) {
        case R.id.browse:
            Intent intent = new Intent(this, FilePickerActivity.class);
            startActivityForResult(intent,REQUEST_PICK_FILE);
            break;
        case R.id.button1:
            Intent intent1 = new Intent(this, VoiceRecog.class);
            startActivity(intent1);//,REQUEST_PICK_FILE);
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            switch(requestCode) {
            case REQUEST_PICK_FILE:
                if(data.hasExtra(FilePickerActivity.EXTRA_FILE_PATH)) {
                    // Get the file path
                    selectedFile = new File(data.getStringExtra(FilePickerActivity.EXTRA_FILE_PATH));
                    // Set the file path text view
                    path1=selectedFile.getPath();
                    //textIn.setText(path1);  
                    //Now you have your selected file, You can do your additional requirement with file.                
                }
            }
        }
    }
	
private void updateDisplay() {
    mDateDisplay.setText(
        new StringBuilder()
                // Month is 0 based so add 1
                .append(mMonth + 1).append("-")
                .append(mDay).append("-")
                .append(mYear).append(" "));
    mTimeDisplay.setText(
            new StringBuilder()
                    .append(pad(mHour)).append(":")
                    .append(pad(mMinute)));

}

private static String pad(int c) {
    if (c >= 10)
        return String.valueOf(c);
    else
        return "0" + String.valueOf(c);
}

//the callback received when the user "sets" the time in the dialog
private TimePickerDialog.OnTimeSetListener mTimeSetListener =
    new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mHour = hourOfDay;
            mMinute = minute;
            updateDisplay();
        }
    };
	

    // the callback received when the user "sets" the date in the dialog
private DatePickerDialog.OnDateSetListener mDateSetListener =
        new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, 
                                  int monthOfYear, int dayOfMonth) {
                mYear = year;
                mMonth = monthOfYear;
                mDay = dayOfMonth;
                updateDisplay();
            }
        };
	
@Override
protected Dialog onCreateDialog(int id) {
    switch (id) {
    case DATE_DIALOG_ID:
        return new DatePickerDialog(this,
                    mDateSetListener,
                    mYear, mMonth, mDay);
    case TIME_DIALOG_ID:
        return new TimePickerDialog(this,
                mTimeSetListener, mHour, mMinute, false);
    }
    return null;
}   
	
public void upload(//String Video_name,String Video_tags,String Video_desc,String Video_path,
		String time){
	try {
		
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
		
		
		Date d = new Date();   
	  	if(Video_path.equalsIgnoreCase(""))
	    {
	  		socket = new Socket(server,Integer.parseInt(port));
	  		if(socket==null){
				runOnUiThread(new Runnable() {
		            public void run() {
		            	Toast.makeText(Upload.this,"No Network Connection", Toast.LENGTH_LONG).show();
		            }
		        });
			}
			else{
	  		dataOutputStream = new DataOutputStream(socket.getOutputStream());
		  	dataInputStream = new DataInputStream(socket.getInputStream());
		  	String msg =  "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root>" +
		  				  "<Message><size>1</size><fileName>"+Video_name+"</fileName>" +
		  				  "<Title>"+Video_name+"</Title><Desc>"+Video_desc+"</Desc>"+
	           			  "<Tags>"+Video_tags+"</Tags><Time_stamp>"+d.toString()+"</Time_stamp>"+
	                      "<Conference_stamp>"+time+"</Conference_stamp>"
	                      +"<Function>upload</Function>" + "</Message></root>\n";
		  	//textIn.setText(dataInputStream.readUTF());
		    
		    dataOutputStream.writeBytes(msg);
		    dataOutputStream.flush();
		    
		   
			String confirmation=(dataInputStream.readLine());
			Cursor c = mDbHelper.searchName(Video_name);
			mDbHelper.updateNote(c.getInt(0), Video_name,Video_desc, Video_tags, "default", "0", "default",
					 time);
				
			dataInputStream.close();
			dataOutputStream.close();
			socket.close();
			
			runOnUiThread(new Runnable() {
	            public void run() {
	            	Toast.makeText(Upload.this, Video_name+" - Will be Uploaded by USB" , Toast.LENGTH_LONG).show();
	            }
	        });
			}
	    }
	    else
	    {
	    	String mFileName =Video_path ;
		 	File myFile = new File (mFileName);
		 	long totalsize = myFile.length();
		 	
		 	boolean flag= opportunistic_networking(totalsize,(long)filesize);
		 	if(flag==true)
		 	{
		 		socket = new Socket(server,Integer.parseInt(port));
		  		if(socket==null){
					runOnUiThread(new Runnable() {
			            public void run() {
			            	Toast.makeText(Upload.this,"No Network Connection", Toast.LENGTH_LONG).show();
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
			Cursor c = mDbHelper.searchName(Video_name);
			mDbHelper.updateNote(c.getInt(0), Video_name,Video_desc, Video_tags, "default", "0", "default",
					 time);
	        
	        dataInputStream.close();
			dataOutputStream.close();
			//os.close();
	        socket.close();
	        runOnUiThread(new Runnable() {
	            public void run() {
	            	Toast.makeText(Upload.this, Video_name+" - has been Uploaded" , Toast.LENGTH_LONG).show();
	            }
	        });
			}
	    	}
	      }
	    
	   
	 } catch (final Exception e) {
		 runOnUiThread(new Runnable() {
	            public void run() {
	            	Toast.makeText(Upload.this, e.getMessage(), Toast.LENGTH_LONG).show();
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
    Button.OnClickListener buttonSendOnClickListener
	 = new Button.OnClickListener(){

	@Override
	public void onClick(View arg0) {
	 // TODO Auto-generated method stub
		//upload();
		Video_name=name.getText().toString();
		if(Video_name.equals("")){
			textIn.setText("Please Specify a File name!!");
		}
		else{
	    Video_tags=tags.getText().toString();
	    if(Video_tags.equals("")){
			Video_tags="Default";
		}
	    Video_desc=desc.getText().toString();
	    if(Video_desc.equals("")){
			Video_desc="Default";
		}
	    Video_path=path1;
		 mDbHelper.createNote(Video_name,Video_desc, Video_tags, Video_path, "2", "default",
				 mHour+":"+mMinute+" "+mDay+"-"+(mMonth+1)+"-"+mYear);
		 textIn.setText("Your Content has been Queued for Upload");
		 
		 if(getNetwork()){
		 new Thread(new Runnable() {
		        public void run() {    
		        	upload(//Video_name,Video_tags,Video_desc,Video_path,
		        						mHour+":"+mMinute+" "+mDay+"-"+(mMonth+1)+"-"+mYear);
		        }
		    }).start();
		 }
		}
		 	//Calendar cal = Calendar.getInstance();
//		 	Date d1 = new Date(mYear,mMonth,mDay,mHour,mMinute);
//	        Intent intent = new Intent(Intent.ACTION_EDIT);
//	        intent.setType("vnd.android.cursor.item/event");
//	        intent.putExtra("beginTime",d1.getSeconds()*1000);
//	        intent.putExtra("allDay", true);
//	        intent.putExtra("rrule", "FREQ=YEARLY");
//	        intent.putExtra("endTime", (d1.getSeconds()*1000)+(60*60*1000));
//	        intent.putExtra("title", "Conference for Video : "+Video_name);
//	        startActivity(intent);
		 
	}};
	
}
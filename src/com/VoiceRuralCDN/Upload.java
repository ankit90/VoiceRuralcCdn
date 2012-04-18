package com.VoiceRuralCDN;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;

import java.io.*;
import java.net.*;
import java.util.Calendar;
import java.util.Date;


//import com.browsing.R;
//import java.sql.Connection;


import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;


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
	Socket socket = null;
    DataOutputStream dataOutputStream = null;
    DataInputStream dataInputStream = null;
    private TextView mDateDisplay;
    private int mYear;
    private int mMonth;
    private int mDay;

    static final int DATE_DIALOG_ID = 0;
    private TextView mTimeDisplay;
 

    private int mHour;
    private int mMinute;

    static final int TIME_DIALOG_ID = 1;
    private Button mStartActivityButton,home;
    private static final int REQUEST_PICK_FILE = 1;
    String path1="";

    private TextView mFilePathTextView;
    private File selectedFile;
	/** Called when the activity is first created. */


	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.begin);
        
        name = (EditText)findViewById(R.id.name);
        tags = (EditText)findViewById(R.id.tags);
        desc = (EditText)findViewById(R.id.body);
        //path = (EditText)findViewById(R.id.path);
        mStartActivityButton = (Button)findViewById(R.id.browse);
        Button buttonSend = (Button)findViewById(R.id.send);
        home = (Button)findViewById(R.id.button1);
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
                    textIn.setText(path1);  
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



    Button.OnClickListener buttonSendOnClickListener
	 = new Button.OnClickListener(){

	@Override
	public void onClick(View arg0) {
	 // TODO Auto-generated method stub
	
	 try {
	    Video_name=name.getText().toString();
	    Video_tags=tags.getText().toString();
	    Video_desc=desc.getText().toString();
	    Video_path=path1;
	    
	    socket = new Socket("192.168.1.185", 2004);
		if(socket==null)
			textIn.setText(" Could Not Connect to the host");
		else{
	    Date d = new Date();   
	  	if(Video_path.equalsIgnoreCase(""))
	    {
	  		dataOutputStream = new DataOutputStream(socket.getOutputStream());
		  	dataInputStream = new DataInputStream(socket.getInputStream());
		  	String msg =  "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root>" +
		  				  "<Message><size>1</size><fileName>"+Video_name+"</fileName>" +
		  				  "<Title>"+Video_name+"</Title><Desc>"+Video_desc+"</Desc>"+
	           			  "<Tags>"+Video_tags+"</Tags><Time_stamp>"+d.toString()+"</Time_stamp>"+
	                      "<Conference_stamp>"+mHour+":"+mMinute+" "+mDay+"-"+mMonth+"-"+mYear+"</Conference_stamp>"
	                      +"<Function>upload</Function>" + "</Message></root>\n";
		  	//textIn.setText(dataInputStream.readUTF());
		    
		    dataOutputStream.writeBytes(msg);
		    dataOutputStream.flush();
		    textIn.setText(dataInputStream.readLine());
				
				dataInputStream.close();
				dataOutputStream.close();
				socket.close();
	    
	    }
	    else
	    {
	    	String mFileName =Video_path ;//Environment.getExternalStorageDirectory().getAbsolutePath();
	        //mFileName += "/"+Video_path;
	        //textIn.setText("Name "+mFileName);
		 	File myFile = new File (mFileName);
		 	String msg =  "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root>" +
			  "<Message><size>0</size><fileName>"+Video_name+"</fileName>" +
			  "<Title>"+(int)myFile.length()+"</Title><Desc>"+Video_desc+"</Desc>"+
 			  "<Tags>"+Video_tags+"</Tags><Time_stamp>"+d.toString()+"</Time_stamp>"+
            "<Conference_stamp>"+mHour+":"+mMinute+" "+mDay+"-"+mMonth+"-"+mYear+"</Conference_stamp>"
            +"<Function>upload</Function>" +"</Message></root>\n";
		    
	        byte [] mybytearray  = new byte [(int)myFile.length()];
	        FileInputStream fis = new FileInputStream(myFile);
	        BufferedInputStream bis = new BufferedInputStream(fis);
	        bis.read(mybytearray,0,mybytearray.length);
	        
	        dataOutputStream = new DataOutputStream(socket.getOutputStream());
	        dataInputStream = new DataInputStream(socket.getInputStream());
	        dataOutputStream.writeBytes(msg);
		    dataOutputStream.flush();
		    //textIn.setText(dataInputStream.readLine());
		    //OutputStream os = socket.getOutputStream();
	        //tecd xtIn.setText("Sending...");
	        dataOutputStream.write(mybytearray,0,mybytearray.length);
	        //dataOutputStream.writeBytes("\n");
	        //textIn.setText("Sent...");
	        dataOutputStream.flush();
	        textIn.setText(dataInputStream.readLine());
	        dataInputStream.close();
			dataOutputStream.close();
			//os.close();
	        socket.close();
	    }
	   }
	 } catch (Exception e) {
	  // TODO Auto-generated catch block
		 textIn.setText(e.getMessage()+" Some connection Problem");
	 }
	}};
	
}
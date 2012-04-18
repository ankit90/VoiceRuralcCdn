package com.VoiceRuralCDN;

import android.app.Activity;
import java.io.*;
import java.net.Socket;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class Comments extends Activity implements OnClickListener{
	private Button recordComment,playButton,voiceComment,comments,submit,back;
	private EditText edittext;
	Socket socket = null;
    DataOutputStream dataOutputStream = null;
	String name="";
	private NotesDbAdapter mDbHelper;
    private Cursor mNotesCursor;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle s = getIntent().getExtras();
        name = s.getString("videofilename");
        setContentView(R.layout.comments);
        
        recordComment = (Button) findViewById(R.id.record);
        playButton = (Button) findViewById(R.id.play);
        voiceComment = (Button) findViewById(R.id.voice);
        comments = (Button) findViewById(R.id.comm);
        submit = (Button) findViewById(R.id.submit);
        back = (Button) findViewById(R.id.back);
        
        edittext = (EditText) findViewById(R.id.text);
        
        recordComment.setOnClickListener(this);
        playButton.setOnClickListener(this);
        voiceComment.setOnClickListener(this);
        comments.setOnClickListener(this);
        submit.setOnClickListener(this);
        back.setOnClickListener(this);
        
	}
	
	@Override
	public void onClick(View v) {
	 // TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.record:
			record();
			break;
        case R.id.play:
            // Create a new Intent for the file picker activity
            Intent intent = new Intent(this, Play.class);
            intent.putExtra("filename1", name);
            startActivity(intent);
            break;
        case R.id.voice:
        	viewvoice();
			break;
        case R.id.comm:
        	 viewcomment();
			break;
        case R.id.submit:
        	comment();
            break;
        case R.id.back:
        	startActivity(new Intent(this,VoiceRecog.class));
            break;
	}
	}
	
	private void comment(){
		String comment = edittext.getText().toString();
		if (!comment.equalsIgnoreCase("")){
		String [] temp = name.split("/");
		try{
			socket = new Socket("192.168.1.185", 2004);
			dataOutputStream = new DataOutputStream(socket.getOutputStream());
		  	String msg =  "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root>" +
		  				  "<Message><size>1</size><fileName>"+"f"+"</fileName>" +
		  				  "<Title>"+temp[temp.length-1]+"</Title><Desc>"+comment+"</Desc>"+
	           			  "<Tags>t</Tags><Time_stamp>"+"t"+"</Time_stamp>"+
	                      "<Conference_stamp>"+"c"+"</Conference_stamp>"
	                      +"<Function>comment</Function>" + "</Message></root>\n";
		  	//textIn.setText(dataInputStream.readUTF());
		    
		    dataOutputStream.writeBytes(msg);
		    dataOutputStream.flush();
			dataOutputStream.close();
			socket.close();
		} catch (Exception e) {
			  // TODO Auto-generated catch block
		 }
		mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();
        mNotesCursor =mDbHelper.searchName(temp[temp.length-1]);
        if(mNotesCursor!=null && mNotesCursor.getCount()!=0){
        	if (mNotesCursor.getString(4).equalsIgnoreCase("default"))
        	mDbHelper.updateNote(mNotesCursor.getInt(0), mNotesCursor.getString(1),
        						 mNotesCursor.getString(2), mNotesCursor.getString(3),
        						 comment, mNotesCursor.getString(5)
        						 ,mNotesCursor.getString(6),mNotesCursor.getString(7));
        	else
        		mDbHelper.updateNote(mNotesCursor.getInt(0), mNotesCursor.getString(1),
						 mNotesCursor.getString(2), mNotesCursor.getString(3),
						 mNotesCursor.getString(4) + "~"+comment, mNotesCursor.getString(5)
						 ,mNotesCursor.getString(6),mNotesCursor.getString(7));
        }
        edittext.setText("");
		}
	}
	
	private void viewcomment(){
		String comment="hello";
		mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();
        String [] temp = name.split("/");
        mNotesCursor =mDbHelper.searchName(temp[temp.length-1]);
        if(mNotesCursor!=null && mNotesCursor.getCount()!=0){
        	comment = mNotesCursor.getString(4);
        }
		Intent foo = new Intent(this,ViewComment.class);
		foo.putExtra("VideoComments", comment);
		foo.putExtra("filename", temp[temp.length-1]);
		startActivity(foo);
		
	}
	
	private void record(){
		Intent foo = new Intent(this,VoiceRecorderActivity.class);
		foo.putExtra("RecordComment", name);
		startActivity(foo);
	}
	
	private void viewvoice(){
		String comment="default";
		mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();
        String [] temp = name.split("/");
        mNotesCursor =mDbHelper.searchName(temp[temp.length-1]);
        if(mNotesCursor!=null && mNotesCursor.getCount()!=0){
        	comment = mNotesCursor.getString(6);
        }
		Intent foo = new Intent(this,ViewVoice.class);
		foo.putExtra("VoiceComments", comment);
		startActivity(foo);
	}
}

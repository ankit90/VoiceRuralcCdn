package com.VoiceRuralCDN;

import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class Conference extends ListActivity{
	final Calendar c = Calendar.getInstance();
    int mYear = c.get(Calendar.YEAR);
    int mMonth = c.get(Calendar.MONTH)+1;
    int mDay = c.get(Calendar.DAY_OF_MONTH);
    int mHour = c.get(Calendar.HOUR_OF_DAY);
    int mMinute = c.get(Calendar.MINUTE);
    private NotesDbAdapter mDbHelper;
    private Cursor mNotesCursor;
    Vector <String> v = new Vector<String>();
	String[] videoFileList;
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conf_list);
        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();
        mNotesCursor = mDbHelper.getDownloaded();
        

        if(mNotesCursor.moveToFirst())
        {
        	int rows = mNotesCursor.getCount();
        	//Toast.makeText(this, mYear + " "+mMonth + " "+mDay + " "+mHour + " "+mMinute , Toast.LENGTH_LONG).show();
        	while(rows>0){
        
        	String time=mNotesCursor.getString(7);
        	String [] arr=time.split(" ");
        	String [] arr1=arr[0].split(":");
        	String [] arr2=arr[1].split("-");
        	
        	Date d = new Date(Integer.parseInt(arr2[2]),
        						Integer.parseInt(arr2[1]),
        						Integer.parseInt(arr2[0]),
        						Integer.parseInt(arr1[0]),
        						Integer.parseInt(arr1[1]));
        	Date d1 = new Date(mYear,mMonth,mDay,mHour,mMinute);
        	
        	
        	//arr2[2] +" " + arr2[1] +" " +arr2[0] +" "+arr1[0] +" "+arr1[1]
        	int sec = (int)(((d1.getTime()-d.getTime()))/(1000*60));
        	if (sec <60 && sec >=0)
        		{
        			v.add(mNotesCursor.getString(1));
        			//Toast.makeText(this, "Got one", Toast.LENGTH_LONG).show();
        		}
        	rows--;
        	mNotesCursor.moveToNext();
        }
        }
        videoFileList = new String[v.size()];
        for(int i=0;i<v.size();i++)
        	videoFileList[i] = v.get(i);
//        
        setListAdapter(new ArrayAdapter(this,R.layout.conf_row,videoFileList));
	}
	@Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		String selection = l.getItemAtPosition(position).toString();
		//Toast.makeText(this, selection, Toast.LENGTH_LONG).show();
		Intent foo = new Intent(this,Options.class);
		startActivity(foo);
		
	}
}

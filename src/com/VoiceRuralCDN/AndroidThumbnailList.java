package com.VoiceRuralCDN;

import java.util.Vector;


import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore.Video.Thumbnails;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AndroidThumbnailList extends ListActivity{
	
	private NotesDbAdapter mDbHelper;
    private Cursor mNotesCursor;
    Vector <String> v = new Vector<String>();
	String[] videoFileList;
	public class MyThumbnaildapter extends ArrayAdapter<String>{

		 public MyThumbnaildapter(Context context, int textViewResourceId,
		   String[] objects) {
		  super(context, textViewResourceId, objects);
		  // TODO Auto-generated constructor stub
		 }

		 @Override
		 public View getView(final int position, View convertView, ViewGroup parent) {
		  // TODO Auto-generated method stub
		 
		  View row = convertView;
		  if(row==null){
		   LayoutInflater inflater=getLayoutInflater();
		   row=inflater.inflate(R.layout.row, parent, false);
		  }
		 
		  TextView textfilePath = (TextView)row.findViewById(R.id.FilePath);
		  textfilePath.setText(videoFileList[position]);
		  ImageView imageThumbnail = (ImageView)row.findViewById(R.id.Thumbnail);
		 
		  Bitmap bmThumbnail;
		        bmThumbnail = ThumbnailUtils.createVideoThumbnail(videoFileList[position], Thumbnails.MICRO_KIND);
		        imageThumbnail.setImageBitmap(bmThumbnail);
		        
		        row.setOnClickListener(new OnClickListener() {

		            public void onClick(View arg0) {
		                    Intent intent = new Intent(AndroidThumbnailList.this, Comments.class);
		                    intent.putExtra("videofilename", videoFileList[position]);
		                    startActivity(intent);
		                    }
		            });
		 
		  return row;
		 }

		}

		  /** Called when the activity is first created. */
		  @Override
		  public void onCreate(Bundle savedInstanceState) {
		      super.onCreate(savedInstanceState);
		      mDbHelper = new NotesDbAdapter(this);
		      mDbHelper.open();
		      mNotesCursor = mDbHelper.searchType("1");
		      int rows = mNotesCursor.getCount();
		      while(rows>0)
		      {
		    	  v.add(mNotesCursor.getString(1));
		    	  mNotesCursor.moveToNext();
		    	  rows--;
		      }
		      videoFileList = new String[v.size()];
		      for(int i=0;i<v.size();i++){
		    	  videoFileList[i] = "/mnt/sdcard/"+v.get(i);
		      }
		      setListAdapter(new MyThumbnaildapter(AndroidThumbnailList.this, R.layout.row, videoFileList));
		  }
		}
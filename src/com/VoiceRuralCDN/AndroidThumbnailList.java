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
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class AndroidThumbnailList extends ListActivity{
	
	private NotesDbAdapter mDbHelper;
	private static final int DELETE_ID = Menu.FIRST + 1;
    private Cursor mNotesCursor;
    Vector <String> v = new Vector<String>();
	String[] videoFileList;
	public class MyThumbnaildapter extends ArrayAdapter<String> implements View.OnCreateContextMenuListener{

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
		      row.setOnCreateContextMenuListener(this);

		  return row;
		 }
		 public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
	          // empty implementation
	        }


		}
	 	
		  /** Called when the activity is first created. */
		  @Override
		  public void onCreate(Bundle savedInstanceState) {
		      super.onCreate(savedInstanceState);
		      mDbHelper = new NotesDbAdapter(this);
		      mDbHelper.open();
		      
		      fillVideos();
		      
		      registerForContextMenu(getListView());
		  }
		  
		  public void fillVideos(){
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
		  @Override
			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo) {
		    	    super.onCreateContextMenu(menu, v, menuInfo);
		    	    menu.add(0, DELETE_ID, 0, R.string.menu_delete);
			}
		
		    @Override
			public boolean onContextItemSelected(MenuItem item) {
		    	switch(item.getItemId()) {
		        case DELETE_ID:
		            AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		            mNotesCursor.moveToPosition((int) info.id);
		            mDbHelper.deleteNote(mNotesCursor.getLong(0));
		            fillVideos();
		            return true;
		        }
		        return super.onContextItemSelected(item);
			}
		}
package com.VoiceRuralCDN;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ListView;
import android.widget.TextView;
//import android.widget.MediaController;
import android.widget.SimpleCursorAdapter;
//import android.widget.VideoView;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class Download extends ListActivity {
    private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;
    TextView tv;
    private static final int INSERT_ID = Menu.FIRST;
    private static final int DELETE_ID = Menu.FIRST + 1;
    private NotesDbAdapter mDbHelper;
    private Cursor mNotesCursor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notes_list);
        String schema=getDBschema();
        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();
        syncSchema(schema);
        final Intent queryIntent = getIntent();
		final String queryAction = queryIntent.getAction();
		if (Intent.ACTION_SEARCH.equals(queryAction)) {
			String searchKeywords = queryIntent.getStringExtra(SearchManager.QUERY);
			 mNotesCursor = mDbHelper.searchByTag(searchKeywords);
		        startManagingCursor(mNotesCursor);
		        String[] from = new String[]{NotesDbAdapter.KEY_TITLE};
		        int[] to = new int[]{R.id.text1};
		        SimpleCursorAdapter notes = 
		        	    new SimpleCursorAdapter(this, R.layout.notes_row, mNotesCursor, from, to);
		        setListAdapter(notes);
		}
		else{
	        fillData();
	        registerForContextMenu(getListView());
		}
    }
    
    private void syncSchema(String schema){
    	String[] arr = schema.split("@");
    	for(int i=0;i<arr.length;i++){
    		String [] arr1 = arr[i].split("#");
    		Cursor cur =mDbHelper.searchName(arr1[0]);
//    		String [] temp = arr1[4].split("~");
//    		if(!temp[0].equals("default"))
//    			for(int j=0;j<temp.length;j++)
//    				temp[j] = temp[j]+" 0";
//    		if(!temp[0].equals("default"))
//    			arr1[4] = temp[0];
//    		else
//    			arr1[4] = "";
//    		for(int j=1;j<temp.length;j++)
//    			arr1[4] = arr1[4]+"~"+temp[j];
    		if(cur!=null && cur.getCount()==0){
    				mDbHelper.createNote(arr1[0], arr1[1], arr1[2], arr1[3],"0",arr1[5],arr1[4]);
    		}
    		else if(cur!=null && cur.getCount()==1){
    			mDbHelper.updateNote(cur.getInt(0),arr1[0], arr1[1], arr1[2], arr1[3],cur.getString(5),arr1[5],arr1[4]);
    		}
    	}
    }
    private String getDBschema(){
    	String response="";
    	Socket socket = null;
        DataOutputStream dataOutputStream = null;
        DataInputStream dataInputStream = null;
    	try {
        socket = new Socket("192.168.1.185", 2004);

	  		dataOutputStream = new DataOutputStream(socket.getOutputStream());
		  	dataInputStream = new DataInputStream(socket.getInputStream());
		  	String msg =  "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root>" +
		  				  "<Message><size>1</size><fileName>efew</fileName>" +
		  				  "<Title>efwe</Title><Desc>efwf</Desc>"+
	           			  "<Tags>ewfw</Tags><Time_stamp>fewfw</Time_stamp>"+
	                      "<Conference_stamp>ewfw</Conference_stamp>"
	                      +"<Function>sync</Function>" + "</Message></root>\n";
		    dataOutputStream.writeBytes(msg);
		    dataOutputStream.flush();
		    response =(dataInputStream.readLine());
			dataInputStream.close();
			dataOutputStream.close();
			socket.close();
    	}catch(Exception e){}
	    
    	return response;
    }
    private void fillData() {
        mNotesCursor = mDbHelper.fetchAllNotes();
        startManagingCursor(mNotesCursor);
        String[] from = new String[]{NotesDbAdapter.KEY_TITLE};
        int[] to = new int[]{R.id.text1};
        SimpleCursorAdapter notes = 
        	    new SimpleCursorAdapter(this, R.layout.notes_row, mNotesCursor, from, to);
        setListAdapter(notes);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, INSERT_ID,0, R.string.menu_insert);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
        case INSERT_ID:
            createNote();
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
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
            mDbHelper.deleteNote(info.id);
            fillData();
            return true;
        }
        return super.onContextItemSelected(item);
	}

    private void createNote() {
    	/*Intent i = new Intent(this, NoteEdit.class);
    	startActivityForResult(i, ACTIVITY_CREATE);*/
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
  
        super.onListItemClick(l, v, position, id);
        Cursor c = mNotesCursor;
        c.moveToPosition(position);
        String title = c.getString(c.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE));
        String desc = c.getString(c.getColumnIndexOrThrow(NotesDbAdapter.KEY_DESCRIPTION));
        String tags = c.getString(c.getColumnIndexOrThrow(NotesDbAdapter.KEY_TAGS));
        displayInfo(v,title,desc,tags);
    }

    public void displayInfo(View v,String title, String desc, String tags){
    	Intent foo = new Intent(this, DisplayInfo.class);
    	foo.putExtra("TITLE",title);
    	foo.putExtra("DESC", desc);
    	foo.putExtra("TAGS", tags);
    	startActivity(foo);
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	super.onActivityResult(requestCode, resultCode, intent);
    	Bundle extras = intent.getExtras();

    	switch(requestCode) {
    	case ACTIVITY_CREATE:
    	    String title = extras.getString(NotesDbAdapter.KEY_TITLE);
    	    String body = extras.getString(NotesDbAdapter.KEY_DESCRIPTION);
    	    String tags = extras.getString(NotesDbAdapter.KEY_TAGS);
    	    String comments="";
    	    String type="0";
    	    mDbHelper.createNote(title, body, tags, comments, type);//,"");
    	    fillData();
    	    break;
    	case ACTIVITY_EDIT:
    	    Long mRowId = extras.getLong(NotesDbAdapter.KEY_ROWID);
    	    if (mRowId != null) {
    	        String editTitle = extras.getString(NotesDbAdapter.KEY_TITLE);
    	        String editBody = extras.getString(NotesDbAdapter.KEY_DESCRIPTION);
    	        String editTags = extras.getString(NotesDbAdapter.KEY_TAGS);
    	        String editComments="";
        	    String editType="0";
    	        mDbHelper.updateNote(mRowId, editTitle, editBody, editTags, editComments, editType);
    	    }
    	    fillData();
    	    break;
    	}
    }*/
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
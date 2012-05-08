package com.VoiceRuralCDN;


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
    TextView tv;
    private static final int INSERT_ID = Menu.FIRST;
    private static final int DELETE_ID = Menu.FIRST + 1;
    private NotesDbAdapter mDbHelper;
    private Cursor mNotesCursor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notes_list);
        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();
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
	        //registerForContextMenu(getListView());
		}
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
    protected void onListItemClick(ListView l, View v, int position, long id) {
  
        super.onListItemClick(l, v, position, id);
        Cursor c = mNotesCursor;
        c.moveToPosition(position);
        String title = c.getString(c.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE));
        String desc = c.getString(c.getColumnIndexOrThrow(NotesDbAdapter.KEY_DESCRIPTION));
        String tags = c.getString(c.getColumnIndexOrThrow(NotesDbAdapter.KEY_TAGS));
        String conf = c.getString(c.getColumnIndexOrThrow(NotesDbAdapter.KEY_CONFTIME));
        String comm = c.getString(c.getColumnIndexOrThrow(NotesDbAdapter.KEY_COMMENTS));
        displayInfo(v,title,desc,tags,conf,comm);
    }

    public void displayInfo(View v,String title, String desc, String tags,String conf,String comm){
    	Intent foo = new Intent(this, DisplayInfo.class);
    	foo.putExtra("TITLE",title);
    	foo.putExtra("DESC", desc);
    	foo.putExtra("TAGS", tags);
    	foo.putExtra("CONF", conf);
    	foo.putExtra("COMM", comm);
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
   
}
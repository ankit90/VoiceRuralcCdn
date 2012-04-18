package com.VoiceRuralCDN;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

public class SearchResult extends Activity
{
	private NotesDbAdapter mDbHelper;
    private Cursor mNotesCursor;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Bundle s = getIntent().getExtras();
        String searchKeywords = s.getString("tag");

    }
}

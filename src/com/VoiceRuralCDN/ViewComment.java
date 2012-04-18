package com.VoiceRuralCDN;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class ViewComment extends ListActivity{
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comments_list);
        Bundle s = getIntent().getExtras();
        String videocomments = s.getString("VideoComments");
        String [] comments = videocomments.split("~");
        setListAdapter(new ArrayAdapter(this,R.layout.comments_row,comments));
    }

}

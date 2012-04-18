package com.VoiceRuralCDN;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Video_thumnailsActivity extends Activity {
	
	
	private final static Uri MEDIA_EXTERNAL_CONTENT_URI =MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
	
	private final static String _ID = MediaStore.Video.Media._ID;
	private final static String MEDIA_DATA = MediaStore.Video.Media.DATA;
	private GridView _gallery;
	private Cursor _cursor;
	private int _columnIndex;
	private int[] _videosId;
	private Uri _contentUri;
	protected Context _context;
	TextView tv;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        _context = getApplicationContext();
        setContentView(R.layout.downloaded);
        //set GridView for gallery
        
        _gallery = (GridView) findViewById(R.id.gridView);
        //set default as external/sdcard uri
		_contentUri = MEDIA_EXTERNAL_CONTENT_URI;//Uri.parse(Environment.getExternalStorageDirectory().getAbsolutePath()+"/DCIM/100MEDIA/");
		tv = (TextView) findViewById( R.id.text);
        //tv.setOnClickListener((OnClickListener)this);
		//initialize the videos uri 
		showToast(_contentUri.toString());
		initVideosId();
		//set gallery adapter
		setGalleryAdapter();
		
    }

//    public void clicktext(View v) {
//		if (v.getId() == R.id.textin1)
//		{
//			Intent i = new Intent(this, VoiceRecog.class);
//	    	startActivity(i);
//		}
//	}
    
    private class VideoGalleryAdapter extends BaseAdapter
    {
    	public VideoGalleryAdapter(Context c){
    		_context = c;
    	}
    	public int getCount(){
    		return _videosId.length;
    	}

    public Object getItem(int position)
    {
    return position;
    }

    public long getItemId(int position)
    {
    return position;
    }
    
    public View getView(int position, View convertView, ViewGroup parent)
    {
    ImageView imgVw= new ImageView(_context);;
    try
    {
    if(convertView!=null)
    {
    imgVw= (ImageView) convertView;
    }
    imgVw.setImageBitmap(getImage(_videosId[position]));
    imgVw.setLayoutParams(new GridView.LayoutParams(96, 96));
    imgVw.setPadding(8, 8, 8, 8);
    }
    catch(Exception ex)
    {
    //System.out.println("StartActivity:getView()-135: ex " + ex.getClass() +", "+ ex.getMessage());
    }
    return imgVw;
    }

    // Create the thumbnail on the fly
    private Bitmap getImage(int id) {
    Bitmap thumb = MediaStore.Video.Thumbnails.getThumbnail(getContentResolver(),id, MediaStore.Video.Thumbnails.MICRO_KIND, null);
    return thumb;
    }
    }

    private void initVideosId() {
    	try {
    	//Here we set up a string array of the thumbnail ID column we want to get back
    	String [] proj={_ID};
    	//Now we create the cursor pointing to the external thumbnail store
    	_cursor = managedQuery(_contentUri,
    			proj, // Which columns to return
    			null,       // WHERE clause; which rows to return (all rows)
    			null,       // WHERE clause selection arguments (none)
    			null); // Order-by clause (ascending by name)
    	int count= _cursor.getCount();
    	// We now get the column index of the thumbnail id
    	_columnIndex = _cursor.getColumnIndex(_ID);
    	//initialize
    	_videosId = new int[count];
    	//move position to first element
    	_cursor.moveToFirst();
    	for(int i=0;i<count;i++)
    	{
    	int id = _cursor.getInt(_columnIndex);
    	//
    	_videosId[i]= id;
    	//
    	_cursor.moveToNext();
    	//
    	}
    	}
    	catch(Exception ex)
    	{
    	showToast(ex.getMessage().toString());
    	}
    	}

   
    
    protected void showToast(String msg)
    {
    Toast.makeText(_context, msg, Toast.LENGTH_LONG).show();
    }
    protected void playit(View v,String s){
    	Intent foo = new Intent(this, Comments.class);
    	foo.putExtra("filename",s);
    	startActivity(foo);
    	
    }
    private void setGalleryAdapter() {
    	_gallery.setAdapter(new VideoGalleryAdapter(_context));
    	_gallery.setOnItemClickListener(_itemClickLis);
    	}
    
    private AdapterView.OnItemClickListener _itemClickLis = new AdapterView.OnItemClickListener(){

    public void onItemClick(AdapterView parent, View v, int position, long id)
    {
    // Now we want to actually get the data location of the file
    String [] proj={MEDIA_DATA};
    // We request our cursor again
    _cursor = managedQuery(_contentUri,
    						proj, // Which columns to return
    						null,       // WHERE clause; which rows to return (all rows)
    						null,       // WHERE clause selection arguments (none)
    						null); // Order-by clause (ascending by name)
    // We want to get the column index for the data uri
    int count = _cursor.getCount();
    _cursor.moveToFirst();
    _columnIndex = _cursor.getColumnIndex(MEDIA_DATA);
    // Lets move to the selected item in the cursor
    _cursor.moveToPosition(position);
    // And here we get the filename
    String filename = _cursor.getString(_columnIndex);

    //*********** You can do anything when you know the file path :-)
    
    playit(v,filename);
    //
    }
};    
}
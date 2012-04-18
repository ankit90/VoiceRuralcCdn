package com.VoiceRuralCDN;

import android.app.Activity;
//import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

public class Play extends Activity{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);
        //videoPlayer("/mnt/sdcard/DCIM/100MEDIA","VIDEO0001.mp4",true);
        VideoView vv = new VideoView(getApplicationContext());
//        Intent i = new Intent(this,Notepadv2.class);
        Bundle s = getIntent().getExtras();
        String ss = s.getString("filename1");
        setContentView(vv);
        vv.setVideoPath(ss);
//        vv.setVideoPath("/mnt/sdcard/DCIM/100MEDIA/VIDEO0001.3gp");
        vv.setMediaController(new MediaController(this));
        vv.requestFocus();
        vv.start();
        

    }
    
    public void videoPlayer(String path, String fileName, boolean autoplay){
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        VideoView videoHolder = new VideoView(this);
        videoHolder.setMediaController(new MediaController(this));
        videoHolder.setVideoURI(Uri.parse(path+"/"+fileName));
        videoHolder.requestFocus();
        if(autoplay){
            videoHolder.start();
        }
 }
}
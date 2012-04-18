package com.VoiceRuralCDN;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class VideoDownloaded extends Activity {

	String message="";
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.final_screen);
        Button back = (Button)findViewById(R.id.button1);
        TextView tv = (TextView)findViewById(R.id.d_tv);
        Bundle s = getIntent().getExtras();
        message = s.getString("message");
        tv.setText(message);
        back.setOnClickListener(buttonSendOnClickListener);
	}
	Button.OnClickListener buttonSendOnClickListener
	 = new Button.OnClickListener(){

	@Override
	public void onClick(View arg0) {
	 // TODO Auto-generated method stub
		back(arg0);
	 
	}};
	
	public void back(View v)
	{
		Intent foo = new Intent(this,TabOptions.class);
    	startActivity(foo);
	}
}

package com.VoiceRuralCDN;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;



public class Options extends Activity implements OnClickListener{

	Button start;
	Button join;
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.conf_options);
        start = (Button) findViewById(R.id.button1);
        join = (Button) findViewById(R.id.button2);
        start.setOnClickListener(this);
        join.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
	 // TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.button1:
			Intent foo = new Intent(this,SIP.class);
			startActivity(foo);
			break;
        case R.id.button2:
           
            break;
        
	}
	}
}

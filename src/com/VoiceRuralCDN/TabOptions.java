package com.VoiceRuralCDN;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TabHost;

public class TabOptions extends TabActivity implements OnClickListener {
	private Button bb;

	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.taboptions);
	    
	    bb = (Button) findViewById(R.id.button2);
	    bb.setOnClickListener(this);

	    Resources res = getResources(); // Resource object to get Drawables
	    TabHost tabHost = getTabHost();  // The activity TabHost
	    TabHost.TabSpec spec;  // Resusable TabSpec for each tab
	    Intent intent;  // Reusable Intent for each tab

	    // Create an Intent to launch an Activity for the tab (to be reused)
	    intent = new Intent().setClass(this, Download.class);

	    // Initialize a TabSpec for each tab and add it to the TabHost
	    spec = tabHost.newTabSpec("CDN Videos").setIndicator("CDN Videos",
	                      res.getDrawable(R.layout.ic_tab_download))
	                  .setContent(intent);
	    tabHost.addTab(spec);
	    // Do the same for the other tabs
	    intent = new Intent().setClass(this, AndroidThumbnailList.class);
	    spec = tabHost.newTabSpec("Downloaded").setIndicator("Downloaded",
	                      res.getDrawable(R.layout.ic_tab_cdn))
	                  .setContent(intent);
	    tabHost.addTab(spec);
	    tabHost.setCurrentTab(0);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId()==R.id.button2)
			startActivity(new Intent(this,VoiceRecog.class));
	}
}

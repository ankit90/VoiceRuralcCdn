package com.VoiceRuralCDN;


import android.os.Bundle;
import android.preference.*;


public class Settings extends PreferenceActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.preference);
    }
} 
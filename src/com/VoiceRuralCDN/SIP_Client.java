package com.VoiceRuralCDN;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.sip.SipAudioCall;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;
import android.net.sip.SipRegistrationListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class SIP_Client extends Activity {
	
	public SipManager mSipManager = null;
	public SipProfile mSipProfile = null;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	
        if(mSipManager == null) {
            mSipManager = SipManager.newInstance(this);
        }
        String username="ankit",domain="192.168.1.12",password="asdf";
        try{
        SipProfile.Builder builder = new SipProfile.Builder(username, domain);
        builder.setPassword(password);
        mSipProfile = builder.build();
        
        Intent intent = new Intent();
        intent.setAction("android.SipDemo.INCOMING_CALL");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, Intent.FILL_IN_DATA);
        mSipManager.open(mSipProfile, pendingIntent, null);
        
        
        mSipManager.setRegistrationListener(mSipProfile.getUriString(), new SipRegistrationListener() {

    		public void onRegistering(String localProfileUri) {
    		    updateStatus("Registering with SIP Server...");
    		}

    		public void onRegistrationDone(String localProfileUri, long expiryTime) {
    		    updateStatus("Ready");
    		}
    		   
    		public void onRegistrationFailed(String localProfileUri, int errorCode,
    		    String errorMessage) {
    		    updateStatus("Registration failed.  Please check settings.");
    		}
        });
		
        SipAudioCall.Listener listener = new SipAudioCall.Listener() {
        	  
        	   @Override
        	   public void onCallEstablished(SipAudioCall call) {
        	      call.startAudio();
        	      call.setSpeakerMode(true);
        	      call.toggleMute();
        	        
        	   }
        	   
        	   @Override
        	   public void onCallEnded(SipAudioCall call) {
        	      // Do something.
        	   }
        	};
        	String sipAddress ="sip:anupam90@sip2sip.info";
            mSipManager.makeAudioCall(mSipProfile.getUriString(), sipAddress, listener, 30);
           
        }catch(Exception e){}
        
        
    	
	}
	private void updateStatus(String string) {
		// TODO Auto-generated method stub
		Toast.makeText(this, string, Toast.LENGTH_LONG).show();
		
	}
	public void closeLocalProfile() {
	    if (mSipManager == null) {
	       return;
	    }
	    try {
	       if (mSipProfile != null) {
	          mSipManager.close(mSipProfile.getUriString());
	       }
	     } catch (Exception ee) {
	       Log.d("WalkieTalkieActivity/onDestroy", "Failed to close local profile.", ee);
	     }
	}

}

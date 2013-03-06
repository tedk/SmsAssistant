package net.homeip.tedk.smsassistant;

import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.UtteranceProgressListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class TestActivity extends Activity { 

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.test);

      final Context appContext = this;
      
      Button b = (Button) findViewById(R.id.testButton);
      b.setOnClickListener(new OnClickListener() {
		public void onClick(View v) {
			String message = null;
			try {
				final AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
				BluetoothAdapter ba = BluetoothAdapter.getDefaultAdapter();
				if(ba != null && ba.isEnabled() && ba.getProfileConnectionState(BluetoothProfile.HEADSET) == BluetoothProfile.STATE_CONNECTED) {
					if(am.isBluetoothScoAvailableOffCall()) {
						registerReceiver(new BroadcastReceiver() {	
							@SuppressWarnings("serial")
							@Override
							public void onReceive(Context context, Intent intent) {
								if(intent.getExtras().getInt(AudioManager.EXTRA_SCO_AUDIO_STATE) == AudioManager.SCO_AUDIO_STATE_CONNECTED) {
									context.unregisterReceiver(this);
									final TextToSpeech tts = new TextToSpeech(context, new OnInitListener() {									
										public void onInit(int status) {
											if(status != TextToSpeech.SUCCESS) {
												new AlertDialog.Builder(appContext).setTitle("Result").setMessage("Unable to Init").setNeutralButton("OK", null).show();
											}
										}
									});
									tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {	
										@Override
										public void onStart(String utteranceId) {
	
										}
										
										@Override
										public void onError(String utteranceId) {
											new AlertDialog.Builder(appContext).setTitle("Result").setMessage("Error speaking: " + utteranceId).setNeutralButton("OK", null).show();
										}
										
										@Override
										public void onDone(String utteranceId) {
											tts.shutdown();
											am.stopBluetoothSco();
											new AlertDialog.Builder(appContext).setTitle("Result").setMessage("Complete").setNeutralButton("OK", null).show();
										}
									});
									tts.speak("New message received from Test Person: Hello, Ted.  This is a test message.  lol.", TextToSpeech.QUEUE_ADD, new HashMap<String, String>() {{
										put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_VOICE_CALL));
									}});
								}
							}
						}, new IntentFilter(AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED));
						am.setMode(AudioManager.MODE_NORMAL);
						am.startBluetoothSco();
					} else {
						message = "SCO Not Available";
					}
				} else {
					message = "Bluetooth headset is not available.";
				}
			} catch (Exception e) {
				message = e.toString();
			} finally {
				if(message != null) {
					new AlertDialog.Builder(appContext).setTitle("Result").setMessage(message).setNeutralButton("OK", null).show();
				}
			}
		}
      });
   }

}

package net.homeip.tedk.smsassistant;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class TestActivity extends Activity { 

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.test);

      Button b = (Button) findViewById(R.id.testButton);
      b.setOnClickListener(new OnClickListener() {
		public void onClick(View v) {
			String message = null;
			try {
				final AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
				if(am.isBluetoothScoAvailableOffCall()) {
					registerReceiver(new BroadcastReceiver() {	
						@Override
						public void onReceive(Context context, Intent intent) {
							if(intent.getExtras().getInt(AudioManager.EXTRA_SCO_AUDIO_STATE) == AudioManager.SCO_AUDIO_STATE_CONNECTED) {
								context.unregisterReceiver(this);
								
								am.stopBluetoothSco();
							}
						}
					}, new IntentFilter(AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED));
					am.startBluetoothSco();
				} else {
					message = "SCO Not Available";
				}
			} catch (Exception e) {
				message = e.toString();
			} finally {
				if(message != null) {
					new AlertDialog.Builder(getApplicationContext()).setTitle("Result").setMessage(message).setNeutralButton("OK", null).show();
				}
			}
		}
	});
   }

}

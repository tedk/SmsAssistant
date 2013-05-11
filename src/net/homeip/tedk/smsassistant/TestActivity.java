package net.homeip.tedk.smsassistant;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class TestActivity extends Activity {

    private boolean serviceRunning = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.test);

	((Button) findViewById(R.id.testSpeechButton)).setOnClickListener(new OnClickListener() {
	    public void onClick(View v) {
		SpeechRecognitionManager srm = new SpeechRecognitionManager(TestActivity.this);
		srm.listen(new SpeechRecognitionManager.OnCompleteListener() {
		    public void onComplete(String[] results) {
		        new AlertDialog.Builder(TestActivity.this).setTitle("Result").setMessage(results == null ? "null" : results[0]).setNeutralButton("Close", null).show();
		    }
		});
	    }
	});
	
	((Button) findViewById(R.id.testMessageButton)).setOnClickListener(new OnClickListener() {
	    public void onClick(View v) {
		new MessageHandler(TestActivity.this).handle("Ted Krofssik", "Hello. This is a test message.");
	    }
	});
	
	final Button testMessageButton = (Button) findViewById(R.id.testListenerButton);
	testMessageButton.setOnClickListener(new OnClickListener() {
	    public void onClick(View v) {
		if(serviceRunning) {
		    testMessageButton.setText("Test Listener");
		    serviceRunning = false;
		    TestActivity.this.stopService(new Intent(TestActivity.this, SmsListenerService.class));
		} else {
		    testMessageButton.setText("Stop Listener");
		    serviceRunning = true;
		    TestActivity.this.startService(new Intent(TestActivity.this, SmsListenerService.class));
		}
	    }
	});
    }

}

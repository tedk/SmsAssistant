package net.homeip.tedk.smsassistant;

import android.app.Activity;
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
		new MessageHandler(TestActivity.this).handle("Ted Krofssik", "Hello. This is a test message.");
//		SpeechRecognitionManager srm = new SpeechRecognitionManager(TestActivity.this);
//		srm.listen(new SpeechRecognitionManager.OnCompleteListener() {
//		    public void onComplete(String[] results) {
//		        new AlertDialog.Builder(TestActivity.this).setTitle("Result").setMessage(results == null ? "null" : results[0]).setNeutralButton("Close", null).show();
//		    }
//		});
	    }
	});
    }

}

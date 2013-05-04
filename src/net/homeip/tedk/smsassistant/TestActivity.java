package net.homeip.tedk.smsassistant;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class TestActivity extends Activity {

    private BluetoothManager bm;
    private SpeechRecognitionManager srm;
    private TtsManager tm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.test);

	bm = new BluetoothManager(this);
	srm = new SpeechRecognitionManager();
	srm.start(this);
	tm = new TtsManager(this);

	Button b = (Button) findViewById(R.id.testButton);
	b.setOnClickListener(new OnClickListener() {
	    public void onClick(View v) {
		if (!bm.isAvailable())
		    return;
		new Handler().post(new Runnable() {
		    
		    public void run() {
			bm.start();
			tm.start();
			tm.speak("Test1");
			if (SpeechRecognitionManager.contains(srm.listen(), "READ")) {
			    tm.speak("Test2");
			} else {
			    tm.speak("Test3");
			}
			tm.stop();
			bm.stop();
		    }
		});

	    }
	});
    }

}

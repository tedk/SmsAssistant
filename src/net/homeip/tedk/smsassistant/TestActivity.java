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

	final MessageHandler mh = new MessageHandler(this);

	Button b = (Button) findViewById(R.id.testButton);
	b.setOnClickListener(new OnClickListener() {
	    public void onClick(View v) {
		mh.handle("Ted Krofssik", "Hello. This is a test message.");
	    }
	});
    }

}

package net.homeip.tedk.smsassistant;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;

/**
 *  VoiceRecognition API Demo
 */
public class SpeechRecognitionManager extends Activity {
    
    private CountDownLatch latch = new CountDownLatch(1);
    
    private int currentId;
    private volatile String[] result = null;
    
    private Context context;
    
    public void start(Context context) {
	this.context = context;
    }
    
    @SuppressLint("DefaultLocale")
    public static boolean contains(String[] results, String item) {
	if(results == null || item == null || results.length < 1 || item.length() < 1)
	    return false;
	for(String r : results) {
	    if(r != null && r.trim().toLowerCase().equals(item.toLowerCase())) {
		return true;
	    }
	}
	return false;
    }

    public String[] listen() {
	context.startActivity(new Intent(context, this.getClass()));
	try { 
	    latch.await();
	} catch (InterruptedException e) {
	}
	return result;
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
	Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

	// Specify the calling package to identify your application
	intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass()
		.getPackage().getName());

	// Display an hint to the user about what he should say.
	intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
		"Say a command");

	// Given an hint to the recognizer about what the user is going to say
	intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
		RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

	// Specify how many results you want to receive. The results will be
	// sorted
	// where the first result is the one with higher confidence.
//	intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);

	// Specify the recognition language. This parameter has to be specified
	// only if the
	// recognition has to be done in a specific language and not the default
	// one (i.e., the
	// system locale). Most of the applications do not have to set this
	// parameter.
//	if (!mSupportedLanguageView.getSelectedItem().toString()
//		.equals("Default")) {
//	    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
//		    mSupportedLanguageView.getSelectedItem().toString());
//	}

	startActivityForResult(intent, currentId++);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	if(requestCode == currentId) {
	    if(resultCode == RESULT_OK) {
		ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
		if(results == null) {
		    result = null;
		    latch.countDown();
		} else if (results.size() < 1){
		    result = null;
		    latch.countDown();
		} else {
		    result = (String[]) results.toArray();
		    latch.countDown();
		}
	    } else {
		result = null;
		latch.countDown();
	    }
	}
	finish();
	
        super.onActivityResult(requestCode, resultCode, data);
    }

}

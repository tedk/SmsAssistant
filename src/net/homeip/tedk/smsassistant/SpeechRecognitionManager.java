package net.homeip.tedk.smsassistant;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

public class SpeechRecognitionManager implements RecognitionListener {
    
    public interface OnCompleteListener {
	public void onComplete(String[] results);
    }
    
    private OnCompleteListener onCompleteListener;
    
    private Context context;
    private SpeechRecognizer sr;
    
    public SpeechRecognitionManager(Context context) {
	this.context = context;
	sr = SpeechRecognizer.createSpeechRecognizer(context);       
        sr.setRecognitionListener(this);
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

    public void listen(OnCompleteListener listener) {
	this.onCompleteListener = listener;
	
	Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);        
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.getPackageName());
        sr.startListening(intent);
    }

    public void onBeginningOfSpeech() {
	Log.d("SpeechRecognitionManager", "onBeginningOfSpeech");
    }

    public void onBufferReceived(byte[] buffer) {
	Log.d("SpeechRecognitionManager", "onBufferReceived");
    }

    public void onEndOfSpeech() {
	Log.d("SpeechRecognitionManager", "onEndOfSpeech");
    }

    public void onError(int error) {
	Log.d("SpeechRecognitionManager", "onError");
	onCompleteListener.onComplete(null);
    }

    public void onEvent(int eventType, Bundle params) {
	Log.d("SpeechRecognitionManager", "onEvent");
    }

    public void onPartialResults(Bundle partialResults) {
	Log.d("SpeechRecognitionManager", "onPartialResults");
    }

    public void onReadyForSpeech(Bundle params) {
	Log.d("SpeechRecognitionManager", "onReadyForSpeech");
    }

    public void onResults(Bundle results) {
	Log.d("SpeechRecognitionManager", "onResults");
	ArrayList<String> data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
	if(data == null || data.size() < 1) {
	    onCompleteListener.onComplete(null);
	} else {
	    onCompleteListener.onComplete(data.toArray(new String[data.size()]));
	}
    }

    public void onRmsChanged(float rmsdB) {
	Log.d("SpeechRecognitionManager", "onRmsChanged");
    } 

}

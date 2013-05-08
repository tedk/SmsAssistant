package net.homeip.tedk.smsassistant;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

/**
 *  VoiceRecognition API Demo
 */
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
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say a command");
        sr.startListening(intent);
    }

    public void onBeginningOfSpeech() {
	
    }

    public void onBufferReceived(byte[] buffer) {
	
    }

    public void onEndOfSpeech() {
	
    }

    public void onError(int error) {
	
    }

    public void onEvent(int eventType, Bundle params) {
	
    }

    public void onPartialResults(Bundle partialResults) {
	
    }

    public void onReadyForSpeech(Bundle params) {
	
    }

    public void onResults(Bundle results) {
	ArrayList<String> data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
	if(data == null || data.size() < 1) {
	    onCompleteListener.onComplete(null);
	} else {
	    onCompleteListener.onComplete((String[]) data.toArray());
	}
    }

    public void onRmsChanged(float rmsdB) {
	
    } 

}

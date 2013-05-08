package net.homeip.tedk.smsassistant;

import java.util.HashMap;

import android.content.Context;
import android.media.AudioManager;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

public class TtsManager extends UtteranceProgressListener {
    
    public interface OnReadyListener {
	public void onReady();
    }
    public interface OnCompleteListener {
	public void onComplete();
    }
    
    private OnReadyListener onReadyListener;
    private OnCompleteListener onCompleteListener;

    private int currentId = 0;

    private Context context;
    private TextToSpeech tts;

    public TtsManager(Context context) {
	this.context = context;
    }

    public void start(OnReadyListener listener) {
	this.onReadyListener = listener;
	tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
	    public void onInit(int status) {
		tts.setOnUtteranceProgressListener(TtsManager.this);
		onReadyListener.onReady();
	    }
	});
    }

    public void stop() {
	tts.stop();
    }

    public void speak(final String text, OnCompleteListener listener) {
	this.onCompleteListener = listener;
	final HashMap<String, String> hashMap = new HashMap<String, String>();
	hashMap.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,
		String.valueOf(currentId++));
	hashMap.put(TextToSpeech.Engine.KEY_PARAM_VOLUME, String.valueOf(1.f));
	hashMap.put(TextToSpeech.Engine.KEY_PARAM_STREAM,
		String.valueOf(AudioManager.STREAM_VOICE_CALL));

	new CountDownTimer(1000, 1000 / 2) {
	    @Override
	    public void onFinish() {
		try {
		    tts.speak(text, TextToSpeech.QUEUE_ADD, hashMap);
		} catch (Exception e) {

		}
	    }

	    @Override
	    public void onTick(long arg0) {
	    }

	}.start();
    }

    @Override
    public void onDone(String utteranceId) {
	stop();
	onCompleteListener.onComplete();
    }

    @Override
    public void onError(String utteranceId) {
	stop();
	onCompleteListener.onComplete();
    }

    @Override
    public void onStart(String utteranceId) {
    }

}

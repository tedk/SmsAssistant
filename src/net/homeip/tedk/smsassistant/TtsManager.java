package net.homeip.tedk.smsassistant;

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

import android.content.Context;
import android.media.AudioManager;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

public class TtsManager extends UtteranceProgressListener {
    
    private CountDownLatch latch = new CountDownLatch(1);

    private int currentId = 0;

    private Context context;
    private TextToSpeech tts;

    public TtsManager(Context context) {
	this.context = context;
    }

    public void start() {
	tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
	    public void onInit(int status) {
		tts.setOnUtteranceProgressListener(TtsManager.this);
		latch.countDown();
	    }
	});
	try { 
	    latch.await();
	} catch (InterruptedException e) {
	}
    }

    public void stop() {
	tts.stop();
    }

    public void speak(final String text) {
	final HashMap<String, String> hashMap = new HashMap<String, String>();
	hashMap.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,
		String.valueOf(currentId++));
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
	try { 
	    latch.await();
	} catch (InterruptedException e) {
	}
    }

    @Override
    public void onDone(String utteranceId) {
	stop();
	latch.countDown();
    }

    @Override
    public void onError(String utteranceId) {
	stop();
	latch.countDown();
    }

    @Override
    public void onStart(String utteranceId) {
    }

}

package net.homeip.tedk.smsassistant;

import android.content.Context;

public class MessageHandler {
    
    private enum Mode {
	ANNOUNCE,
	READ,
    }

    private class BluetoothListener implements BluetoothManager.OnReadyListener {
	public void onReady() {
	    tm.start(tl);
	}
    }
    
    private class SpeechRecognitionListener implements SpeechRecognitionManager.OnCompleteListener {
	public void onComplete(String[] results) {
	    if(SpeechRecognitionManager.contains(results, "READ")) {
		tm.speak("Hello.  This is a test message.", tl);
	    }
	}
    }
    
    private class TtsListener implements TtsManager.OnReadyListener, TtsManager.OnCompleteListener {
	public void onReady() {
	    tm.speak("New message from Ted Krofssik", tl);
	    
	}
	public void onComplete() {
	    switch(mode) {
	    case ANNOUNCE:
		mode = Mode.READ;
		srm.listen(srl);
	    case READ:
		stop();
		break;
	    default:
		stop();
		break;
	    }
	}
    }
    
    private Mode mode;
    private BluetoothManager bm;
    private SpeechRecognitionManager srm;
    private TtsManager tm;
    private BluetoothListener bl = new BluetoothListener();
    private SpeechRecognitionListener srl = new SpeechRecognitionListener();
    private TtsListener tl = new TtsListener();
    
    public MessageHandler(Context context) {
	bm = new BluetoothManager(context);
	srm = new SpeechRecognitionManager();
	srm.start(context);
	tm = new TtsManager(context);
    }
    
    public void handle(String sender, String message) {
	mode = Mode.ANNOUNCE;
	if(!bm.isAvailable())
	    return;
	
	bm.start(bl);
    }
    
    public void stop() {
	tm.stop();
	bm.stop();
    }

}

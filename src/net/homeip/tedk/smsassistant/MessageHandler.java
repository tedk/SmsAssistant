package net.homeip.tedk.smsassistant;

import android.content.Context;

public class MessageHandler {
    
    private enum Mode {
	ANNOUNCE,
	READ,
    }

    private class BluetoothListener implements BluetoothManager.OnReadyListener {
	public void onReady() {
	    BluetoothManager.startVoiceRecognition(bm.getBluetoothHeadset());
	    tm.start(tl);
	}
    }
    
    private class SpeechRecognitionListener implements SpeechRecognitionManager.OnCompleteListener {
	public void onComplete(String[] results) {
	    if(SpeechRecognitionManager.contains(results, "READ")) {
		mode = Mode.READ;
		tm.speak(message, tl);
	    } else {
		stop();
	    }
	}
    }
    
    private class TtsListener implements TtsManager.OnReadyListener, TtsManager.OnCompleteListener {
	public void onReady() {
	    tm.speak("New message from " + sender, tl);
	    
	}
	public void onComplete() {
	    switch(mode) {
	    case ANNOUNCE:
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
    
    private String sender;
    private String message;
    
    public MessageHandler(Context context) {
	bm = new BluetoothManager(context);
	srm = new SpeechRecognitionManager(context);
	tm = new TtsManager(context);
    }
    
    public void handle(String sender, String message) {
	this.sender = sender;
	this.message = message;
	this.mode = Mode.ANNOUNCE;
	if(!bm.isAvailable())
	    return;
	
	bm.start(bl);
    }
    
    public void stop() {
	tm.stop();
	BluetoothManager.stopVoiceRecognition(bm.getBluetoothHeadset());
	bm.stop();
    }

}

package net.homeip.tedk.smsassistant;

import android.content.Context;

public class MessageHandler {
    
    private enum Mode {
	ANNOUNCE,
	READ,
    }

    private BluetoothManager.OnReadyListener bluetoothReadyListener = new BluetoothManager.OnReadyListener() {
	public void onReady() {
	    tm.start(ttsReadyListener);
	}
    };
    
    private TtsManager.OnReadyListener ttsReadyListener = new TtsManager.OnReadyListener() {
	public void onReady() {
	    tm.speak("New message from " + sender, ttsCompleteListener);
	}
    };
    
    private TtsManager.OnCompleteListener ttsCompleteListener = new TtsManager.OnCompleteListener() {
	public void onComplete() {
	    switch(mode) {
	    case ANNOUNCE:
		mode = Mode.READ;
		tm.speak(message, ttsCompleteListener);
		break;
	    case READ:
		stop();
		break;
	    default:
		stop();
		break;
	    }
	}
    };
    
    private volatile Mode mode;
    private BluetoothManager bm;
    private TtsManager tm;
    
    private String sender;
    private String message;
    
    public MessageHandler(Context context) {
	bm = new BluetoothManager(context);
	tm = new TtsManager(context);
    }
    
    public void handle(String sender, String message) {
	this.sender = sender;
	this.message = message;
	this.mode = Mode.ANNOUNCE;
	if(!bm.isAvailable())
	    return;
	
	bm.start(bluetoothReadyListener);
    }
    
    public void stop() {
	tm.stop();
	bm.stop();
    }

}

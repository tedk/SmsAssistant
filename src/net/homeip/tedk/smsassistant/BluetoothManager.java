package net.homeip.tedk.smsassistant;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;

public class BluetoothManager implements OnAudioFocusChangeListener {

    private CountDownLatch latch = new CountDownLatch(1);

    private boolean musicWasPlaying = false;
    private boolean speakerPhoneWasOn = false;

    private Context context;
    private AudioManager am;
    private TelephonyManager tm;

    public BluetoothManager(Context context) {
	this.context = context;
	am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
	tm = (TelephonyManager) context
		.getSystemService(Context.TELEPHONY_SERVICE);
    }

    public boolean isAvailable() {
	return (tm.getCallState() == TelephonyManager.CALL_STATE_IDLE);
    }

    public void start() {
	musicWasPlaying = am.isMusicActive();

	if (musicWasPlaying) {
	    Intent downIntent2 = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
	    KeyEvent downEvent2 = new KeyEvent(KeyEvent.ACTION_DOWN,
		    KeyEvent.KEYCODE_MEDIA_STOP);
	    downIntent2.putExtra(Intent.EXTRA_KEY_EVENT, downEvent2);
	    context.sendOrderedBroadcast(downIntent2, null);
	}

	if (am.isBluetoothScoAvailableOffCall()) {
	    am.startBluetoothSco();
	}
	if (!am.isSpeakerphoneOn()) {
	    speakerPhoneWasOn = false;
	    am.setSpeakerphoneOn(true);
	}

	am.requestAudioFocus(BluetoothManager.this,
		AudioManager.STREAM_VOICE_CALL, AudioManager.AUDIOFOCUS_GAIN);

	try { 
	    latch.await();
	} catch (InterruptedException e) {
	}
    }

    public void stop() {

	if (am.isBluetoothScoAvailableOffCall()) {
	    am.stopBluetoothSco();
	}
	if (!speakerPhoneWasOn) {
	    am.setSpeakerphoneOn(false);
	}

	am.abandonAudioFocus(BluetoothManager.this);
	am.setMode(AudioManager.MODE_NORMAL);

	if (musicWasPlaying) {

	    Intent intent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
	    KeyEvent play = new KeyEvent(KeyEvent.ACTION_DOWN,
		    KeyEvent.KEYCODE_MEDIA_PLAY);
	    intent.putExtra(Intent.EXTRA_KEY_EVENT, play);
	    context.sendOrderedBroadcast(intent, null);

	}

	am.setMode(AudioManager.MODE_NORMAL);
    }

    public void onAudioFocusChange(int focusChange) {
	switch (focusChange) {
	case AudioManager.AUDIOFOCUS_GAIN:
	    latch.countDown();
	    break;

	case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT:
	    latch.countDown();
	    break;

	case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK:
	    latch.countDown();
	    break;

	case AudioManager.AUDIOFOCUS_LOSS:
	    stop();
	    break;

	case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
	    stop();
	    break;

	case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
	    stop();
	    break;
	}
    }

}

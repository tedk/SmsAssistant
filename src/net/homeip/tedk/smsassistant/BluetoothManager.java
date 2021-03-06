package net.homeip.tedk.smsassistant;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;

public class BluetoothManager implements OnAudioFocusChangeListener,
	BluetoothProfile.ServiceListener {

    public interface OnReadyListener {
	public void onReady();
    }

    private OnReadyListener onReadyListener;

    private boolean musicWasPlaying = false;
    private boolean speakerPhoneWasOn = false;

    private Context context;
    private Handler handler;
    private AudioManager am;
    private TelephonyManager tm;
    private BluetoothHeadset bh;

    public BluetoothManager(Context context) {
	this.context = context;
	this.handler = new Handler();
	am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
	tm = (TelephonyManager) context
		.getSystemService(Context.TELEPHONY_SERVICE);
    }

    public boolean isAvailable() {
	return (tm.getCallState() == TelephonyManager.CALL_STATE_IDLE);
    }

    public void start(OnReadyListener listener) {
	this.onReadyListener = listener;

	BluetoothAdapter.getDefaultAdapter().getProfileProxy(context, this,
		BluetoothProfile.HEADSET);

	musicWasPlaying = am.isMusicActive();

	if (musicWasPlaying) {
	    Intent downIntent2 = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
	    KeyEvent downEvent2 = new KeyEvent(KeyEvent.ACTION_DOWN,
		    KeyEvent.KEYCODE_MEDIA_STOP);
	    downIntent2.putExtra(Intent.EXTRA_KEY_EVENT, downEvent2);
	    context.sendOrderedBroadcast(downIntent2, null);
	}

	setVolumes(true);

	if (am.isBluetoothScoAvailableOffCall()) {
	    am.startBluetoothSco();
	}
	if (!am.isSpeakerphoneOn()) {
	    speakerPhoneWasOn = false;
	    am.setSpeakerphoneOn(true);
	}

	am.requestAudioFocus(BluetoothManager.this,
		AudioManager.STREAM_VOICE_CALL, AudioManager.AUDIOFOCUS_GAIN);

	handler.postDelayed(new Runnable() {
	    public void run() {
		onReadyListener.onReady();
	    }
	}, 2000);

    }

    public void stop() {

	BluetoothAdapter.getDefaultAdapter().closeProfileProxy(
		BluetoothProfile.HEADSET, bh);

	if (am.isBluetoothScoAvailableOffCall()) {
	    am.stopBluetoothSco();
	}
	if (!speakerPhoneWasOn) {
	    am.setSpeakerphoneOn(false);
	}

	am.abandonAudioFocus(BluetoothManager.this);
	am.setMode(AudioManager.MODE_NORMAL);

	setVolumes(false);

	if (musicWasPlaying) {

	    Intent intent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
	    KeyEvent play = new KeyEvent(KeyEvent.ACTION_DOWN,
		    KeyEvent.KEYCODE_MEDIA_PLAY);
	    intent.putExtra(Intent.EXTRA_KEY_EVENT, play);
	    context.sendOrderedBroadcast(intent, null);

	}

    }

    private void setVolumes(boolean on) {
	am.setStreamMute(AudioManager.STREAM_NOTIFICATION, on);
	am.setStreamMute(AudioManager.STREAM_VOICE_CALL, false);
	am.setStreamSolo(AudioManager.STREAM_VOICE_CALL, on);
	am.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
		am.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL), 0);
    }

    public BluetoothHeadset getBluetoothHeadset() {
	return bh;
    }

    public void onAudioFocusChange(int focusChange) {
	switch (focusChange) {
	case AudioManager.AUDIOFOCUS_GAIN:
	    break;

	case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT:
	    break;

	case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK:
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

    public void onServiceConnected(int profile, BluetoothProfile proxy) {
	if (profile == BluetoothProfile.HEADSET) {
	    bh = (BluetoothHeadset) proxy;
	}
    }

    public void onServiceDisconnected(int profile) {
	if (profile == BluetoothProfile.HEADSET) {
	    bh = null;
	}
    }

}

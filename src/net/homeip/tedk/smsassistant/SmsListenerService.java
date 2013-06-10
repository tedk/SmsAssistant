package net.homeip.tedk.smsassistant;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsMessage;

public class SmsListenerService extends Service {

    private BroadcastReceiver smsReceiver = new BroadcastReceiver() {

	@Override
	public void onReceive(Context context, Intent intent) {
	    Bundle extras = intent.getExtras();

	    if (extras != null) {
		Object[] smsextras = (Object[]) extras.get("pdus");

		for (int i = 0; i < smsextras.length; i++) {
		    SmsMessage smsmsg = SmsMessage
			    .createFromPdu((byte[]) smsextras[i]);

		    String body = smsmsg.getDisplayMessageBody();
		    String source = smsmsg.getDisplayOriginatingAddress();
		    String displayName = ContactResolver.getDisplayName(context, source, smsmsg.isEmail());
		    
		    new MessageHandler(context).handle(displayName, body);

		}

	    }

	}

    };

    @Override
    public void onCreate() {
	super.onCreate();
	
	IntentFilter intentFilter = new IntentFilter();
	intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
	registerReceiver(smsReceiver, intentFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
	super.onStartCommand(intent, flags, startId);

	startForeground(
		1,
		new Notification.Builder(this)
			.setSmallIcon(android.R.drawable.sym_def_app_icon)
			.setContentTitle("SMS Assistant")
			.setContentText("Running...").setOngoing(true)
			.getNotification());

	return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
	return binder;
    }

    private Binder binder = new Binder() {
	@SuppressWarnings("unused")
	public SmsListenerService getService() {
	    return SmsListenerService.this;
	}
    };

    @Override
    public void onDestroy() {
	super.onDestroy();
	stopForeground(true);
	unregisterReceiver(smsReceiver);
    }
}

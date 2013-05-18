package net.homeip.tedk.smsassistant;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Data;
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

		    String body = smsmsg.isEmail() ? smsmsg.getDisplayMessageBody() : smsmsg.getMessageBody();
		    String source = smsmsg.isEmail() ? smsmsg.getDisplayOriginatingAddress() : smsmsg.getOriginatingAddress();

		    new MessageHandler(context).handle(getDisplayName(source), body);

		}

	    }

	}

    };

    private String getDisplayName(String source) {
	Cursor c = getContentResolver().query(
		Data.CONTENT_URI,
		new String[] { Phone.DISPLAY_NAME },
		"( " + Data.MIMETYPE + "=? AND " + Phone.NUMBER + "=? ) OR ( "
			+ Data.MIMETYPE + "=? AND " + Email.ADDRESS + "=? )",
		new String[] { Phone.CONTENT_ITEM_TYPE, source, Email.CONTENT_ITEM_TYPE, source },
		null);
	try {
	    if (c != null) {
		for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
		    String displayName = c.getString(0);
		    if(displayName != null && displayName.length() > 0) {
			return displayName;
		    }
		}
	    }
	} finally {
	    if (c != null) {
		c.close();
	    }
	}
	
	return source;
    }

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

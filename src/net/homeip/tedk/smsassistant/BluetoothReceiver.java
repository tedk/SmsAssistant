package net.homeip.tedk.smsassistant;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BluetoothReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
	if (intent.getAction().equals(BluetoothDevice.ACTION_ACL_CONNECTED)) {
	    context.startService(new Intent(context, SmsListenerService.class));
	}
	if (intent.getAction().equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)
		|| intent.getAction().equals(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED)) {
	    context.stopService(new Intent(context, SmsListenerService.class));
	}
    }
}

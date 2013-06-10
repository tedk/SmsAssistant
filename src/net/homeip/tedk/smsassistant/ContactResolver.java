package net.homeip.tedk.smsassistant;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract;

public class ContactResolver {

    public static String getDisplayName(Context context, String source,
	    boolean email) {
	if (email) {
	    return getContactByEmailAddress(context, source);
	} else {
	    return getContactByPhoneNumber(context, source);
	}
    }

    public static String getContactByPhoneNumber(Context context, String number) {
	Uri uri = Uri.withAppendedPath(
		ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
		Uri.encode(number));
	String name = number;

	ContentResolver contentResolver = context.getContentResolver();
	Cursor contactLookup = contentResolver.query(uri, new String[] {
		BaseColumns._ID, ContactsContract.PhoneLookup.DISPLAY_NAME },
		null, null, null);

	try {
	    if (contactLookup != null && contactLookup.getCount() > 0) {
		contactLookup.moveToNext();
		name = contactLookup.getString(contactLookup
			.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
		// String contactId =
		// contactLookup.getString(contactLookup.getColumnIndex(BaseColumns._ID));
	    }
	} finally {
	    if (contactLookup != null) {
		contactLookup.close();
	    }
	}

	return name;
    }

    public static String getContactByEmailAddress(Context context, String emailAddress) {
	String nameAndEmailOrder = "lower("
		+ ContactsContract.Data.DISPLAY_NAME + ") ASC";
	String[] nameAndEmailProjection = new String[] {
		ContactsContract.Data.DISPLAY_NAME,
		ContactsContract.CommonDataKinds.Email.DATA };

	Cursor emails = context.getContentResolver().query(
		ContactsContract.CommonDataKinds.Email.CONTENT_URI,
		nameAndEmailProjection, 
		ContactsContract.CommonDataKinds.Email.DATA + "=?", 
		new String[] { emailAddress }, 
		nameAndEmailOrder);
	
	String returnVal = emailAddress;

	try {
	    if (emails != null && emails.getCount() > 0) {

		emails.moveToFirst();
		int nameColumn = emails
			.getColumnIndex(ContactsContract.Data.DISPLAY_NAME);
		//int emailColumn = emails
		//	.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA);
		do {
		    returnVal = emails.getString(nameColumn);
		    break;
		} while (emails.moveToNext());

	    }
	} finally {
	    if (emails != null) {
		emails.close();
	    }
	}
	
	return returnVal;
    }

}

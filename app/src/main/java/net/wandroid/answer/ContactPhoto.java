
package net.wandroid.answer;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;

public class ContactPhoto {

    public Uri getPhotoUri(long contactId, ContentResolver contentResolver) {

        try {
            Cursor cursor = contentResolver.query(ContactsContract.Data.CONTENT_URI, null,
                    ContactsContract.Data.CONTACT_ID + "=" + contactId + " AND "

                    + ContactsContract.Data.MIMETYPE + "='"
                            + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + "'", null,
                    null);

            if (cursor != null) {
                if (!cursor.moveToFirst()) {
                    return null;
                }
            } else {
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        Uri person = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
        return Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
    }

    public String fetchContactIdFromPhoneNumber(String phoneNumber, ContentResolver resolver) {

        Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cFetch = resolver.query(uri, new String[] {
                PhoneLookup.DISPLAY_NAME, PhoneLookup._ID
        }, null, null, null);

        String contactId = null;

        if (cFetch.moveToFirst()) {

            cFetch.moveToFirst();

            contactId = cFetch.getString(cFetch.getColumnIndex(PhoneLookup._ID));

        }
        return contactId;

    }
}

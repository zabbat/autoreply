package net.wandroid.answer.contacts;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.widget.ImageView;
import android.widget.Toast;

import net.wandroid.answer.R;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Created by jjungb on 2/15/15.
 */
public class ContactInfo {

    private String mDisplayName;
    public final String phoneNumber;
    private AsyncTask<Void, Void, Bitmap> mContactPhotoTask;

    public ContactInfo(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setContactImage(ImageView imageView, Context context) {
        setContactImage(phoneNumber, imageView, context);
    }

    private void setContactImage(final String phoneNumber, final ImageView imageView, final Context context) {
        mContactPhotoTask = new AsyncTask<Void, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(Void... voids) {
                Bitmap photo = null;
                Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));//phone no
                String[] proj = new String[]
                        {
                                ContactsContract.Contacts.DISPLAY_NAME,
                                ContactsContract.Contacts._ID,
                        };
                String id = null;
                String sortOrder1 = ContactsContract.CommonDataKinds.StructuredPostal.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
                Cursor crsr = null;
                try {
                    crsr = context.getContentResolver().query(lookupUri, proj, null, null, sortOrder1);
                    if (crsr.moveToFirst()) {
                        String name = crsr.getString(crsr.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        id = crsr.getString(crsr.getColumnIndex(ContactsContract.Contacts._ID));
                        mDisplayName = name;
                        photo = BitmapFactory.decodeStream(openPhoto(Long.parseLong(id), context));
                    }
                } finally {
                    if (crsr != null) {
                        crsr.close();
                    }
                    return photo;
                }
            }

            private InputStream openPhoto(long contactId, Context context) {
                Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
                Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
                Cursor cursor = null;
                try {
                    cursor = context.getContentResolver().query(photoUri,
                            new String[]{ContactsContract.Contacts.Photo.PHOTO}, null, null, null);
                    if (cursor == null) {
                        return null;
                    }
                    if (cursor.moveToFirst()) {
                        byte[] data = cursor.getBlob(0);
                        if (data != null) {
                            return new ByteArrayInputStream(data);
                        }
                    }
                } finally {
                    if(cursor!=null) {
                        cursor.close();
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Bitmap photo) {
                super.onPostExecute(photo);
                if (photo == null) {
                    imageView.setImageResource(R.drawable.ic_default_contact_small);
                } else {
                    imageView.setImageBitmap(photo);
                }
            }
        };
        mContactPhotoTask.execute();
    }

    public void cancel() {
        if (mContactPhotoTask != null) {
            mContactPhotoTask.cancel(true);
            mContactPhotoTask = null;
        }
    }
}

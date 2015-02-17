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

import net.wandroid.answer.R;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Class that handles contact info
 */
public class ContactInfo {

    private String mDisplayName;
    public final String phoneNumber;
    private AsyncTask<Void, Void, Bitmap> mContactPhotoTask;

    /**
     * Initate the Contact Info with the phone number of the contact
     *
     * @param phoneNumber the phone number of the contact
     */
    public ContactInfo(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Loads the contact image that belongs to the contact
     * If the photo could not be loaded, a default image is displayed instead
     *
     * @param imageView the view that the contact photo should be displayed in
     * @param context   the context
     */
    public void loadContactImage(ImageView imageView, Context context) {
        setContactImage(phoneNumber, imageView, context);
    }

    /**
     * Loads the contact image that belongs to the contact
     * If the photo could not be loaded, a default image is displayed instead
     *
     * @param phoneNumber the phone number of the contact
     * @param imageView   the view that the contact photo should be displayed in
     * @param context     the context
     */
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
                        InputStream is = openPhoto(Long.parseLong(id), context);
                        if (is != null) {
                            photo = BitmapFactory.decodeStream(openPhoto(Long.parseLong(id), context));
                            is.close();
                        }
                    }
                } finally {
                    if (crsr != null) {
                        crsr.close();
                    }
                    return photo;
                }
            }

            /**
             * Loads contact photo from a contactId
             * @param contactId the id of the contact
             * @param context the context
             * @return the inputstream for the photo, or null if no stream could be opened.
             */
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
                    if (cursor != null) {
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

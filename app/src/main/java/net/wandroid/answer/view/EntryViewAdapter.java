
package net.wandroid.answer.view;

import net.wandroid.answer.ContactPhoto;
import net.wandroid.answer.ConvertTimeToString;
import net.wandroid.answer.R;
import net.wandroid.answer.providers.ReplyContract;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class EntryViewAdapter extends CursorAdapter {

    private LayoutInflater mInflater;

    public final String DATE_FORMAT;

    private ContentResolver mResolver;

    private Resources mResources;

    public EntryViewAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
        mInflater = LayoutInflater.from(context);
        DATE_FORMAT = context.getResources().getString(R.string.entry_info_time_format);
        mResolver = context.getContentResolver();
        mResources = context.getResources();
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        populateView(view, cursor);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup container) {
        View view = mInflater.inflate(R.layout.entry_list_item, container, false);
        populateView(view, cursor);
        return view;
    }

    private void populateView(View view, Cursor cursor) {

        ContentValues values = ReplyContract.Reply.cursor2Value(cursor);

        Holder holder;
        if (view.getTag() == null) {
            TextView number = (TextView)view.findViewById(R.id.entry_item_contact_number_text);

            TextView info = (TextView)view.findViewById(R.id.entry_item_info_text);

            ImageView active = (ImageView)view.findViewById(R.id.entry_item_active_image);

            ImageView photo = (ImageView)view.findViewById(R.id.entry_item_image);

            LoadContactPhoto loader = new LoadContactPhoto(mResolver, photo);

            holder = new Holder(number, info, photo, active, loader);

            view.setTag(holder);
        } else {
            holder = (Holder)view.getTag();
            // reuse - cancel current download
            holder.loaderTask.cancel(true);
            LoadContactPhoto loader = new LoadContactPhoto(mResolver, holder.photo);
            holder.loaderTask = loader;
        }
        String number = values.getAsString(ReplyContract.Reply.PHONE_NR);
        holder.number.setText(number);
        holder.photo.setImageResource(R.drawable.ic_default_contact_small);

        // TODO: execute on multiple threads
        holder.loaderTask.execute(number);

        long start = values.getAsLong(ReplyContract.Reply.START_TIME);
        long duration = values.getAsLong(ReplyContract.Reply.DURATION);
        ConvertTimeToString convert = new ConvertTimeToString();
        long currentTime = System.currentTimeMillis();
        String infoString;
        if (currentTime < start + duration) {
            infoString = mResources.getString(R.string.entry_view_active_msg_txt)+" "
                    + convert.fromMillisecondsToDateString(start + duration, DATE_FORMAT);
            holder.active.setVisibility(View.VISIBLE);
        } else {
            infoString = mResources.getString(R.string.entry_view_active_msg_txt)+" "
                    + convert.fromMillisecondsToDateString(start + duration, DATE_FORMAT);
            holder.active.setVisibility(View.INVISIBLE);
        }

        holder.info.setText(infoString);
    }

    private class Holder {
        TextView number;

        TextView info;

        ImageView active;

        ImageView photo;

        LoadContactPhoto loaderTask;

        public Holder(TextView number, TextView info, ImageView photo, ImageView active,
                LoadContactPhoto loaderTask) {
            super();
            this.number = number;
            this.info = info;
            this.active = active;
            this.photo = photo;
            this.loaderTask = loaderTask;
        }

    }

    private class LoadContactPhoto extends AsyncTask<String, Void, Uri> {
        private ContentResolver mResolver;

        private ImageView mPhoto;

        public LoadContactPhoto(ContentResolver resolver, ImageView photo) {
            super();
            mResolver = resolver;
            mPhoto = photo;
        }

        @Override
        protected Uri doInBackground(String... number) {
            ContactPhoto contactPhoto = new ContactPhoto();
            String id = contactPhoto.fetchContactIdFromPhoneNumber(number[0], mResolver);
            if (id == null) {
                return null;
            }
            Uri uri = contactPhoto.getPhotoUri(Long.parseLong(id), mResolver);

            return uri;
        }

        @Override
        protected void onPostExecute(Uri result) {
            super.onPostExecute(result);
            if (result != null) {
                // TODO: cache bitmaps
                mPhoto.setImageURI(result);
                if (mPhoto.getDrawable() == null) {
                    mPhoto.setImageResource(R.drawable.ic_default_contact);
                }
            } else {
                mPhoto.setImageResource(R.drawable.ic_default_contact_small);
            }
        }
    }

}


package net.wandroid.answer.view;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.wandroid.answer.ConvertTimeToString;
import net.wandroid.answer.R;
import net.wandroid.answer.contacts.ContactInfo;
import net.wandroid.answer.providers.ReplyContract;

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
        populateView(view, cursor, context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup container) {
        View view = mInflater.inflate(R.layout.entry_list_item, container, false);
        populateView(view, cursor, context);
        return view;
    }

    private void populateView(View view, Cursor cursor, Context context) {

        ContentValues values = ReplyContract.Reply.cursor2Value(cursor);
        String number = values.getAsString(ReplyContract.Reply.PHONE_NR);
        Holder holder;
        if (view.getTag() == null) { // view has not been used before
            TextView numberText = (TextView) view.findViewById(R.id.entry_item_contact_number_text);

            TextView info = (TextView) view.findViewById(R.id.entry_item_info_text);

            ImageView active = (ImageView) view.findViewById(R.id.entry_item_active_image);

            ImageView photo = (ImageView) view.findViewById(R.id.entry_item_image);

            holder = new Holder(numberText, info, photo, active, new ContactInfo(number));

            //start downloading contact photo
            holder.contactInfo.loadContactImage(photo, context);

            view.setTag(holder);
        } else { // view is reused, content might be new
            holder = (Holder) view.getTag();

            if (!holder.contactInfo.phoneNumber.equals(number)) {
                // view and photo doesn't match anymore, cancel current download, start a new
                holder.contactInfo.cancel();
                holder.contactInfo = new ContactInfo(number);
                holder.contactInfo.loadContactImage(holder.photo, context);
            }

        }

        holder.number.setText(number);
        holder.photo.setImageResource(R.drawable.ic_default_contact_small);


        long start = values.getAsLong(ReplyContract.Reply.START_TIME);
        long duration = values.getAsLong(ReplyContract.Reply.DURATION);
        ConvertTimeToString convert = new ConvertTimeToString();
        long currentTime = System.currentTimeMillis();
        String infoString;
        if (currentTime < start + duration) {
            infoString = mResources.getString(R.string.entry_view_active_msg_txt) + " "
                    + convert.fromMillisecondsToDateString(start + duration, DATE_FORMAT);
            holder.active.setVisibility(View.VISIBLE);
        } else {
            infoString = mResources.getString(R.string.entry_view_active_msg_txt) + " "
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

        ContactInfo contactInfo;


        public Holder(TextView number, TextView info, ImageView photo, ImageView active,
                      ContactInfo contactInfo) {
            super();
            this.number = number;
            this.info = info;
            this.active = active;
            this.photo = photo;
            this.contactInfo = contactInfo;
        }

    }

}

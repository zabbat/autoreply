
package net.wandroid.answer.edit;

import android.app.Activity;
import android.app.Fragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import net.wandroid.answer.ConvertTimeToString;
import net.wandroid.answer.R;
import net.wandroid.answer.contacts.ContactInfo;

/**
 * Fragment handling the UI for editing an entry
 */
public class EditEntryFragment extends Fragment implements OnClickListener {

    public static final int NO_ID = -1;

    private Button mRemove;

    private TextView mName;

    private TextView mNumber;

    private TextView mStart;

    private TextView mEnd;

    private TextView mActive;

    private ImageView mActiveImage;

    private ImageView mPhoto;

    private IEditEntryListener mEditEntryListener;

    private long mId = NO_ID;

    private Resources mResources;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_entry_view, container, false);
        mRemove = (Button)view.findViewById(R.id.edit_entry_remove_button);
        mRemove.setOnClickListener(this);

        mName = (TextView)view.findViewById(R.id.edit_entry_name_text);
        mNumber = (TextView)view.findViewById(R.id.edit_entry_number_text);
        mStart = (TextView)view.findViewById(R.id.edit_entry_start_time_text);
        mEnd = (TextView)view.findViewById(R.id.edit_entry_end_time_text);
        mActive = (TextView)view.findViewById(R.id.edit_entry_active_text);

        mActiveImage = (ImageView)view.findViewById(R.id.edit_entry_status_image);

        mPhoto = (ImageView)view.findViewById(R.id.edit_entry_contact_image);
        mPhoto.setImageResource(R.drawable.ic_default_contact);
        mResources = getActivity().getResources();
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    /**
     * Loads the Contact photo
     * @param numberString the contacts phone number
     */
    private void loadPhoto(final String numberString) {
        new ContactInfo(numberString).loadContactImage(mPhoto, getActivity());

    }

    public void setId(long id) {
        mId = id;
    }

    @Override
    public void onClick(View view) {
        if (view == mRemove) {
            mEditEntryListener.onRemoveClicked(mId);
        }
    }

    public void setNumberText(String text) {
        mNumber.setText(text);
        loadPhoto(text);
    }

    /**
     * sets the text when the auto reply started for this contact.
     * The time will be formatted
     * @param ms start time in ms
     */
    public void setStartTimeText(Long ms) {
        String timeFormat = mResources.getString(R.string.info_time_format);
        mStart.setText(mResources.getString(R.string.edit_entry_start_txt)
                + (new ConvertTimeToString().fromMillisecondsToDateString(ms, timeFormat)));
    }

    /**
     * sets the text when the auto reply for this contact
     * will end.The time will be formatted
     * @param ms end time in ms
     */
    public void setEndTimeText(Long ms) {
        String timeFormat = mResources.getString(R.string.info_time_format);
        mEnd.setText(mResources.getString(R.string.edit_entry_end_txt)
                + (new ConvertTimeToString().fromMillisecondsToDateString(ms, timeFormat)));
    }

    /**
     * Displays if the entry is active or expired
     * @param isActive true if the entry is still active, otherwise false
     */
    public void setActiveInfo(boolean isActive) {
        if (isActive) {
            mActive.setText(mResources.getString(R.string.edit_entry_active_txt));
            mActiveImage.setImageResource(R.drawable.ic_active);
            mActiveImage.setVisibility(View.VISIBLE);
        } else {
            mActive.setText(mResources.getString(R.string.edit_entry_expired_txt));
            mActiveImage.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof IEditEntryListener) {
            mEditEntryListener = (IEditEntryListener)activity;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mEditEntryListener = null;
    }

    public interface IEditEntryListener {
        void onRemoveClicked(long id);
    }

}

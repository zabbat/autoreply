
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

public class EditEntryFragment extends Fragment implements OnClickListener {

    private Button mRemove;

    private TextView mName;

    private TextView mNumber;

    private TextView mStart;

    private TextView mEnd;

    private TextView mActive;

    private ImageView mActiveImage;

    private ImageView mPhoto;

    private IEditEntryListener mEditEntryListener = IEditEntryListener.NO_LISTENER;

    private long mId = -1;

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

    private void loadPhoto(final String numberString) {
        new ContactInfo(numberString).setContactImage(mPhoto,getActivity());

    }

    public void setRemoveId(long id) {
        mId = id;
    }

    @Override
    public void onClick(View view) {
        if (view == mRemove) {
            mEditEntryListener.onRemoveClicked(mId);
        }
    }

    public void setNameText(String text) {
        mName.setText(text);
        mName.setVisibility(TextUtils.isEmpty(text) ? View.GONE : View.VISIBLE);
    }

    public void setNumberText(String text) {
        mNumber.setText(text);
        loadPhoto(text);
    }

    public void setStartTimeText(Long ms) {
        String timeFormat = mResources.getString(R.string.info_time_format);
        mStart.setText(mResources.getString(R.string.edit_entry_start_txt)
                + (new ConvertTimeToString().fromMillisecondsToDateString(ms, timeFormat)));
    }

    public void setEndTimeText(Long ms) {
        String timeFormat = mResources.getString(R.string.info_time_format);
        mEnd.setText(mResources.getString(R.string.edit_entry_end_txt)
                + (new ConvertTimeToString().fromMillisecondsToDateString(ms, timeFormat)));
    }

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
        mEditEntryListener = IEditEntryListener.NO_LISTENER;
    }

    public interface IEditEntryListener {
        void onRemoveClicked(long id);

        static final IEditEntryListener NO_LISTENER = new IEditEntryListener() {
            @Override
            public void onRemoveClicked(long id) {
            }
        };
    }

}

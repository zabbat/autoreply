package net.wandroid.answer.add;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import net.wandroid.answer.R;
import net.wandroid.answer.TabTitleFragment.ITabFragment;
import net.wandroid.answer.view.IControllButtonListener;

public class AddOptionsFragment extends Fragment implements OnClickListener, TextWatcher,
        ITabFragment {
    private static final long DURATION_TIME_SCALE = 60 * 60 * 1000; // hours to
    // ms

    private static final int MIN_DURATION = 1;

    private static final long MAX_DURATION = 48;
    private static final String TIME_SLOT = "timeSlot";

    private EditText mNumber;

    private Button mNext;

    private Button mBack;

    private TextView mErrorText;

    private IControllButtonListener mAddOptionsListener = IControllButtonListener.NO_LISTENER;

    private String mTitle;

    private Bundle mAddData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_options_view, container, false);
        mNumber = (EditText) view.findViewById(R.id.add_option_duration_edit);
        mNumber.addTextChangedListener(this);
        mNumber.requestFocus();

        mNext = (Button) view.findViewById(R.id.control_buttons_next_button);
        mNext.setVisibility(View.VISIBLE);
        mNext.setOnClickListener(this);
        mNext.setEnabled(false);

        mBack = (Button) view.findViewById(R.id.control_buttons_back_button);
        mBack.setVisibility(View.VISIBLE);
        mBack.setOnClickListener(this);
        mErrorText = (TextView) view.findViewById(R.id.add_options_error_text);

        mAddData = getArguments();
        String numberText=mAddData.getString(TIME_SLOT,"");
        if(!TextUtils.isEmpty(numberText)){
            mNumber.setText(numberText);
        }

        return view;
    }

    public long getDurationTime() {
        String s = mNumber.getText().toString().trim();
        return Long.parseLong(s) * DURATION_TIME_SCALE;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof IControllButtonListener) {
            mAddOptionsListener = (IControllButtonListener) activity;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mAddOptionsListener = IControllButtonListener.NO_LISTENER;
    }

    @Override
    public void onClick(View view) {
        if (view == mNext) {
            mAddData.putString(TIME_SLOT,mNumber.getText().toString().trim());
            mAddOptionsListener.onNextSlide();
        }
        if (view == mBack) {
            mAddOptionsListener.onSlideBack();
        }

    }

    @Override
    public void afterTextChanged(Editable arg0) {
    }

    @Override
    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
    }

    @Override
    public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        boolean isEmpty = mNumber.getText().toString().trim().isEmpty();
        try {
            boolean isValidTime = getDurationTime() >= MIN_DURATION * DURATION_TIME_SCALE
                    && getDurationTime() <= MAX_DURATION * DURATION_TIME_SCALE;
            boolean isInRange = !isEmpty && isValidTime;
            mNext.setEnabled(isInRange);
            if (!isInRange) {
                mErrorText.setText(getActivity().getResources().getString(R.string.add_options_error_not_in_range_txt));
                mErrorText.setVisibility(View.VISIBLE);
            } else {
                mErrorText.setVisibility(View.INVISIBLE);
            }
        } catch (NumberFormatException e) {
            mNext.setEnabled(false);
        }

    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public void setTitle(String title) {
        mTitle = title;

    }

}


package net.wandroid.answer.add;

import net.wandroid.answer.R;
import net.wandroid.answer.TabTitleFragment.ITabFragment;
import net.wandroid.answer.view.IControllButtonListener;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AddContactFragment extends Fragment implements OnClickListener, TextWatcher,ITabFragment {
    private Button mNext;

    private Button mCancel;

    private EditText mNumber;

    private TextView mErrorText;

    private IControllButtonListener mAddContactListener = IControllButtonListener.NO_LISTENER;

    private String mTitle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_contact_view, container, false);
        mNumber = (EditText)view.findViewById(R.id.add_message_reply_edit);
        mNumber.addTextChangedListener(this);
        mNumber.requestFocus();

        mNext = (Button)view.findViewById(R.id.control_buttons_next_button);
        mNext.setVisibility(View.VISIBLE);
        mNext.setOnClickListener(this);
        mNext.setEnabled(false);

        mCancel = (Button)view.findViewById(R.id.control_buttons_cancel_button);
        mCancel.setVisibility(View.VISIBLE);
        mCancel.setOnClickListener(this);

        mErrorText=(TextView)view.findViewById(R.id.add_contact_error_text);

        return view;
    }

    public String getContactNumber() {
        return mNumber.getText().toString().trim();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof IControllButtonListener) {
            mAddContactListener = (IControllButtonListener)activity;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mAddContactListener = IControllButtonListener.NO_LISTENER;
    }

    @Override
    public void onClick(View view) {
        if (view == mNext) {

            mAddContactListener.onNextSlide();
        }
        if (view == mCancel) {
            mAddContactListener.onCancel();
        }
    }

    @Override
    public void afterTextChanged(Editable text) {
    }

    @Override
    public void beforeTextChanged(CharSequence text, int arg1, int arg2, int arg3) {
    }

    @Override
    public void onTextChanged(CharSequence text, int arg1, int arg2, int arg3) {
        mNext.setEnabled(!getContactNumber().isEmpty());
        if(!getContactNumber().startsWith("+") && !getContactNumber().isEmpty()){
            mErrorText.setText(getActivity().getResources().getString(R.string.add_contact_error_not_countrycode_txt));
            mErrorText.setVisibility(View.VISIBLE);
            mNext.setEnabled(false);
        }else{
            mErrorText.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public void setTitle(String title) {
       mTitle=title;

    }

}

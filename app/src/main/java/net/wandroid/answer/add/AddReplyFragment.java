
package net.wandroid.answer.add;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import net.wandroid.answer.R;
import net.wandroid.answer.TabTitleFragment.ITabFragment;
import net.wandroid.answer.view.IControllButtonListener;

public class AddReplyFragment extends Fragment implements OnClickListener, TextWatcher,
        ITabFragment, OnItemSelectedListener {
    private Button mBack;

    private Button mSave;

    private EditText mMessageEditText;

    private IControllButtonListener mAddReplyListener = IControllButtonListener.NO_LISTENER;

    private String mTitle;

    private Spinner mSpinner;

    private boolean mUseBot;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_reply_view, container, false);
        mBack = (Button)view.findViewById(R.id.control_buttons_back_button);
        mBack.setVisibility(View.VISIBLE);
        mBack.setOnClickListener(this);

        mSave = (Button)view.findViewById(R.id.control_buttons_save_button);
        mSave.setVisibility(View.VISIBLE);
        mSave.setOnClickListener(this);
        mSave.setEnabled(false);

        mMessageEditText = (EditText)view.findViewById(R.id.add_message_reply_edit);
        mMessageEditText.addTextChangedListener(this);

        mSpinner = (Spinner)view.findViewById(R.id.add_reply_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, new String[] {
                        "Bot", "Message"
                });
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(this);
        setRetainInstance(true);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof IControllButtonListener) {
            mAddReplyListener = (IControllButtonListener)activity;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mAddReplyListener = IControllButtonListener.NO_LISTENER;
    }

    public String getMessageText() {
        return mMessageEditText.getText().toString().trim();
    }

    public boolean useBot(){
        return mUseBot;
    }

    @Override
    public void onClick(View view) {
        if (view == mBack) {
            mAddReplyListener.onSlideBack();
        }
        if (view == mSave) {
            mAddReplyListener.onSaveEntry();
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
        mSave.setEnabled(!getMessageText().isEmpty());
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public void setTitle(String title) {
        mTitle = title;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                mMessageEditText.setVisibility(View.GONE);
                mSave.setEnabled(true);
                mUseBot=true;
                break;
            case 1:
                mMessageEditText.setVisibility(View.VISIBLE);
                mMessageEditText.requestFocus();
                mSave.setEnabled(!getMessageText().isEmpty());
                mUseBot=false;
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {

    }

}

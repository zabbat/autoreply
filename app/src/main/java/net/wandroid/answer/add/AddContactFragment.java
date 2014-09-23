
package net.wandroid.answer.add;

import net.wandroid.answer.R;
import net.wandroid.answer.TabTitleFragment.ITabFragment;
import net.wandroid.answer.view.IControllButtonListener;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AddContactFragment extends Fragment implements OnClickListener, TextWatcher,ITabFragment {
    private static final int CONTACT_PICK = 1;
    private Button mNext;

    private Button mCancel;

    private EditText mNumber;

    private TextView mErrorText;

    private IControllButtonListener mAddContactListener = IControllButtonListener.NO_LISTENER;

    private String mTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.add_contact_fragment,menu);
        super.onCreateOptionsMenu(menu, inflater);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_add_contact:
                pickContact();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }



    }

    private void pickContact(){
        Intent intent =new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(intent,CONTACT_PICK);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode!=CONTACT_PICK || resultCode!=Activity.RESULT_OK){
            return;
        }
        String nr="";
        String name="";
        Uri contact = data.getData();

        Cursor c = null;
        try {
            c = getActivity().getContentResolver().query(contact, null, null, null, null);

            if (!c.moveToFirst()) {
                return;
            }

            String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));


            nr = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            name = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

            if (!TextUtils.isEmpty(nr.trim())) {
                mNumber.setText(nr);
            }

        } finally {
            if (c != null) {
                c.close();
            }
        }
        //mName.setText(name);



    }

/*

            if(requestCode!=1 || resultCode!=Activity.RESULT_OK){
            return;
        }
        String nr="?";
        String name="?";
        Uri contact =data.getData();

        Cursor c=null;

        c=getContentResolver().query(contact,null,null,null,null);

        c.moveToFirst();
        Log.d("",DatabaseUtils.dumpCursorToString(c));
        String id =c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
        String hasPhone =c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

        if(hasPhone.equals("1")){
          Cursor contactC=getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                    new String[]{id},null);

            Log.d("",DatabaseUtils.dumpCursorToString(contactC));
            contactC.moveToFirst();
            nr=contactC.getString(contactC.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            name=contactC.getString(contactC.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            contactC.close();

    nr=c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
    name=c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
}
c.close();
        mNumber.setText(nr);
        mName.setText(name);

     */
}
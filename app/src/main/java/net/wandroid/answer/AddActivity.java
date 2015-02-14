
package net.wandroid.answer;





import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;


import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.telephony.PhoneNumberUtils;
import android.view.Menu;
import android.view.MenuItem;

import net.wandroid.answer.TabTitleFragment.ITabFragment;
import net.wandroid.answer.TabTitleFragment.ITabTitleListener;
import net.wandroid.answer.add.AddContactFragment;
import net.wandroid.answer.add.AddOptionsFragment;
import net.wandroid.answer.add.AddReplyFragment;
import net.wandroid.answer.providers.ReplyContentProvider;
import net.wandroid.answer.providers.ReplyContract;
import net.wandroid.answer.view.IControllButtonListener;

public class AddActivity extends ActionBarActivity implements ITabTitleListener, IControllButtonListener {

    private enum eAddFragments {
        ADD_CONTACT, ADD_OPTIONS, ADD_MESSAGE
    }

    private static final String FRAG_TAG = "fragmentTag";

    private static final String CURRENT_PAGE = "currentPage";

    private int mPageIndex = 0;

    private TabTitleFragment mTabTitleFragment;

    private AddReplyFragment mAddReplyFragment;

    private AddContactFragment mAddContactFragment;

    private AddOptionsFragment mAddOptionsFragment;

    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        // Show the Up button in the action bar.
        actionBar.setDisplayHomeAsUpEnabled(true);

        mFragmentManager = getFragmentManager();
        mTabTitleFragment = (TabTitleFragment)mFragmentManager
                .findFragmentById(R.id.add_tab_title_fragment);
        for (int i = 0; i < eAddFragments.values().length; i++) {
            ITabFragment fragment = (ITabFragment)getFragmentAtIndex(i);
            mTabTitleFragment.addTab(fragment);
        }

        if (savedInstanceState != null) {
            mPageIndex = savedInstanceState.getInt(CURRENT_PAGE);
            // FragmentTransaction ft =
            // mFragmentManager.beginTransaction().replace(
            // R.id.add_fragment_layout, getFragmentAtIndex(mPageIndex),
            // FRAG_TAG);
            // ft.commit();
        } else {
            FragmentTransaction ft = mFragmentManager.beginTransaction().add(
                    R.id.add_fragment_layout, getFragmentAtIndex(mPageIndex), FRAG_TAG);
            ft.commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_PAGE, mPageIndex);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
        // TODO: this is cheating, but the fragments gets killed and there's no
        // easy way to save them
    }

    private Fragment getFragmentAtIndex(int index) {
        eAddFragments fragmentType = eAddFragments.values()[index];
        Resources resources = getResources();
        switch (fragmentType) {
            case ADD_CONTACT:
                if (mAddContactFragment == null) {
                    mAddContactFragment = new AddContactFragment();
                    mAddContactFragment.setTitle(resources
                            .getString(R.string.add_contect_tab_title));
                }
                return mAddContactFragment;
            case ADD_OPTIONS:
                if (mAddOptionsFragment == null) {
                    mAddOptionsFragment = new AddOptionsFragment();
                    mAddOptionsFragment
                            .setTitle(resources.getString(R.string.add_option_tab_title));
                }
                return mAddOptionsFragment;
            case ADD_MESSAGE:
                if (mAddReplyFragment == null) {
                    mAddReplyFragment = new AddReplyFragment();
                    mAddReplyFragment.setTitle(resources.getString(R.string.add_message_tab_title));
                }
                return mAddReplyFragment;
            default:
                throw new IllegalArgumentException("No such fragment index:" + index);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabClick(int postion) {

    }

    @Override
    public void onSaveEntry() {
        long start = System.currentTimeMillis();
        String message = mAddReplyFragment.getMessageText();
        String number = PhoneNumberUtils.stripSeparators(mAddContactFragment.getContactNumber());
//        if (!PhoneNumberUtils.isWellFormedSmsAddress(number)) {
//            Toast.makeText(this, "not global", Toast.LENGTH_LONG).show();
//        } else {
////            Toast.makeText(
////                    this,
////                    PhoneNumberUtils.TOA_International + " saved:" + number + ","
////                            + PhoneNumberUtils.toaFromString(number), Toast.LENGTH_LONG).show();
////            Log.d("", "yyy:"+PhoneNumberUtils.+ " saved:" + number + ","
////                    + PhoneNumberUtils.toaFromString(number));
//        }
        long duration = mAddOptionsFragment.getDurationTime();

        ContentValues values = ReplyContract.Reply.createEntry(number, start, duration, message,
                Boolean.toString(mAddReplyFragment.useBot()));

        ContentResolver resolver = getContentResolver();
        resolver.insert(ReplyContentProvider.REPLY_CONTENT_URI, values);
        finish();
    }

    @Override
    public void onSlideBack() {

        if (mPageIndex == 0) {
            return;
        }
        mPageIndex--;
        mFragmentManager.beginTransaction()
                .setCustomAnimations(R.animator.in_right_to_left, R.animator.out_right_to_left)
                .replace(R.id.add_fragment_layout, getFragmentAtIndex(mPageIndex), FRAG_TAG)
                .commit();
        mTabTitleFragment.setSelectedTab(mPageIndex);
    }

    @Override
    public void onNextSlide() {

        if (mPageIndex == eAddFragments.values().length - 1) {
            return;
        }
        mPageIndex++;
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.setCustomAnimations(R.animator.in_left_to_right, R.animator.out_left_to_right);
        ft.replace(R.id.add_fragment_layout, getFragmentAtIndex(mPageIndex), FRAG_TAG);
        ft.commit();
        mTabTitleFragment.setSelectedTab(mPageIndex);

    }

    @Override
    public void onCancel() {
        finish();
    }

}

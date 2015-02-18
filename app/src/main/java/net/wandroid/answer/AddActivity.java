package net.wandroid.answer;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.telephony.PhoneNumberUtils;
import android.view.MenuItem;

import net.wandroid.answer.TabTitleFragment.ITabFragment;
import net.wandroid.answer.TabTitleFragment.ITabTitleListener;
import net.wandroid.answer.add.AddContactFragment;
import net.wandroid.answer.add.AddOptionsFragment;
import net.wandroid.answer.add.AddReplyFragment;
import net.wandroid.answer.providers.ReplyContentProvider;
import net.wandroid.answer.providers.ReplyContract;
import net.wandroid.answer.view.IControllButtonListener;

/**
 * Activity to add a contact to auto reply to
 */
public class AddActivity extends ActionBarActivity implements ITabTitleListener, IControllButtonListener {

    public static final String ADD_DATA = "mAddData";

    private enum eAddFragments {
        ADD_CONTACT, ADD_OPTIONS, ADD_MESSAGE
    }

    private static final String FRAG_TAG = "fragmentTag";

    private static final String CURRENT_PAGE_KEY = "currentPage";

    private int mPageIndex = 0;

    private TabTitleFragment mTabTitleFragment;

    private AddReplyFragment mAddReplyFragment;

    private AddContactFragment mAddContactFragment;

    private AddOptionsFragment mAddOptionsFragment;

    private FragmentManager mFragmentManager;

    private Bundle mAddData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        // Show the Up button in the action bar.
        actionBar.setDisplayHomeAsUpEnabled(true);

        if(savedInstanceState!=null){
            mAddData = savedInstanceState.getBundle("mAddData");
        }else{
            mAddData=new Bundle();
        }

        mFragmentManager = getFragmentManager();

        // initiate the tabs
        mTabTitleFragment = (TabTitleFragment) mFragmentManager.findFragmentById(R.id.add_tab_title_fragment);

        for (int i = 0; i < eAddFragments.values().length; i++) {// add all tab fragments
            ITabFragment fragment = (ITabFragment) getFragmentAtIndex(i);
            mTabTitleFragment.addTab(fragment);
        }

        if (savedInstanceState != null) {
            mPageIndex = savedInstanceState.getInt(CURRENT_PAGE_KEY);
        }

        FragmentTransaction ft = mFragmentManager.beginTransaction().replace(
                R.id.add_fragment_layout, getFragmentAtIndex(mPageIndex), FRAG_TAG);
        ft.commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putInt(CURRENT_PAGE_KEY, mPageIndex);
        outState.putBundle(ADD_DATA,mAddData);
        super.onSaveInstanceState(outState);
    }


    /**
     * Returns the fragment at index, if it will be initiated if it hasn't already been.
     *
     * @param index
     * @return the fragment at index
     */
    private Fragment getFragmentAtIndex(int index) {
        eAddFragments fragmentType = eAddFragments.values()[index];
        Resources resources = getResources();
        switch (fragmentType) {
            case ADD_CONTACT:
                if (mAddContactFragment == null) {
                    mAddContactFragment = new AddContactFragment();
                    mAddContactFragment.setArguments(mAddData);
                    mAddContactFragment.setTitle(resources
                            .getString(R.string.add_contect_tab_title));
                }
                return mAddContactFragment;
            case ADD_OPTIONS:
                if (mAddOptionsFragment == null) {
                    mAddOptionsFragment = new AddOptionsFragment();
                    mAddOptionsFragment.setArguments(mAddData);
                    mAddOptionsFragment
                            .setTitle(resources.getString(R.string.add_option_tab_title));
                }
                return mAddOptionsFragment;
            case ADD_MESSAGE:
                if (mAddReplyFragment == null) {
                    mAddReplyFragment = new AddReplyFragment();
                    mAddReplyFragment.setArguments(mAddData);
                    mAddReplyFragment.setTitle(resources.getString(R.string.add_message_tab_title));
                }
                return mAddReplyFragment;
            default:
                throw new IllegalArgumentException("No such fragment index:" + index);
        }
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
    public void onTabClick(int position) {

    }

    @Override
    public void onSaveEntry() {
        long start = System.currentTimeMillis();
        String message = mAddReplyFragment.getMessageText();
        String number = PhoneNumberUtils.stripSeparators(mAddContactFragment.getContactNumber());

        long duration = mAddOptionsFragment.getDurationTime();

        ContentValues values = ReplyContract.Reply.createEntry(number, start, duration, message,
                Boolean.toString(mAddReplyFragment.useBot()));

        ContentResolver resolver = getContentResolver();
        resolver.insert(ReplyContentProvider.REPLY_CONTENT_URI, values);
        finish();
    }

    @Override
    public void onSlideBack() {

        if (mPageIndex != 0) {

            mPageIndex--;
            mFragmentManager.beginTransaction()
                    .setCustomAnimations(R.animator.in_right_to_left, R.animator.out_right_to_left)
                    .replace(R.id.add_fragment_layout, getFragmentAtIndex(mPageIndex), FRAG_TAG)
                    .commit();
            mTabTitleFragment.setSelectedTab(mPageIndex);
        }
    }

    @Override
    public void onNextSlide() {

        if (mPageIndex < eAddFragments.values().length) {
            mPageIndex++;
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            ft.setCustomAnimations(R.animator.in_left_to_right, R.animator.out_left_to_right);
            ft.replace(R.id.add_fragment_layout, getFragmentAtIndex(mPageIndex), FRAG_TAG);
            ft.commit();
            mTabTitleFragment.setSelectedTab(mPageIndex);
        }
    }

    @Override
    public void onCancel() {
        finish();
    }

}

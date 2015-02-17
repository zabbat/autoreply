package net.wandroid.answer;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Fragment that displays tabs with title text
 */
public class TabTitleFragment extends Fragment implements OnClickListener {

    private static final int NO_VIEW_FOUND = -1;

    private static final float TAB_WEIGHT = 1.0f;

    private static final int START_TAB = 0;

    private int mTabSelectedColor;

    private int mTabDeselectedColor;

    private int mTabSelectedTextColor;

    private int mTabDeselectedTextColor;

    private LinearLayout mLayout;

    private int mSelectedTab;

    private ITabTitleListener mTabTitleListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mTabSelectedColor = getActivity().getResources().getColor(R.color.tab_selected);
        mTabDeselectedColor = getActivity().getResources().getColor(R.color.tab_deselected);
        mTabSelectedTextColor = getActivity().getResources().getColor(R.color.main_color);
        mTabDeselectedTextColor = getActivity().getResources().getColor(R.color.text_color);

        mLayout = (LinearLayout) inflater.inflate(R.layout.tab_title_view, container, false);
        mSelectedTab = START_TAB;
        return mLayout;
    }

    public void addTab(ITabFragment tabFragment) {
        TextView tv = (TextView) View.inflate(getActivity(), R.layout.tab_title_item, null);
        tv.setText(tabFragment.getTitle());
        tv.setOnClickListener(this);
        mLayout.addView(tv, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, TAB_WEIGHT));
        setSelectedTab(mSelectedTab);

    }

    @Override
    public void onClick(View view) {
        int position = getChildPosition(view);
        mTabTitleListener.onTabClick(position);
    }

    private int getChildPosition(View view) {

        for (int i = 0; i < mLayout.getChildCount(); i++) {
            if (mLayout.getChildAt(i) == view) {
                return i;
            }
        }
        return NO_VIEW_FOUND;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof ITabTitleListener) {
            mTabTitleListener = (ITabTitleListener) activity;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mTabTitleListener = null;
    }

    public void setSelectedTab(int position) {
        mSelectedTab = position;
        for (int i = 0; i < mLayout.getChildCount(); i++) {
            View v = mLayout.getChildAt(i);
            if (mSelectedTab == i) {
                v.setBackgroundColor(mTabSelectedColor);
                if (v instanceof TextView) {
                    ((TextView) v).setTextColor(mTabSelectedTextColor);
                }
            } else {
                v.setBackgroundColor(mTabDeselectedColor);
                if (v instanceof TextView) {
                    ((TextView) v).setTextColor(mTabDeselectedTextColor);
                }
            }
        }
    }

    public interface ITabTitleListener {
        /**
         * callback when a tab is clicked
         *
         * @param position the index position of the tab
         */
        void onTabClick(int position);
    }

    public interface ITabFragment {
        String getTitle();

        void setTitle(String title);
    }

}

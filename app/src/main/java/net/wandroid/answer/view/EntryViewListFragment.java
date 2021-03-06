package net.wandroid.answer.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import com.melnykov.fab.FloatingActionButton;

import net.wandroid.answer.EditActivity;
import net.wandroid.answer.R;
import net.wandroid.answer.providers.ReplyContentProvider;
import net.wandroid.answer.providers.ReplyContract;

/**
 * ListFragment for entries
 */
public class EntryViewListFragment extends ListFragment implements
        LoaderManager.LoaderCallbacks<Cursor>, EntryViewAdapter.IItemSelectedListener {


    public interface IEntryViewListener {
        void onFabClicked();
    }

    private IEntryViewListener mEntryViewListener;

    private static final int LOADER_ID = 0;

    /**
     * 3rd party implementation, see license.txt
     */
    private FloatingActionButton mFab;

    private LoaderManager.LoaderCallbacks<Cursor> mLoaderCallBacks;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.entry_list_view, container, false);
        mFab = (FloatingActionButton) view.findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEntryViewListener.onFabClicked();
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mFab.attachToListView(getListView());

        EntryViewAdapter adapter = new EntryViewAdapter(getActivity(), null, false, this);

        setListAdapter(adapter);

        getListView().setEmptyView(view.findViewById(R.id.empty));

        mLoaderCallBacks = this;
        LoaderManager manager = getLoaderManager();
        manager.initLoader(LOADER_ID, savedInstanceState, mLoaderCallBacks);
    }

    @Override
    public void onListItemClick(long id) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), EditActivity.class);
        intent.putExtra(EditActivity.ENTRY_DB_ID, id);
        startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        if (id == LOADER_ID) {
            return new CursorLoader(getActivity(), ReplyContentProvider.REPLY_CONTENT_URI, null,
                    null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor newCursor) {
        ((CursorAdapter) getListAdapter()).swapCursor(newCursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        ((CursorAdapter) getListAdapter()).swapCursor(null);
    }

    public void removeAllExpired() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Resources resourses = getResources();
        builder.setMessage(resourses.getString(R.string.entry_view_delete_all_dialog_txt))
                .setPositiveButton(resourses.getString(R.string.yes_no_dialog_positive),
                        new OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                removeAllExpiredEntries(System.currentTimeMillis());
                            }
                        })
                .setNegativeButton(resourses.getString(R.string.yes_no_dialog_negative), null)
                .show();

    }

    private void removeAllExpiredEntries(final long currentTimeMillis) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... arg) {
                ReplyContract.Reply.removeAllExpiredEntries(currentTimeMillis, getActivity()
                        .getContentResolver());
                return null;
            }

            protected void onPostExecute(Void result) {
            }

            ;
        }.execute();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof IEntryViewListener) {
            mEntryViewListener = (IEntryViewListener) activity;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mEntryViewListener = null;
    }
}

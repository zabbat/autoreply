
package net.wandroid.answer;

import net.wandroid.answer.edit.EditEntryFragment;
import net.wandroid.answer.edit.EditEntryFragment.IEditEntryListener;
import net.wandroid.answer.providers.ReplyContentProvider;
import net.wandroid.answer.providers.ReplyContract;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class EditActivity extends Activity implements IEditEntryListener {

    public static final String ENTRY_DB_ID = "entry_db_id";

    private LoadEntryViewTask mLoadEntryViewTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        // Show the Up button in the action bar.
        setupActionBar();
        Intent intent = getIntent();

        if (intent != null) {
            long id = intent.getLongExtra(ENTRY_DB_ID, -1);
            if (id < 0) {
                Toast.makeText(getApplicationContext(), "No such entry", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
            mLoadEntryViewTask = new LoadEntryViewTask(id);
            mLoadEntryViewTask.execute();
        }

    }

    private void initEntryFragment(long id, ContentValues values) {
        FragmentManager manager = getFragmentManager();
        EditEntryFragment fragment = (EditEntryFragment)manager
                .findFragmentById(R.id.edit_activity_entry_fragment);
        fragment.setRemoveId(id);
        // fragment.setNameText(values.getAsString(ReplyContract.Reply.PHONE_NR));
        fragment.setNumberText(values.getAsString(ReplyContract.Reply.PHONE_NR));
        long startTime = values.getAsLong(ReplyContract.Reply.START_TIME);
        long duration = values.getAsLong(ReplyContract.Reply.DURATION);
        fragment.setStartTimeText(startTime);
        fragment.setEndTimeText(startTime + duration);
        long currentTime = System.currentTimeMillis();
        fragment.setActiveInfo(currentTime < startTime + duration);
    }

    /**
     * Set up the {@link android.app.ActionBar}.
     */
    private void setupActionBar() {

        getActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit, menu);
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
    public void onRemoveClicked(final long id) {
        Resources resources = getResources();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(resources.getString(R.string.edit_entry_delete_dialog_txt))
                .setPositiveButton(resources.getString(R.string.yes_no_dialog_positive),
                        new OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                removeEntry(id);
                            }
                        })
                .setNegativeButton(resources.getString(R.string.yes_no_dialog_negative), null)
                .show();
    }

    private void removeEntry(long id) {
        ContentResolver resolver = getContentResolver();
        int nr = resolver.delete(ReplyContentProvider.REPLY_CONTENT_URI, BaseColumns._ID + " = ?",
                new String[] {
                    Long.toString((id))
                });

        if (nr < 1) {
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.edit_entry_failed_delete_txt),
                    Toast.LENGTH_LONG).show();
        } else {
            finish();
        }
    }

    private class LoadEntryViewTask extends AsyncTask<Void, Void, ContentValues> {
        private final long mId;

        public LoadEntryViewTask(long id) {
            super();
            mId = id;
        }

        @Override
        protected ContentValues doInBackground(Void... arg) {
            ContentResolver resolver = getContentResolver();
            Cursor c = null;
            try {
                c = resolver.query(ReplyContentProvider.REPLY_CONTENT_URI, null,
                        ReplyContract.Reply.SELECT_BY_ID, new String[] {
                            Long.toString(mId)
                        }, null);
                c.moveToFirst();
                ContentValues values = ReplyContract.Reply.cursor2Value(c);

                return values;
            } finally {
                if (c != null) {
                    c.close();
                }
            }
        }

        @Override
        protected void onPostExecute(ContentValues result) {
            super.onPostExecute(result);
            initEntryFragment(mId, result);
        }

    }

}


package net.wandroid.answer.tests;


import net.wandroid.answer.providers.ReplyContentProvider;
import net.wandroid.answer.providers.ReplyContract;
import net.wandroid.answer.view.EntryViewListFragment;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.test.ProviderTestCase2;
import android.util.Log;

public class ReplyContentProviderTests extends ProviderTestCase2<ReplyContentProvider> {

    private ContentResolver mResolver;

    private ContentValues mValues;

    private String mNr = "+46708657788";

    private long startTime = 1;

    private long duration = 60000;

    private String message = "message";

    private String bot = "bot";

    public ReplyContentProviderTests() {
        super(ReplyContentProvider.class, ReplyContentProvider.AUTHORITY);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mContext = getMockContext();
        mResolver = getMockContentResolver();
        mValues = ReplyContract.Reply.createEntry(mNr, startTime, duration, message, bot);

    }

    public void test_Insert() {
        Uri uri = mResolver.insert(ReplyContentProvider.REPLY_CONTENT_URI, mValues);
        assertNotNull(uri);
        long id = Long.parseLong(uri.getLastPathSegment());
        assertTrue(id >= 0);
    }

    // test that if creatEntry sets contentvalue to its params
    public void test_create_entry() {

        ContentValues values = ReplyContract.Reply.createEntry(mNr, startTime, duration, message,
                bot);
        assertEquals(mNr, values.getAsString(ReplyContract.Reply.PHONE_NR));
        assertTrue(values.getAsLong(ReplyContract.Reply.START_TIME) == startTime);
        assertTrue(values.getAsLong(ReplyContract.Reply.DURATION) == duration);
        assertEquals(message, values.getAsString(ReplyContract.Reply.MESSAGE));
        assertEquals(bot, values.getAsString(ReplyContract.Reply.BOT));
    }

    // test that if creatEntry sets number,start or duration to null,0, or
    // empty, an illegalArgument is thrown
    public void test_cannot_create_entry() {
        ContentValues values = null;
        try {
            values = ReplyContract.Reply.createEntry(null, startTime, duration, message, bot);
            values = ReplyContract.Reply.createEntry("", startTime, duration, message, bot);
            values = ReplyContract.Reply.createEntry(mNr, 0, duration, message, bot);
            values = ReplyContract.Reply.createEntry(mNr, startTime, 0, message, bot);
            fail();
        } catch (IllegalArgumentException e) {
        }
        assertNull(values);
    }

    // test that if insert a valid entry in an empty db, the entry can be
    // fetched by querry
    public void test_querry_inserted_entry() {
        Cursor c = null;
        try {
            c = mResolver.query(ReplyContentProvider.REPLY_CONTENT_URI, null, null, null, null);
            assertTrue(c.getCount() == 0);
            c.close();

            mResolver.insert(ReplyContentProvider.REPLY_CONTENT_URI, mValues);
            c = mResolver.query(ReplyContentProvider.REPLY_CONTENT_URI, null, null, null, null);
            assertTrue(c.getCount() == 1);
            c.moveToFirst();
            ContentValues values = ReplyContract.Reply.cursor2Value(c);
            c.close();
            assertTrue(ReplyContract.Reply.equals(values, mValues));

        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
            }
        }

    }

    // test that two different entries is not equal when converting to values
    public void test_inserted_entry_not_equal_other_entry() {
        Cursor c = null;
        try {
            mResolver.insert(ReplyContentProvider.REPLY_CONTENT_URI, mValues);
            mResolver.insert(ReplyContentProvider.REPLY_CONTENT_URI,
                    ReplyContract.Reply.createEntry("123456", 2, 3, "no", "any"));
            c = mResolver.query(ReplyContentProvider.REPLY_CONTENT_URI, null, null, null, null);
            c.moveToFirst();
            ContentValues values1 = ReplyContract.Reply.cursor2Value(c);
            c.moveToNext();
            ContentValues values2 = ReplyContract.Reply.cursor2Value(c);
            c.close();
            assertFalse(ReplyContract.Reply.equals(values1, values2));

        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
            }
        }

    }

    // test that two values with same content is equal
    public void test_same_content_is_equal() {
        assertTrue(ReplyContract.Reply.equals(
                ReplyContract.Reply.createEntry("123456", 2, 3, "no", "any"),
                ReplyContract.Reply.createEntry("123456", 2, 3, "no", "any")));
    }

    // test that it is possible to delete an entry
    public void test_delete_entry() {
        mResolver.insert(ReplyContentProvider.REPLY_CONTENT_URI, mValues);
        int nr = mResolver.delete(ReplyContentProvider.REPLY_CONTENT_URI, null, null);
        assertTrue(nr == 1);
        Cursor c = null;
        try {
            c = mResolver.query(ReplyContentProvider.REPLY_CONTENT_URI, null, null, null, null);
            assertTrue(c.getCount() == 0);
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
            }
        }
    }

    // test that it is possible to delete a specific entry among multiple
    // entries
    public void test_delete_specific_entry() {
        mResolver.insert(ReplyContentProvider.REPLY_CONTENT_URI, mValues);
        ContentValues values = ReplyContract.Reply.createEntry("123456", 2, 3, "no", "any");
        mResolver.insert(ReplyContentProvider.REPLY_CONTENT_URI, values);
        int nr = mResolver.delete(ReplyContentProvider.REPLY_CONTENT_URI,
                ReplyContract.Reply.PHONE_NR + " = ?", new String[] {
                    mNr
                });
        assertTrue(nr == 1);
        Cursor c = null;
        try {
            c = mResolver.query(ReplyContentProvider.REPLY_CONTENT_URI, null, null, null, null);
            assertTrue(c.getCount() == 1);
            c.moveToFirst();
            ReplyContract.Reply.equals(ReplyContract.Reply.cursor2Value(c), values);
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
            }
        }
    }

    // test that it is possible to update a specific entry
    public void test_update_entry() {
        mResolver.insert(ReplyContentProvider.REPLY_CONTENT_URI, mValues);
        ContentValues values = ReplyContract.Reply.createEntry("123", 2, 3, "hi", "any");
        int nr = mResolver.update(ReplyContentProvider.REPLY_CONTENT_URI, values,
                ReplyContract.Reply.PHONE_NR + " = ?", new String[] {
                    mNr
                });
        assertTrue(nr == 1);
        Cursor c = null;
        try {
            c = mResolver.query(ReplyContentProvider.REPLY_CONTENT_URI, null, null, null, null);
            c.moveToFirst();
            assertTrue(ReplyContract.Reply.equals(ReplyContract.Reply.cursor2Value(c), values));
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
            }
        }
    }

    // test that it is possible to update multiple entries
    public void test_update_entries() {
        mResolver.insert(ReplyContentProvider.REPLY_CONTENT_URI, mValues);
        mResolver.insert(ReplyContentProvider.REPLY_CONTENT_URI, mValues);
        ContentValues values = ReplyContract.Reply.createEntry("123", 2, 3, "hi", "any");
        int nr = mResolver.update(ReplyContentProvider.REPLY_CONTENT_URI, values, null, null);
        assertTrue(nr == 2);
        Cursor c = null;
        try {
            c = mResolver.query(ReplyContentProvider.REPLY_CONTENT_URI, null, null, null, null);
            c.moveToFirst();
            assertTrue(ReplyContract.Reply.equals(ReplyContract.Reply.cursor2Value(c), values));
            c.moveToNext();
            assertTrue(ReplyContract.Reply.equals(ReplyContract.Reply.cursor2Value(c), values));
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
            }
        }
    }

    // test if expired selection can find all expired entries
    public void test_no_expired_selections() {
        mResolver.insert(ReplyContentProvider.REPLY_CONTENT_URI, mValues);
        long expireTime = startTime + duration - 1;
        int nr = ReplyContract.Reply.removeAllExpiredEntries(expireTime, mResolver);
        assertTrue(nr == 0);
    }

    // test if expired selection can find all expired entries
    public void test_find_expired_selection() {
        mResolver.insert(ReplyContentProvider.REPLY_CONTENT_URI,
                ReplyContract.Reply.createEntry("123456", startTime, duration, "hello", ""));
        long expireTime = startTime + duration + 1;
        int nr = ReplyContract.Reply.removeAllExpiredEntries(expireTime, mResolver);
        assertTrue(nr == 1);
    }

    // test if expired selection can find all expired entries
    public void test_find_expired_selections() {
        mResolver.insert(ReplyContentProvider.REPLY_CONTENT_URI, mValues);
        mResolver.insert(ReplyContentProvider.REPLY_CONTENT_URI,
                ReplyContract.Reply.createEntry("123456", startTime, duration - 2, "hello", ""));
        long expireTime = startTime + duration - 1;
        int nr = ReplyContract.Reply.removeAllExpiredEntries(expireTime, mResolver);
        assertTrue(nr == 1);
    }

}

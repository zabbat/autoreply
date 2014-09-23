
package net.wandroid.answer.providers;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class ReplyContentProvider extends ContentProvider {

    public final static String AUTHORITY = "net.wandroid.answer.provider";

    public final static Uri CONTENT_URI = Uri.parse(AUTHORITY);

    public final static Uri REPLY_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/"
            + ReplyContract.Reply.REPLY_TABLE);

    private final static int REPLY = 1;

    private final static int REPLY_ID = 2;

    private SQLiteOpenHelper mDatabaseHelper;

    private final static UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(AUTHORITY, ReplyContract.Reply.REPLY_TABLE, 1);
        sUriMatcher.addURI(AUTHORITY, ReplyContract.Reply.REPLY_TABLE + "/#", 2);
    }

    @Override
    public int delete(Uri uri, String where, String[] selectionArgs) {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        int result;
        switch (sUriMatcher.match(uri)) {
            case REPLY:
                result = db.delete(ReplyContract.Reply.REPLY_TABLE, where, selectionArgs);
                break;
            case REPLY_ID:
            default:
                db.close();
                throw new IllegalArgumentException("No uri match for insert");
        }
        if (result > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return result;
    }

    @Override
    public String getType(Uri arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        long result;
        switch (sUriMatcher.match(uri)) {
            case REPLY:
                result = db.insert(ReplyContract.Reply.REPLY_TABLE, null, values);
                break;
            case REPLY_ID:
            default:
                throw new IllegalArgumentException("No uri match for insert");
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(ReplyContract.Reply.REPLY_TABLE + "/" + result);
    }

    @Override
    public boolean onCreate() {
        mDatabaseHelper = new ReplyDatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        final SQLiteQueryBuilder query = new SQLiteQueryBuilder();

        Cursor cursor;
        switch (sUriMatcher.match(uri)) {
            case REPLY:
                query.setTables(ReplyContract.Reply.REPLY_TABLE);
                break;
            case REPLY_ID:
                // query.appendWhere(BaseColumns._ID + "=" +
                // uri.getLastPathSegment());
            default:
                throw new IllegalArgumentException("No uri match for insert");
        }
        cursor = query.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        int result;
        switch (sUriMatcher.match(uri)) {
            case REPLY:
                result = db.update(ReplyContract.Reply.REPLY_TABLE, values, where, whereArgs);
                break;
            case REPLY_ID:
            default:
                db.close();
                throw new IllegalArgumentException("No uri match for update");
        }
        if (result > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return result;
    }

}

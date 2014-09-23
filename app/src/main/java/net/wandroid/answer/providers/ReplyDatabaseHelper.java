
package net.wandroid.answer.providers;
import static net.wandroid.answer.providers.ReplyContract.Reply.*;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class ReplyDatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "reply.db";

    public static final int DATABASE_VERSION = 1;


    private static final String CREATE_DATABAS = "CREATE TABLE " + REPLY_TABLE +
            " ( "+BaseColumns._ID+" integer primary key autoincrement, "+PHONE_NR+" TEXT, "+
            START_TIME+" integer, "+DURATION+" integer, "+MESSAGE+" text, "+BOT+" text" +" );";



    public ReplyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DATABAS);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}

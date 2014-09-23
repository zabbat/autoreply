
package net.wandroid.answer.providers;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ReplyContract {

    public static class Reply {
        public static final String REPLY_TABLE = "reply";

        public static final String PHONE_NR = "phone_nr";

        public static final String START_TIME = "start_time";

        public static final String DURATION = "duration";

        public static final String MESSAGE = "message";

        public static final String BOT = "bot";

        private static final String TAG = ReplyContract.class.getName();

        public static final String[] PROJECTION_BY_PHONE = new String[] {
            PHONE_NR
        };

        public static final String SELECT_BY_PHONE = PHONE_NR + "= ?";

        public static final String SELECT_BY_ID = BaseColumns._ID + "= ?";

        public static final String[] PROJECTION_MESSAGE_BOT_START_DURATION = new String[] {
                MESSAGE, BOT, START_TIME, DURATION
        };

        public static ContentValues createEntry(String nr, long startTime, long duration,
                String message, String bot) {
            if (nr == null || nr.isEmpty()) {
                throw new IllegalArgumentException("number must not be null or empty");
            }
            if (startTime == 0) {
                throw new IllegalArgumentException("start time must not be 0");
            }
            if (duration == 0) {
                throw new IllegalArgumentException("duration must not be 0");
            }

            ContentValues values = new ContentValues();
            values.put(PHONE_NR, nr);
            values.put(START_TIME, startTime);
            values.put(DURATION, duration);
            values.put(MESSAGE, message);
            values.put(BOT, bot);

            return values;
        }

        public static ContentValues cursor2Value(Cursor c) {
            ContentValues values = new ContentValues();
            int indexNr = c.getColumnIndexOrThrow(ReplyContract.Reply.PHONE_NR);
            int indexStart = c.getColumnIndexOrThrow(ReplyContract.Reply.START_TIME);
            int indexDuration = c.getColumnIndexOrThrow(ReplyContract.Reply.DURATION);
            int indexMessage = c.getColumnIndexOrThrow(ReplyContract.Reply.MESSAGE);
            int indexBot = c.getColumnIndexOrThrow(ReplyContract.Reply.BOT);

            values.put(ReplyContract.Reply.PHONE_NR, c.getString(indexNr));
            values.put(ReplyContract.Reply.START_TIME, c.getLong(indexStart));
            values.put(ReplyContract.Reply.DURATION, c.getLong(indexDuration));
            values.put(ReplyContract.Reply.MESSAGE, c.getString(indexMessage));
            values.put(ReplyContract.Reply.BOT, c.getString(indexBot));

            return values;
        }

        public static boolean equals(ContentValues values1, ContentValues values2) {
            Set<String> set1 = values1.keySet();
            if (set1.size() != values2.keySet().size()) {
                Log.d(TAG, "key sets' sizes is not equal");
                return false;
            }

            for (String s : set1) {
                if (!values2.containsKey(s)
                        || !values1.getAsString(s).equals(values2.getAsString(s))) {
                    Log.d(TAG, "key " + s + " is not valid");
                    Log.d(TAG, "value1 " + values1.get(s) + ", value2 " + values2.get(s));
                    return false;
                }
            }

            return true;
        }

        public static int removeAllExpiredEntries(long currentTime, ContentResolver resolver) {
            // TODO: find out how to do do
            // "delete starttime+duration>currentTime"

            Cursor c = resolver.query(ReplyContentProvider.REPLY_CONTENT_URI, null, null, null,
                    null);
            List<Long> removeList = new ArrayList<Long>();
            c.moveToFirst();
            while (!c.isAfterLast()) {
                ContentValues values = cursor2Value(c);
                if (values.getAsLong(START_TIME) + values.getAsLong(DURATION) < currentTime) {
                    removeList.add(c.getLong(c.getColumnIndex(BaseColumns._ID)));

                }
                c.moveToNext();
            }
            c.close();
            for (long l : removeList) {
                resolver.delete(ReplyContentProvider.REPLY_CONTENT_URI, BaseColumns._ID + " = ?",
                        new String[] {
                            Long.toString(l)
                        });
            }
            return removeList.size();
        }

    }

}


package net.wandroid.answer.sms;


import net.wandroid.answer.bot.BotJson;
import net.wandroid.answer.preferences.SmsSharedPreference;
import net.wandroid.answer.providers.ReplyContentProvider;
import net.wandroid.answer.providers.ReplyContract;

import org.apache.http.HttpException;
import org.apache.http.client.ClientProtocolException;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import java.io.IOException;

public class SmsBroadCastReceiver extends BroadcastReceiver {

    private static final int MAX_NR_SMS = 2;

    private static final int MAX_TIME = 20 * 1000;

    private static final String TAG = SmsBroadCastReceiver.class.getName();

    private StringBuffer mApiKey;

    private static final String CHATBOT_ID = "754";

    @Override
    public void onReceive(final Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null && bundle.containsKey("pdus")) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            final SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdus[0]);
            final String senderNumber = sms.getOriginatingAddress();

            SmsSharedPreference preferences = new SmsSharedPreference(context, MAX_TIME, MAX_NR_SMS);
            long timeStamp = System.currentTimeMillis();
            if (preferences.canInc(timeStamp)) {
                preferences.incSmsCount(timeStamp);
                final ContentResolver resolver = context.getContentResolver();

                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        if (hasAutoReply(resolver, senderNumber)) {
                            mApiKey = new StringBuffer(context.getResources().getString(
                                    net.wandroid.answer.R.string.ai_apikey));

                            generateAutoReply(resolver, senderNumber, sms.getMessageBody());
                        }
                        return null;
                    }
                }.execute();

            }
        } else {
            Log.d(TAG, "could not inc");
        }

    }


    private synchronized void generateAutoReply(ContentResolver resolver, final String senderNumber,
                                                final String senderMessage) {
        Log.d(TAG, "auto sending to: " + senderNumber + ": '" + senderMessage + "'");

        String message = null;
        String bot = null;
        Cursor c = null;
        try {
            c = resolver.query(ReplyContentProvider.REPLY_CONTENT_URI,
                    ReplyContract.Reply.PROJECTION_MESSAGE_BOT_START_DURATION,
                    ReplyContract.Reply.SELECT_BY_PHONE, new String[]{
                            senderNumber
                    }, null);

            if (c.getCount() <= 0) {
                return;
            }

            c.moveToFirst();
            long start = c.getLong(c.getColumnIndexOrThrow(ReplyContract.Reply.START_TIME));
            long duration = c.getLong(c.getColumnIndexOrThrow(ReplyContract.Reply.DURATION));

            if (System.currentTimeMillis() > start + duration) { //expired, don't use
                return;
            }

            message = null;
            bot = c.getString(c.getColumnIndex(ReplyContract.Reply.BOT));
            message = c.getString(c.getColumnIndex(ReplyContract.Reply.MESSAGE));
        } finally {
            if (c != null) {
                c.close();
            }
        }
        if (!Boolean.parseBoolean(bot)) {
            sendTextMessage(senderNumber, message);
        } else {

            BotJson jsonbot = new BotJson();
            try {
                message = jsonbot.getResponse(senderMessage, mApiKey.toString(), CHATBOT_ID).toString();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (HttpException e) {
                e.printStackTrace();
            }
            sendTextMessage(senderNumber, message);
        }
    }

    private void sendTextMessage(String senderNumber, String message) {
        if (message == null) {
            return;
        }
        if (message.length() > 139) {
            message = message.substring(0, 139);
        }
        SmsManager smsManager = SmsManager.getDefault();
        // TODO: note <api 19 need to save sms
        smsManager.sendTextMessage(senderNumber, null, message, null, null);
        Log.d(TAG, "auto sent message: " + message);
    }

    private boolean hasAutoReply(ContentResolver resolver, String senderNumber) {
        Cursor c = resolver.query(ReplyContentProvider.REPLY_CONTENT_URI,
                ReplyContract.Reply.PROJECTION_BY_PHONE, ReplyContract.Reply.SELECT_BY_PHONE,
                new String[]{
                        senderNumber
                }, null);
        try {
            return c.getCount() > 0;
        } finally {
            if (c != null) {
                c.close();
            }
        }

    }

}

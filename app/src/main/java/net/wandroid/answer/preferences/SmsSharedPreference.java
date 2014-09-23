
package net.wandroid.answer.preferences;

import net.wandroid.answer.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

public class SmsSharedPreference {
    public static final int MAX_NUMBER_SMS = 100;

    private final SharedPreferences mSharedPreferences;

    private final Resources mResources;

    private final long mMaxTime;

    private final int mMaxSms;

    public SmsSharedPreference(Context context, long maxTime, int maxSms) {
        mResources = context.getResources();
        mSharedPreferences = context.getSharedPreferences(
                mResources.getString(R.string.sms_preferences), Context.MODE_PRIVATE);
        mMaxTime = maxTime;
        mMaxSms = maxSms;
    }

    public void reset() {
        mSharedPreferences.edit().clear().commit();
    }

    public int getNumberOfSms() {
        int nr = mSharedPreferences.getInt(
                mResources.getString(R.string.sms_preferences_nr_sms_key), 0);
        return nr;
    }

    public void incSmsCount(long timeStamp) {
        if (getCurrentTimeStamp() + mMaxTime < timeStamp) {
            reset();
        }

        int nr = getNumberOfSms();
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.putInt(mResources.getString(R.string.sms_preferences_nr_sms_key), nr + 1);

        edit.putLong(mResources.getString(R.string.sms_preferences_sms_time_stamp_key), timeStamp);

        edit.commit();
    }

    public long getCurrentTimeStamp() {
        long time = mSharedPreferences.getLong(
                mResources.getString(R.string.sms_preferences_sms_time_stamp_key), 0);
        return time;
    }

    public boolean canInc(long timeStamp) {
        if (getNumberOfSms() >= MAX_NUMBER_SMS) {
            return false;
        }
        return getNumberOfSms() < mMaxSms || timeStamp > getCurrentTimeStamp() + mMaxTime;
    }

}

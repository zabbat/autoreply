package net.wandroid.answer.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import net.wandroid.answer.R;

/**
 * Class that handles sms shared preference values
 */
public class SmsSharedPreference {

    /**
     * Maximum sms possible to send in one day. Capped to not spam.
     */
    public static final int MAX_NUMBER_SMS = 100;

    private final SharedPreferences mSharedPreferences;

    private final Resources mResources;

    /**
     * To avoid loops and other spamming scenarios the app cannot send
     * more than mMaxSms number of sms during a time period of mMaxTime ms.
     * This is to avoid for an example two different devices both having
     * each other listed to auto reply to each other.
     */
    private final long mMaxTime;
    private final int mMaxSms;

    /**
     * Constructor
     *
     * @param context The context
     * @param maxTime time window in ms that you can send maxSms number of sms
     * @param maxSms  maximum number of sms you can send during maxTime ms
     */
    public SmsSharedPreference(Context context, long maxTime, int maxSms) {
        mResources = context.getResources();
        mSharedPreferences = context.getSharedPreferences(
                mResources.getString(R.string.sms_preferences), Context.MODE_PRIVATE);
        mMaxTime = maxTime;
        mMaxSms = maxSms;
    }

    /**
     * Clears all entries
     */
    public void reset() {
        mSharedPreferences.edit().clear().commit();
    }

    /**
     * get number of sent sms by auto reply
     *
     * @return number of sent sms
     */
    public int getNumberOfSms() {
        int nr = mSharedPreferences.getInt(
                mResources.getString(R.string.sms_preferences_nr_sms_key), 0);
        return nr;
    }

    /**
     * Increase the number of sent sms by 1.
     * If the time the sms was sent is after the mMaxTime limit, the count will be reseted to 0
     * (Resetting the cool down)
     *
     * @param timeStamp
     */
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

    /**
     * get the start timestamp for the cool down
     *
     * @return the start time stamp in ms, or 0 if there is no timestamp saved
     */
    public long getCurrentTimeStamp() {
        long time = mSharedPreferences.getLong(
                mResources.getString(R.string.sms_preferences_sms_time_stamp_key), 0);
        return time;
    }

    /**
     * Checks if it is ok to send more sms
     *
     * @param timeStamp the current time in ms
     * @return true if it is ok to send more sms, false if there need to be a cool down
     */
    public boolean canInc(long timeStamp) {
        if (getNumberOfSms() >= MAX_NUMBER_SMS) {
            return false;
        }
        return getNumberOfSms() < mMaxSms || timeStamp > getCurrentTimeStamp() + mMaxTime;
    }

}

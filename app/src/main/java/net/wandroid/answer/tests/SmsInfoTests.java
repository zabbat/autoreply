
package net.wandroid.answer.tests;

import android.content.Context;
import android.test.InstrumentationTestCase;

import net.wandroid.answer.preferences.SmsSharedPreference;

public class SmsInfoTests extends InstrumentationTestCase {

    private static final long TIME_STAMP = 1;

    private Context mContext;

    private SmsSharedPreference mSharedPreference;

    private final static long MAX_TIME = 60000;

    private final static int MAX_SMS = 10;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mContext = getInstrumentation().getTargetContext();
        mSharedPreference = new SmsSharedPreference(mContext, MAX_TIME, MAX_SMS);
        mSharedPreference.reset();

    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        mSharedPreference.reset();
    }

    // test that it is possible to read number of sms from and empty
    // SmsSharedPreferences
    public void test_save_nr_sms() {
        int nr = mSharedPreference.getNumberOfSms();
        assertTrue(nr == 0);
    }

    // test that is possible to increase the number of messages in sms
    // preferences

    public void test_inc_sms_count() {

        mSharedPreference.incSmsCount(TIME_STAMP);
        int nr = mSharedPreference.getNumberOfSms();
        assertTrue("nr:" + nr, nr == 1);
    }

    // test that it is possilbe to reset shared preferences so that all values
    // are lost
    public void test_reset_pref() {
        mSharedPreference.incSmsCount(TIME_STAMP);
        mSharedPreference.reset();
        int nr = mSharedPreference.getNumberOfSms();
        assertTrue(nr == 0);
        long time = mSharedPreference.getCurrentTimeStamp();
        assertTrue(time == 0);
    }

    // test that time stamp is zero when no sms is present
    public void test_no_time_stamp_on_start() {
        long time = mSharedPreference.getCurrentTimeStamp();
        assertTrue(time == 0);
    }

    // test that time stamp is same as first sms
    public void test_time_stamp_equal_to_first_sms() {
        long currTime = 1000L;
        mSharedPreference.incSmsCount(currTime);
        long time = mSharedPreference.getCurrentTimeStamp();
        assertTrue(time == currTime);
    }

    // test that if timeStamp is more than max time when inc, then sms is set to
    // 1, and the new time stamp
    public void test_reset_when_max_time_expired() {
        mSharedPreference.incSmsCount(TIME_STAMP);

        mSharedPreference.incSmsCount(TIME_STAMP + MAX_TIME + 1);

        int nr = mSharedPreference.getNumberOfSms();
        assertTrue(nr == 1);
        long time = mSharedPreference.getCurrentTimeStamp();
        assertTrue(time == TIME_STAMP + MAX_TIME + 1);

    }

    // test that if max number of sms has been recorded during max time, canInc
    // will return false
    public void test_cannot_inc_max_number_reached() {
        int maxSms = 2;
        mSharedPreference = new SmsSharedPreference(mContext, MAX_TIME, maxSms);
        mSharedPreference.incSmsCount(TIME_STAMP);
        mSharedPreference.incSmsCount(TIME_STAMP);
        assertFalse(mSharedPreference.canInc(TIME_STAMP + MAX_TIME));
    }

    // test that if max number of sms has been recorded, but time stamp exceeds
    // max time, canInc will return true
    public void test_can_inc_max_time_expired() {
        int maxSms = 2;
        mSharedPreference = new SmsSharedPreference(mContext, MAX_TIME, maxSms);
        mSharedPreference.incSmsCount(TIME_STAMP);
        mSharedPreference.incSmsCount(TIME_STAMP);
        assertTrue(mSharedPreference.canInc(TIME_STAMP + MAX_TIME + 1));
    }

    // test that if not max number of sms has been recorded, canInc will return
    // true
    public void test_can_inc_not_max_nr_sms() {
        int maxSms = 2;
        mSharedPreference = new SmsSharedPreference(mContext, MAX_TIME, maxSms);
        mSharedPreference.incSmsCount(TIME_STAMP);
        assertTrue(mSharedPreference.canInc(TIME_STAMP));
    }

    // test that can inc will be false if saftey number of sms is reached
    public void test_cannot_inc_nr_sms_excedes_safe_number() {
        int maxSms = SmsSharedPreference.MAX_NUMBER_SMS;

        mSharedPreference = new SmsSharedPreference(mContext, MAX_TIME, maxSms);

        for (int i = 0; i < maxSms; i++) {
            mSharedPreference.incSmsCount(TIME_STAMP);
            if (i < maxSms - 1) {
                assertTrue("" + i, mSharedPreference.canInc(TIME_STAMP + MAX_TIME));
            } else {
                assertFalse("" + i, mSharedPreference.canInc(TIME_STAMP + MAX_TIME));
            }
        }

    }

}

package net.wandroid.answer.tests;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.test.AndroidTestCase;
import android.test.InstrumentationTestCase;
import android.test.mock.MockContentResolver;

import net.wandroid.answer.ContactPhoto;

/**
 * Created by jjungb on 9/28/14.
 */
public class ContactTests extends InstrumentationTestCase {

    private ContactPhoto mContact;
    private String mNumber="+1234567890";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mContact=new ContactPhoto();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        
    }


    // test that looking for a number with country code and it exist in contacts, the same number is returned.
    public void test_find_number_that_exists(){
        ContentValues values=mContact.findContactByNumber(mNumber,getInstrumentation().getContext().getContentResolver());
        assertTrue(values.getAsString(ContactsContract.PhoneLookup.NUMBER).equals(mNumber));
    }



}
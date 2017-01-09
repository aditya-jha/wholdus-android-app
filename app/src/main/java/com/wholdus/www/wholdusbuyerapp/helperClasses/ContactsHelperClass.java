package com.wholdus.www.wholdusbuyerapp.helperClasses;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Data;

import java.util.ArrayList;

/**
 * Created by aditya on 9/1/17.
 */

public class ContactsHelperClass {

    private Context mContext;

    private static final String[] CONTACT = new String[] {"Wholdus.com", "", "8800971126", "8791947227", "info@wholdus.com"};

    public ContactsHelperClass(Context context) {
        mContext = context;
    }

    public String getSavedNumber() {
        if (isContactSaved(CONTACT[2])) {
            return CONTACT[2];
        } else if (isContactSaved(CONTACT[3])) {
            return CONTACT[3];
        } else {
            return null;
        }
    }

    private boolean isContactSaved(String number) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        String name = "?";

        Cursor contactLookup = mContext.getContentResolver().query(uri,
                new String[] {BaseColumns._ID, ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (contactLookup != null) {
            int count = contactLookup.getCount();
            contactLookup.close();
            return count > 0;
        } else {
            return false;
        }
    }

    public void saveWholdusContacts() {
        addContact(CONTACT);
    }

    public void sendContactsToServer() {
        /* TODO: Send user contacts to server */
    }

    private boolean addContact(String[] contact) {
        ArrayList<ContentProviderOperation> operationList = new ArrayList<>();
        operationList.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        // first and last names
        operationList.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Contacts.Data.RAW_CONTACT_ID, 0)
                .withValue(Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, contact[0])
                .withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, contact[1])
                .build());

        operationList.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
                .withValueBackReference(Data.RAW_CONTACT_ID, 0)
                .withValue(Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, contact[2])
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
                .build());

        operationList.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, contact[3])
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
                .build());

        operationList.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Email.DATA, contact[4])
                .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                .build());

        try {
            ContentProviderResult[] results = mContext.getContentResolver().applyBatch(ContactsContract.AUTHORITY, operationList);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

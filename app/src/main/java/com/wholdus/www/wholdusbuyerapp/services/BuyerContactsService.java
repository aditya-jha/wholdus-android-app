package com.wholdus.www.wholdusbuyerapp.services;

import android.Manifest;
import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;

import com.wholdus.www.wholdusbuyerapp.helperClasses.APIConstants;
import com.wholdus.www.wholdusbuyerapp.helperClasses.GlobalAccessHelper;
import com.wholdus.www.wholdusbuyerapp.helperClasses.OkHttpHelper;
import com.wholdus.www.wholdusbuyerapp.helperClasses.TODO;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Response;

/**
 * Created by kaustubh on 12/2/17.
 */

public class BuyerContactsService extends IntentService {

    public static final String REQUEST_TAG = "BUYER_CONTACTS_API_REQUESTS";

    public BuyerContactsService() {
        super("BuyerContactsService");
    }

    public static final String BUYER_CONTACTS_PREFERENCES = "BuyerContactsPreference";
    public static final String SENT_CONTACTS_KEY = "SentContactsKey";

    @Override
    protected void onHandleIntent(Intent intent) {
        int todo = intent.getIntExtra("TODO", -1);
        switch (todo) {
            case TODO.SEND_BUYER_CONTACTS:
                sendBuyerContacts(todo);
                break;
        }
    }

    private void sendBuyerContacts(int todo){
        try {
            if ((ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) &&
                    (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)){
                return;
            }
            SharedPreferences preferences = getSharedPreferences(BUYER_CONTACTS_PREFERENCES, MODE_PRIVATE);
            String sentContacts = preferences.getString(SENT_CONTACTS_KEY, "0");
            ArrayList<Integer> newContacts = new ArrayList<>();
            String[] projection = {ContactsContract.CommonDataKinds.Contactables.DISPLAY_NAME_PRIMARY,
                    ContactsContract.CommonDataKinds.Contactables.CONTACT_ID,
                    ContactsContract.CommonDataKinds.Contactables.DATA1};

            Uri uri;
            String selection = ContactsContract.CommonDataKinds.Contactables.CONTACT_ID + " NOT IN (" + sentContacts + ")";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2){
                uri = ContactsContract.CommonDataKinds.Contactables.CONTENT_URI;
            } else {
                uri = ContactsContract.Data.CONTENT_URI;
                selection += " AND (" + ContactsContract.Data.HAS_PHONE_NUMBER + "!=0 OR (" + ContactsContract.Data.MIMETYPE + "=? OR "
                        + ContactsContract.Data.MIMETYPE + "=?))";
            }

            Cursor cursor = getContentResolver().query(
                    uri,
                    projection,
                    selection,
                    null,
                    ContactsContract.CommonDataKinds.Contactables.CONTACT_ID);
            if (cursor == null || cursor.getCount() == 0){
                return;
            }
            JSONArray contacts = new JSONArray();
            JSONObject contact = new JSONObject();
            contact.put("contactID", 0);
            ArrayList<String> numbers = new ArrayList<>();
            ArrayList<String> mails = new ArrayList<>();
            while (cursor.moveToNext()) {
                int contactID = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Contactables.CONTACT_ID));
                if (contact.getInt("contactID") != contactID) {
                    if (contact.getInt("contactID") != 0 && numbers.size() > 0) {
                        contact.put("numbersArr", new JSONArray(numbers));
                        contact.put("mailArr", new JSONArray(mails));
                        contacts.put(contact);
                    }
                    contact = new JSONObject();
                    numbers = new ArrayList<>();
                    mails = new ArrayList<>();
                    contact.put("contactID", contactID);
                    newContacts.add(contactID);
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Contactables.DISPLAY_NAME_PRIMARY));
                    contact.put("name", name);
                }
                String data = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Contactables.DATA1));
                data = data.replaceAll("\\s", "");
                if (data != null) {
                    if (data.contains("@")) {
                        if (!mails.contains(data)) {
                            mails.add(data);
                        }
                    } else {
                        data = data.replaceFirst("\\+91", "");
                        data = data.replaceFirst("^0", "");
                        if (!numbers.contains(data)) {
                            numbers.add(data);
                        }
                    }
                }

            }
            if (contact.getInt("contactID") != 0 && numbers.size() > 0) {
                contact.put("numbersArr", new JSONArray(numbers));
                contact.put("mailArr", new JSONArray(mails));
                contacts.put(contact);
            }
            cursor.close();

            HashMap<String, String> params = new HashMap<>();
            String url = GlobalAccessHelper.generateUrl(APIConstants.BUYER_CONTACTS_URL, params);
            JSONObject requestBody = new JSONObject();
            requestBody.put("contacts", contacts);
            Response response = OkHttpHelper.makePostRequest(getApplicationContext(), url, requestBody.toString());
            if(response.isSuccessful()) {
                sentContacts += "," + TextUtils.join(",", newContacts);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(SENT_CONTACTS_KEY, sentContacts);
                editor.apply();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

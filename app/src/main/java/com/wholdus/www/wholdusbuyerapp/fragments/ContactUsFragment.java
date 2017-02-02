package com.wholdus.www.wholdusbuyerapp.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crash.FirebaseCrash;
import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.databaseContracts.UserProfileContract;
import com.wholdus.www.wholdusbuyerapp.helperClasses.APIConstants;
import com.wholdus.www.wholdusbuyerapp.helperClasses.ContactsHelperClass;
import com.wholdus.www.wholdusbuyerapp.helperClasses.GlobalAccessHelper;
import com.wholdus.www.wholdusbuyerapp.helperClasses.OkHttpHelper;
import com.wholdus.www.wholdusbuyerapp.helperClasses.TrackingHelper;
import com.wholdus.www.wholdusbuyerapp.interfaces.HelpSupportListenerInterface;

import org.json.JSONObject;

import okhttp3.Response;

/**
 * Created by aditya on 11/1/17.
 */

public class ContactUsFragment extends Fragment implements View.OnClickListener {

    private HelpSupportListenerInterface mListener;
    private TextInputLayout mMessageWrapper;
    private TextInputEditText mMessageEditText;
    private ProgressBar mSubmitLoader;
    private ScrollView mPageLayout;

    private FirebaseAnalytics mFirebaseAnalytics;

    private static final int CONTACTS_PERMISSION = 0;

    public ContactUsFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (HelpSupportListenerInterface) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contactus, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMessageEditText = (TextInputEditText) view.findViewById(R.id.message_edit_text);
        mMessageWrapper = (TextInputLayout) view.findViewById(R.id.message_wrapper);

        Button phone1 = (Button) view.findViewById(R.id.phone1);
        phone1.setOnClickListener(this);

        Button phone2 = (Button) view.findViewById(R.id.phone2);
        phone2.setOnClickListener(this);

        Button chat = (Button) view.findViewById(R.id.chat_button);
        chat.setOnClickListener(this);

        Button submit = (Button) view.findViewById(R.id.submit_button);
        submit.setOnClickListener(this);

        mPageLayout = (ScrollView) view.findViewById(R.id.page_layout);

        mSubmitLoader = (ProgressBar) view.findViewById(R.id.submit_button_loader);
        mSubmitLoader.setVisibility(View.INVISIBLE);

        TrackingHelper.getInstance(getContext())
                .logEvent(FirebaseAnalytics.Event.VIEW_ITEM, this.getClass().getSimpleName(), "");
    }

    @Override
    public void onResume() {
        super.onResume();
        mListener.fragmentCreated(getString(R.string.contact_us_title));
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        final int ID = view.getId();
        switch (ID) {
            case R.id.phone1:
                callPhone(getString(R.string.phone1));
                break;
            case R.id.phone2:
                callPhone(getString(R.string.phone2));
                break;
            case R.id.chat_button:
                chatButtonClicked();
                break;
            case R.id.submit_button:
                submitButtonClicked(view);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CONTACTS_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    chatButtonClicked();
                } else {
                    Toast.makeText(getContext(), "Permission needed to chat with us", Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void callPhone(String number) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number));
        startActivity(intent);
    }

    private void chatButtonClicked() {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "chat");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "chat on contact us clicked");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        ContactsHelperClass contactsHelperClass = new ContactsHelperClass(getActivity().getApplicationContext());
                        String savedNumber = contactsHelperClass.getSavedNumber();
                        if (savedNumber != null) {
                            openWhatsapp(savedNumber);
                        } else {
                            contactsHelperClass.saveWholdusContacts();
                            savedNumber = contactsHelperClass.getSavedNumber();
                            if (savedNumber != null) openWhatsapp(savedNumber);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        FirebaseCrash.report(e);
                    }
                }
            }).start();
        } else {
            requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS}, CONTACTS_PERMISSION);
        }
    }

    private void openWhatsapp(final String number) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    final Uri uri = Uri.parse("smsto:" + number);
                    Intent i = new Intent(Intent.ACTION_SENDTO, uri);
                    i.putExtra("sms_body", "I need some help");
                    i.setPackage("com.whatsapp");
                    getContext().startActivity(i);
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Whatsapp not installed", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void submitButtonClicked(View view) {
        final String message = mMessageEditText.getText().toString();
        if (!message.isEmpty()) {
            mPageLayout.setVisibility(View.INVISIBLE);
            mSubmitLoader.setVisibility(View.VISIBLE);
            mListener.hideSoftKeyboard(view);
            final String mobileNumber = GlobalAccessHelper.getMobileNumber(getActivity().getApplicationContext());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String url = OkHttpHelper.generateUrl(APIConstants.CONTACT_US_URL);
                        JSONObject data = new JSONObject();
                        data.put(UserProfileContract.UserTable.COLUMN_MOBILE_NUMBER, mobileNumber);
                        data.put("remarks", message);

                        Response response = OkHttpHelper.makePostRequest(getActivity().getApplicationContext(), url, data.toString());

                        if (response.isSuccessful()) {
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mSubmitLoader.setVisibility(View.INVISIBLE);
                                        mPageLayout.setVisibility(View.VISIBLE);
                                        mMessageEditText.setText("");
                                        Toast.makeText(getContext(), getString(R.string.contact_us_sucess), Toast.LENGTH_LONG).show();
                                    }
                                });
                            } else {
                                showError();
                            }
                        } else {
                            showError();
                        }
                    } catch (Exception e) {
                        showError();
                    }
                }
            }).start();
        }
    }

    private void showError() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mSubmitLoader.setVisibility(View.INVISIBLE);
                    mPageLayout.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(), getString(R.string.api_error_message), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}

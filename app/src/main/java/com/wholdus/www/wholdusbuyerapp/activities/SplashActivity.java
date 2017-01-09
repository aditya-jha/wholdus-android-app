package com.wholdus.www.wholdusbuyerapp.activities;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.wholdus.www.wholdusbuyerapp.databaseHelpers.DatabaseHelper;
import com.wholdus.www.wholdusbuyerapp.helperClasses.LoginHelper;

public class SplashActivity extends AppCompatActivity {

    private Bundle mArgs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent()!= null) {
            mArgs = getIntent().getExtras();
        }

        proceed();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void proceed() {
        final Context context = this;

        new Thread(new Runnable() {
            @Override
            public void run() {
                // so that database is created on first open
                DatabaseHelper databaseHelper = DatabaseHelper.getInstance(getApplicationContext());
                SQLiteDatabase db = databaseHelper.openDatabase();
                databaseHelper.closeDatabase();
                LoginHelper loginHelper = new LoginHelper(context);
                startNewActivity(getRoutingIntent(
                        loginHelper.checkIfLoggedIn()
                ));
            }
        }).start();
    }

    private void startNewActivity(final Intent intent) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
                finish();
                overridePendingTransition(0, 0);
            }
        });
    }

    public Intent getRoutingIntent(boolean loggedIn){

        Class activityClass;
        Intent intent = new Intent();

        if (!loggedIn){
            activityClass = IntroActivity.class;
        }
        else {
            activityClass = HomeActivity.class;
            intent.putExtra("router", mArgs);
        }

        intent.setClass(getApplicationContext(), activityClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return intent;
    }
}

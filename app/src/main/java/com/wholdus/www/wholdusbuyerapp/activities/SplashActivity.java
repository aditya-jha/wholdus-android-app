package com.wholdus.www.wholdusbuyerapp.activities;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.wholdus.www.wholdusbuyerapp.databaseHelpers.DatabaseHelper;
import com.wholdus.www.wholdusbuyerapp.helperClasses.LoginHelper;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                // so that database is created on first open
                DatabaseHelper databaseHelper = DatabaseHelper.getInstance(getApplicationContext());
                SQLiteDatabase db = databaseHelper.openDatabase();
                databaseHelper.closeDatabase();

                LoginHelper loginHelper = new LoginHelper(getApplicationContext());
                if (loginHelper.checkIfLoggedIn()) {
                    startLoginSignupActivity(HomeActivity.class);
                } else {
                    startLoginSignupActivity(IntroActivity.class);
                }
            }
        }).start();
    }

    private void startLoginSignupActivity(final Class classToStart) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), classToStart);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                overridePendingTransition(0, 0);
            }
        });
    }
}

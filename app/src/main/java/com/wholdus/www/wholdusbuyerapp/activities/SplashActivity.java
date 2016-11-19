package com.wholdus.www.wholdusbuyerapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.wholdus.www.wholdusbuyerapp.aynctasks.LoginHelperAsyncTask;

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
        LoginHelperAsyncTask loginHelperAsyncTask = new LoginHelperAsyncTask(this, new LoginHelperAsyncTask.AsyncResponse() {
            @Override
            public void processFinish(Boolean result) {
                if (result) {
                    startLoginSignupActivity(HomeActivity.class);
                } else {
                    startLoginSignupActivity(LoginSignupActivity.class);
                }
            }
        });

        loginHelperAsyncTask.execute("checkIfLoggedIn");
    }

    private void startLoginSignupActivity(Class classToStart) {
        Intent intent = new Intent(this, classToStart);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
        overridePendingTransition(0, 0);
    }
}

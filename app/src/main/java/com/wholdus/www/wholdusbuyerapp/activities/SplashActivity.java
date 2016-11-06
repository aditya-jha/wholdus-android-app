package com.wholdus.www.wholdusbuyerapp.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.aynctasks.LoginHelperAsyncTask;

public class SplashActivity extends AppCompatActivity {

    private final String LOG_TAG = "SplashActivity";
    private final long WAITING_TIME = 600;
    private long mStartTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_splash);
        Log.v(LOG_TAG, "on create");

        mStartTime = System.currentTimeMillis();
        proceed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(LOG_TAG, "on resume");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(LOG_TAG, "on destroy");
    }

    private void proceed() {
        LoginHelperAsyncTask loginHelperAsyncTask = new LoginHelperAsyncTask(this, new LoginHelperAsyncTask.AsyncResponse() {
            @Override
            public void processFinish(Boolean result) {

                final Boolean output = result;

                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        long currentTime = System.currentTimeMillis();
                        long timeDiff = currentTime - mStartTime;

                        if(timeDiff < WAITING_TIME) {
                            try {
                                Log.v(LOG_TAG, "waiting for: " + (WAITING_TIME - timeDiff));
                                Thread.sleep(WAITING_TIME - timeDiff);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        if(output) {
                            Log.v(LOG_TAG, "user already logged in");
                        } else {
                            startLoginSignupActivity();
                        }
                    }
                };

                new Thread(runnable).start();
            }
        });

        loginHelperAsyncTask.execute("checkIfLoggedIn");
    }

    private void startLoginSignupActivity() {
        Intent intent = new Intent(this, IntroActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}

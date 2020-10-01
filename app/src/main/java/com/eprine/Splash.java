package com.eprine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.Toast;

import com.onesignal.OneSignal;

import java.util.concurrent.Executor;

public class Splash extends AppCompatActivity {

    private String loggedIn;
    private String deviceId = "";
    private String biometricEnabled;
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);

        bioMetricsAuth();

        loggedIn = SharedPreferences.getString(Splash.this, "loggedIn");
        biometricEnabled = SharedPreferences.getString(Splash.this, "biometricEnabled");
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        deviceId = OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (loggedIn.equalsIgnoreCase("loggedIn")) {
                    if (biometricEnabled.equalsIgnoreCase("")) {
                        Intent intent = new Intent(Splash.this, HomeScreen.class);
                        intent.putExtra("deviceID", deviceId);
                        startActivity(intent);
                        finish();
                    } else {
                        biometricPrompt.authenticate(promptInfo);
                    }
                } else {
                    Intent intent = new Intent(Splash.this, Login.class);
                    intent.putExtra("deviceID", deviceId);
                    startActivity(intent);
                    finish();
                }
            }
        }, 5000);

    }

    public void bioMetricsAuth() {
        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(Splash.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(),
                        "Authentication error: " + errString, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getApplicationContext(),
                        "Authentication succeeded!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Splash.this, HomeScreen.class);
                intent.putExtra("deviceID", deviceId);
                startActivity(intent);
                finish();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Authentication failed",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login for my app")
                .setSubtitle("Log in using your biometric credentials")
                .setNegativeButtonText("Use Phone Password")
                .build();
    }
}
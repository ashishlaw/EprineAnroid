package com.eprine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.eprine.customcontrols.TextViewRegular;
import com.eprine.propertyClasses.LoginProp;
import com.eprine.retrofitclasses.ApiClient;
import com.eprine.retrofitclasses.ApiInterface;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.onesignal.OneSignal;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class Login extends AppCompatActivity {

    public static final int RequestPermissionCode = 1;

    private EditText emailAddressET, passwordET;
    private Button loginBT;
    private String JWTtoken;
    String deviceId = "";
    boolean isClicked = false;
    private CheckBox biometricsCB;
    ImageView showPasswordIV;
    private RelativeLayout mainRL;
    private TextViewRegular forgotPasswordTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);


        if (checkPermission()) {

        } else {
            requestPermission();
        }

        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        deviceId = OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId();

        mainRL = findViewById(R.id.mainRL);

        emailAddressET = findViewById(R.id.emailET);
        passwordET = findViewById(R.id.passwordET);
        loginBT = findViewById(R.id.loginBT);
        showPasswordIV = findViewById(R.id.showPasswordIV);
        biometricsCB = findViewById(R.id.biometricsCB);
        forgotPasswordTV = findViewById(R.id.forgotPasswordTV);

        forgotPasswordTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, ForgotPassword.class);
                startActivity(intent);
            }
        });

        showPasswordIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isClicked) {
                    passwordET.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    isClicked = true;
                } else {
                    passwordET.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    isClicked = false;
                }
            }
        });

        loginBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailAddress = emailAddressET.getText().toString().trim();
                String password = passwordET.getText().toString().trim();

                if (emailAddress.isEmpty()) {
                    Snackbar snackbar = Snackbar
                            .make(mainRL, "Enter an Email Address", Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else if (password.isEmpty()) {
                    Snackbar snackbar = Snackbar
                            .make(mainRL, "Enter the Password", Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else {
                    Dialogs.baseShowProgressDialog(Login.this, "Logging In...");
                    loginService(emailAddress, password, "eprine", deviceId);
                }
            }
        });
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(Login.this, new String[]
                {
                        Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                }, RequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == RequestPermissionCode) {
            if (grantResults.length > 0) {
                boolean CameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean RecordAudio = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                boolean WriteExternalStoragePermission = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                boolean FineLocation = grantResults[3] == PackageManager.PERMISSION_GRANTED;
                boolean CoarseLocation = grantResults[4] == PackageManager.PERMISSION_GRANTED;

                if (CameraPermission && RecordAudio && WriteExternalStoragePermission && FineLocation && CoarseLocation) {
                    Toast.makeText(Login.this, "Permission Granted", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(Login.this, "Permission Denied", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public boolean checkPermission() {

        int FirstPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        int SecondPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        int ThirdPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int FourthPermission = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        int FifthPermission = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_COARSE_LOCATION);

        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED &&
                SecondPermissionResult == PackageManager.PERMISSION_GRANTED &&
                ThirdPermissionResult == PackageManager.PERMISSION_GRANTED &&
                FourthPermission == PackageManager.PERMISSION_GRANTED &&
                FifthPermission == PackageManager.PERMISSION_GRANTED;
    }

    public void loginService(String username, String password, String app_name, String device_id) {
        if (!Dialogs.isInternetAvailable(this)) {
            Dialogs.baseHideProgressDialog();
            Dialogs.showToast(this, "Internet Not Available!");
            return;
        }

        ApiInterface apiInterface = ApiClient.getClientWithToken(Login.this).create(ApiInterface.class);
        apiInterface.loginAPI(username, password, app_name, device_id).enqueue(new Callback<LoginProp>() {
            @Override
            public void onResponse(Call<LoginProp> call, Response<LoginProp> response) {
                Dialogs.baseHideProgressDialog();
                if (response.isSuccessful()) {
                    if (response.body().getStatus()) {
                        JWTtoken = response.body().getData().getJwt().getIdToken();
                        SharedPreferences.setString(Login.this, "loggedIn", "loggedIn");
                        SharedPreferences.setString(Login.this, "token", JWTtoken);
                        SharedPreferences.setString(Login.this, "deviceID", response.body().getDeviceId());
                        SharedPreferences.setString(Login.this, "biometricEnabled", "");
                        Intent intent = new Intent(Login.this, HomeScreen.class);
                        startActivity(intent);
                        finish();
                        if (biometricsCB.isChecked()) {
                            SharedPreferences.setString(Login.this, "biometricEnabled", "Enabled");
                            Toast.makeText(Login.this, "Biometrics Authenticated", Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    try {
                        //  Dialogs.baseHideProgressDialog();
                        Gson gsonObj = new Gson();
                        LoginProp loginError = gsonObj.fromJson(response.errorBody().string(), LoginProp.class);
                        Toast.makeText(getApplicationContext(), loginError.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginProp> call, Throwable t) {
                Dialogs.baseHideProgressDialog();
                Log.e("erroroororoororororor", t.getMessage());
                Toast.makeText(getApplicationContext(), "Unable to Login", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
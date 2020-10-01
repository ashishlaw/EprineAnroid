package com.eprine;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.eprine.customcontrols.ButtonRalewayBold;
import com.eprine.customcontrols.EditTextRalewayRegular;
import com.eprine.propertyClasses.forgotPassword.ForgotPasswordProp;
import com.eprine.retrofitclasses.ApiClient;
import com.eprine.retrofitclasses.ApiInterface;
import com.google.android.material.snackbar.Snackbar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPassword extends AppCompatActivity {

    private RelativeLayout mainRL;
    private EditTextRalewayRegular userNameET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_forgot_password);

        mainRL = findViewById(R.id.mainRL);

        ImageView backIV = findViewById(R.id.backIV);
        ButtonRalewayBold resetPasswordBT = findViewById(R.id.resetPasswordBT);
        userNameET = findViewById(R.id.userNameET);

        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        resetPasswordBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = userNameET.getText().toString().trim();
                if (userName.isEmpty()) {
                    Snackbar snackbar = Snackbar
                            .make(mainRL, "Enter the User Name/Email", Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else {
                    Dialogs.baseShowProgressDialog(ForgotPassword.this, "Loading, Please Wait...");
                    forgotPasswordAPI(userName);
                }
            }
        });
    }

    public void forgotPasswordAPI(String username) {
        if (!Dialogs.isInternetAvailable(this)) {
            Dialogs.baseHideProgressDialog();
            Dialogs.showToast(this, "Internet Not Available!");
            return;
        }
        ApiInterface apiInterface = ApiClient.getClientWithToken(ForgotPassword.this).create(ApiInterface.class);
        apiInterface.forgotPasswordAPI(username).enqueue(new Callback<ForgotPasswordProp>() {
            @Override
            public void onResponse(Call<ForgotPasswordProp> call, Response<ForgotPasswordProp> response) {
                Dialogs.baseHideProgressDialog();
                if (response.isSuccessful()) {
                    if (response.body().getStatus() == 1) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPassword.this);
                        builder.setMessage("Forgot Password Request Successful. \nPlease check your email for a link to reset your password.\n")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //do things
                                        Intent intent = new Intent(ForgotPassword.this, Login.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                    //   Toast.makeText(ForgotPassword.this, response.message(), Toast.LENGTH_LONG).show();
                    else if (response.body().getStatus() == -2) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPassword.this);
                        builder.setMessage("ERROR EMAIL")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //do things
                                        dialog.dismiss();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    } else if (response.body().getStatus() == -1) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPassword.this);
                        builder.setMessage("ERROR USERNAME")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //do things
                                        dialog.dismiss();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ForgotPasswordProp> call, Throwable t) {
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
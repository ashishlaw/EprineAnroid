package com.eprine;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.GeolocationPermissions;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.onesignal.OSSubscriptionObserver;
import com.onesignal.OSSubscriptionStateChanges;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HomeScreen extends AppCompatActivity implements OSSubscriptionObserver {

    /*-- CUSTOMIZE --*/
    /*-- you can customize these options for your convenience --*/
    //  private static String webview_url = "https://camali.adaptivetelehealth.com/index.php/login";    // web address or local file location you want to open in webview
    //private static String webview_url = "https://test.adaptivetelehealth.com/index.php/login/mobile_login/";
    //private static String webview_url = "https://eprine.adaptivetelehealth.com/index.php/login/mobile_login/";
    private static String webview_url = "https://eprine.adaptivetelehealth.com/index.php/login/mobile_login/";

    private static String file_type = "image/*";    // file types to be allowed for upload
    private boolean multiple_files = true;         // allowing multiple file upload

    /*-- MAIN VARIABLES --*/
    WebView webView;
    //ImageView titleicon;
    private static final String TAG = HomeScreen.class.getSimpleName();
    private boolean clearHistory;
    private String cam_file_data = null;        // for storing camera file information
    private ValueCallback<Uri> file_data;       // data/header received after file selection
    private ValueCallback<Uri[]> file_path;     // received file(s) temp. location 
    private String deviceId = "";
    private final static int file_req_code = 1;
    private String JWtoken;
    private String deviceID;
    private GeolocationPermissions.Callback mGeoLocationCallback;
    private String mGeoLocationOrigin;
    private static final int MY_LOCATION_REQUEST = 2;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (Build.VERSION.SDK_INT >= 21) {
            Uri[] results = null;

            /*-- if file request cancelled; exited camera. we need to send null value to make future attempts workable --*/
            if (resultCode == Activity.RESULT_CANCELED) {
                if (requestCode == file_req_code) {
                    file_path.onReceiveValue(null);
                    return;
                }
            }

            /*-- continue if response is positive --*/
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == file_req_code) {
                    if (null == file_path) {
                        return;
                    }
                    if (null == intent.getClipData() && null == intent.getDataString() && null != cam_file_data) {
                        results = new Uri[]{Uri.parse(cam_file_data)};
                    } else {
                        if (null != intent.getClipData()) { // checking if multiple files selected or not
                            final int numSelectedFiles = intent.getClipData().getItemCount();
                            results = new Uri[numSelectedFiles];
                            for (int i = 0; i < intent.getClipData().getItemCount(); i++) {
                                results[i] = intent.getClipData().getItemAt(i).getUri();
                            }
                        } else {
                            results = new Uri[]{Uri.parse(intent.getDataString())};
                        }
                    }
                }
            }
            file_path.onReceiveValue(results);
            file_path = null;
        } else {
            if (requestCode == file_req_code) {
                if (null == file_data) return;
                Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
                file_data.onReceiveValue(result);
                file_data = null;
            }
        }
    }

    @SuppressLint({"SetJavaScriptEnabled", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    /*    OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
        deviceId = OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId();*/
//        Log.d("deviceId----------", deviceId);
        setContentView(R.layout.activity_home_screen);

        webView = findViewById(R.id.mywebview);
        assert webView != null;
        WebSettings webSettings = webView.getSettings();
        webSettings.setBuiltInZoomControls(false);
        webSettings.setSupportZoom(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setSupportMultipleWindows(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setGeolocationEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setSupportZoom(true);
        webSettings.setDefaultTextEncodingName("utf-8");
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setAppCacheEnabled(true); // Disable while debugging
        webSettings.setAllowFileAccess(true);

        webView.setWebViewClient(new MyWebClient()); //CUSTOM WebViewClient
        webView.setPadding(0, 0, 0, 0);
        webView.setInitialScale(100);

        JWtoken = SharedPreferences.getString(HomeScreen.this, "token");
        deviceID = SharedPreferences.getString(HomeScreen.this, "deviceID");

        webView.loadUrl(webview_url + JWtoken + "/" + deviceID);
        System.out.println(webview_url + JWtoken + "/" + deviceID);
        webView.setWebChromeClient(new WebChromeClient() {

            /*-- openFileChooser is not a public Android API and has never been part of the SDK. --*/


            /*-- handling input[type="file"] requests for android API 16+ --*/
//ZoomUs Meeting Code

            @Override
            public void onGeolocationPermissionsShowPrompt(final String origin, final GeolocationPermissions.Callback callback) {
                if (ContextCompat.checkSelfPermission(HomeScreen.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(HomeScreen.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                        new AlertDialog.Builder(HomeScreen.this)
                                .setMessage("Permission for Access Location")
                                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mGeoLocationCallback = callback;
                                        mGeoLocationOrigin = origin;
                                        ActivityCompat.requestPermissions(HomeScreen.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, MY_LOCATION_REQUEST);
                                    }
                                }).show();

                    } else {
                        mGeoLocationCallback = callback;
                        mGeoLocationOrigin = origin;
                        ActivityCompat.requestPermissions(HomeScreen.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, MY_LOCATION_REQUEST);
                    }
                } else {
                    callback.invoke(origin, true, true);
                }
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
                new AlertDialog.Builder(HomeScreen.this)
                        .setTitle("App Title")
                        .setMessage(message)
                        .setPositiveButton(android.R.string.ok,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        result.confirm();
                                    }
                                })
                        .setNegativeButton(android.R.string.cancel,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        result.cancel();
                                    }
                                })
                        .create()
                        .show();
                return true;
            }

            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                WebView newWebView = new WebView(HomeScreen.this);
                WebSettings webSettings = newWebView.getSettings();
                webSettings.setJavaScriptEnabled(true);

                // Other configuration comes here, such as setting the WebViewClient
                final Dialog dialog = new Dialog(HomeScreen.this);
                dialog.setContentView(newWebView);
                dialog.show();

                newWebView.setWebChromeClient(new WebChromeClient() {
                    @Override
                    public void onCloseWindow(WebView window) {
                        dialog.dismiss();
                    }
                });

                ((WebView.WebViewTransport) resultMsg.obj).setWebView(newWebView);
                resultMsg.sendToTarget();
                return true;
            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                file_data = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType(file_type);
                if (multiple_files && Build.VERSION.SDK_INT >= 18) {
                    i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                }
                startActivityForResult(Intent.createChooser(i, "File Chooser"), file_req_code);
            }

            /*-- handling input[type="file"] requests for android API 21+ --*/
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                if (file_permission() && Build.VERSION.SDK_INT >= 21) {
                    file_path = filePathCallback;
                    Intent takePictureIntent = null;
                    Intent takeVideoIntent = null;
                    boolean includeVideo = false;
                    boolean includePhoto = false;

                    /*-- checking the accept parameter to determine which intent(s) to include --*/
                    paramCheck:
                    for (String acceptTypes : fileChooserParams.getAcceptTypes()) {
                        String[] splitTypes = acceptTypes.split(", ?+"); // although it's an array, it still seems to be the whole value; split it out into chunks so that we can detect multiple values
                        for (String acceptType : splitTypes) {
                            switch (acceptType) {
                                case "*/*":
                                    includePhoto = true;
                                    includeVideo = true;
                                    break paramCheck;
                                case "image/*":
                                    includePhoto = true;
                                    break;
                                case "video/*":
                                    includeVideo = true;
                                    break;
                            }
                        }
                    }

                    if (fileChooserParams.getAcceptTypes().length == 0) {   //no `accept` parameter was specified, allow both photo and video
                        includePhoto = true;
                        includeVideo = true;
                    }

                    if (includePhoto) {
                        takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (takePictureIntent.resolveActivity(HomeScreen.this.getPackageManager()) != null) {
                            File photoFile = null;
                            try {
                                photoFile = create_image();
                                takePictureIntent.putExtra("PhotoPath", cam_file_data);
                            } catch (IOException ex) {
                                Log.e(TAG, "Image file creation failed", ex);
                            }
                            if (photoFile != null) {
                                cam_file_data = "file:" + photoFile.getAbsolutePath();
                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                            } else {
                                takePictureIntent = null;
                            }
                        }
                    }

                    if (includeVideo) {
                        takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                        if (takeVideoIntent.resolveActivity(HomeScreen.this.getPackageManager()) != null) {
                            File videoFile = null;
                            try {
                                videoFile = create_video();
                            } catch (IOException ex) {
                                Log.e(TAG, "Video file creation failed", ex);
                            }
                            if (videoFile != null) {
                                cam_file_data = "file:" + videoFile.getAbsolutePath();
                                takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(videoFile));
                            } else {
                                takeVideoIntent = null;
                            }
                        }
                    }
                    Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                    contentSelectionIntent.setType(file_type);
                    if (multiple_files) {
                        contentSelectionIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    }

                    Intent[] intentArray;
                    if (takePictureIntent != null && takeVideoIntent != null) {
                        intentArray = new Intent[]{takePictureIntent, takeVideoIntent};
                    } else if (takePictureIntent != null) {
                        intentArray = new Intent[]{takePictureIntent};
                    } else if (takeVideoIntent != null) {
                        intentArray = new Intent[]{takeVideoIntent};
                    } else {
                        intentArray = new Intent[0];
                    }

                    Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                    chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                    chooserIntent.putExtra(Intent.EXTRA_TITLE, "File chooser");
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                    startActivityForResult(chooserIntent, file_req_code);
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_LOCATION_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (mGeoLocationCallback != null) {
                        mGeoLocationCallback.invoke(mGeoLocationOrigin, true, true);
                    }
                } else {
                    if (mGeoLocationCallback != null) {
                        mGeoLocationCallback.invoke(mGeoLocationOrigin, false, false);
                    }
                }
                return;
            }
        }
    }

    @Override
    public void onOSSubscriptionChanged(OSSubscriptionStateChanges stateChanges) {
        if (!stateChanges.getFrom().getSubscribed() &&
                stateChanges.getTo().getSubscribed()) {
//            new AlertDialog.Builder(this)
//                    .setMessage("You've successfully subscribed to push notifications!")
//                    .show();
            // get player ID
            stateChanges.getTo().getUserId();
            //  deviceId = stateChanges.getTo().getUserId();
            webView.loadUrl(webview_url + JWtoken + "/" + deviceID);
            clearHistory = true;
        }
        Log.i("Debug", "onOSPermissionChanged: " + stateChanges);
    }
    /*-- callback reporting if error occurs --*/

    public class MyWebClient extends WebViewClient {
        private Intent intent;
        static final int ZOOM_REQUEST = 9666;

        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
            // callback.invoke(String origin, boolean allow, boolean remember);
            callback.invoke(origin, true, false);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            Uri uri = request.getUrl();
            if (uri.toString().contains("https://eprine.adaptivetelehealth.com/index.php/login")) {
                String package_name = "com.eprine";
                try {
                    PackageManager pm = view.getContext().getPackageManager();
                    pm.getPackageInfo(package_name, 0);
                } catch (PackageManager.NameNotFoundException e) {
                    String url = uri.toString();
                    url = url.replace("eprine", "https");
                    uri = Uri.parse(url);
                }
                intent = new Intent(view.getContext(), Login.class);
                startActivity(intent);
                SharedPreferences.clear(HomeScreen.this);
                finish();
            }

            if (uri.toString().contains("zoomus")) {
                String package_name = "us.zoom.videomeetings";
                try {
                    PackageManager pm = view.getContext().getPackageManager();
                    pm.getPackageInfo(package_name, 0);
                } catch (PackageManager.NameNotFoundException e) {
                    String url = uri.toString();
                    url = url.replace("zoomus", "https");
                    uri = Uri.parse(url);
                }
                Uri webpage = Uri.parse(uri.toString());
                intent = new Intent(Intent.ACTION_VIEW, webpage);
                view.getContext().startActivity(intent);
                return false;
            }
            view.loadUrl(request.getUrl().toString());
            return false;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.contains("https://eprine.adaptivetelehealth.com/index.php/login")) {
                String package_name = "com.eprine";
                try {
                    PackageManager pm = view.getContext().getPackageManager();
                    pm.getPackageInfo(package_name, 0);
                } catch (PackageManager.NameNotFoundException e) {
                    url = url.replace("eprine", "https");
                }

                intent = new Intent(view.getContext(), Login.class);
                startActivity(intent);
                SharedPreferences.clear(HomeScreen.this);
                finish();
                //  return false;
            }

            if (url.contains("zoomus")) {
                String package_name = "us.zoom.videomeetings";
                try {
                    PackageManager pm = view.getContext().getPackageManager();
                    pm.getPackageInfo(package_name, 0);
                } catch (PackageManager.NameNotFoundException e) {
                    url = url.replace("zoomus", "https");
                }
                Uri webpage = Uri.parse(url);
                intent = new Intent(Intent.ACTION_VIEW, webpage);
                view.getContext().startActivity(intent);
                return false;
            }

            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
        }  /////////////////////////////////////////////////////////////////////////////////////////////////////////////DELETE THIS FOR LIVE


        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Toast.makeText(getApplicationContext(), "Failed loading app!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (clearHistory) {
                clearHistory = false;
                view.clearHistory();
            }
            super.onPageFinished(view, url);
        }
    }

    /*-- checking and asking for required file permissions --*/
    public boolean file_permission() {
        if (Build.VERSION.SDK_INT >= 23 && (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) || ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(HomeScreen.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return false;
        } else {
            return true;
        }
    }

    /*-- creating new image file here --*/
    private File create_image() throws IOException {
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "img_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    /*-- creating new video file here --*/
    private File create_video() throws IOException {
        @SuppressLint("SimpleDateFormat")
        String file_name = new SimpleDateFormat("yyyy_mm_ss").format(new Date());
        String new_name = "file_" + file_name + "_";
        File sd_directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(new_name, ".3gp", sd_directory);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            WebBackForwardList l = webView.copyBackForwardList();
            String lastUrl = l.getItemAtIndex(0).getUrl();
            if (!lastUrl.contains("splash.html"))
                webView.goBack();
        } else {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Closing Activity")
                    .setMessage("Are you sure you want to close this application?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            clearAppData();
                            finish();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
    }

    private void clearAppData() {
        try {
            // clearing app data
            if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT) {
                ((ActivityManager) getSystemService(ACTIVITY_SERVICE)).clearApplicationUserData(); // note: it has a return value!
            } else {
                String packageName = getApplicationContext().getPackageName();
                Runtime runtime = Runtime.getRuntime();
                runtime.exec("pm clear " + packageName);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


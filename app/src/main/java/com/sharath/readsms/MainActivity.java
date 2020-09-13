package com.sharath.readsms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

        private OTPBroadcastReceiver otpBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        registerOtpReceiver();
    }

    public void onClick(View view) {
        try{
            String mMessage= "alert: you've spent rs.5000.00  on credit card xx4069 at khazana jewellery pvt on 2020-08-31:19:06:07.avl bal - rs.142865.00, curr o/s - rs.7135.00.not you? call 18002586161.";
            //Pattern regEx = Pattern.compile("(?i)(?:\\sInfo.\\s*)([A-Za-z0-9*]*\\s?-?\\s?[A-Za-z0-9*]*\\s?-?\\.?)");
            //Pattern regEx = Pattern.compile("(?:inr|rs)+[\\\\s]*[0-9+[\\\\,]*+[0-9]*]+[\\\\.]*[0-9]+");
            Pattern regEx = Pattern.compile("(?=.*[Aa]ccount.*|.*[Aa]/[Cc].*|.*[Aa][Cc][Cc][Tt].*|.*[Cc][Aa][Rr][Dd].*)(?=.*[Cc]redit.*|.*[Dd]ebit.*)(?=.*[Ii][Nn][Rr].*|.*[Rr][Ss].*)");
            // Find instance of pattern matches
            Matcher m = regEx.matcher(mMessage);
            if(m.find()){
                Pattern ammount = Pattern.compile("[rR][sS]\\.?\\s[,\\d]+\\.?\\d{0,2}|[iI][nN][rR]\\.?\\s*[,\\d]+\\.?\\d{0,2}");
                Matcher amtMatcher = ammount.matcher(mMessage);
                if (amtMatcher.find()) {
                    String amtStr = amtMatcher.group(0);
                }

                Pattern actPtr = Pattern.compile("[0-9]*[Xx\\*]*[0-9]*[Xx\\*]+[0-9]{3,}");
                Matcher actMatcher = ammount.matcher(mMessage);
                if (actMatcher.find()) {
                    String actStr = actMatcher.group(0);
                }

                Pattern marPtr = Pattern.compile("(?i)(?:\\sat\\s|in\\*)([A-Za-z0-9]*\\s?-?\\s?[A-Za-z0-9]*\\s?-?\\.?)");
                Matcher marMatcher = ammount.matcher(mMessage);
                if (marMatcher.find()) {
                    String marStr = marMatcher.group(0);
                }

                String mMerchantName = m.group();
                mMerchantName = mMerchantName.replaceAll("^\\s+|\\s+$", "");//trim from start and end
                mMerchantName = mMerchantName.replace("Info.","");
            }else{
                Log.i("sms", "sms");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void registerOtpReceiver() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestReadSmsPermission();
        }
        IntentFilter intentFilter;
        if (otpBroadcastReceiver == null) {
            otpBroadcastReceiver = new OTPBroadcastReceiver();
        }
        intentFilter = otpBroadcastReceiver.getIntentFilter();
        try {
            registerReceiver(otpBroadcastReceiver, intentFilter);
        } catch (Exception e) {

        }
    }

    private void requestReadSmsPermission() {
        int hasSmsReadPermission = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            hasSmsReadPermission = checkSelfPermission(Manifest.permission.RECEIVE_SMS);
            if (hasSmsReadPermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.RECEIVE_SMS},
                        0);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case 0:
                // Check if the only required permission has been granted
                if (grantResults.length > 0 && permissions.length > 0
                        && permissions[0].equals(Manifest.permission.RECEIVE_SMS)) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        registerOtpReceiver();
                        return;
                    }
                }
                showSmsPermissionRationale();
                break;
        }
    }

    private void showSmsPermissionRationale() {
        /*View contentView = findViewById(android.R.id.content);
        if (contentView == null) {
            contentView = getWindow().getDecorView();
        }
        Snackbar snackbar = Snackbar.make(contentView,
                R.string.sms_permission_rationale, Snackbar.LENGTH_LONG);
        //hack to show multiline snackbar text
        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        if (textView != null) {
            textView.setMaxLines(5);
        }
        snackbar.setAction(R.string.action_settings, v -> startPermissionsSettingsActivity(ValidateMobileActivity.this));
        snackbar.setActionTextColor(ContextCompat.getColor(ValidateMobileActivity.this,
                R.color.colorPrimaryDark));
        snackbar.show();*/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (otpBroadcastReceiver != null) {
            try {
                unregisterReceiver(otpBroadcastReceiver);
            } catch (Exception e) {

            }
        }
    }
}
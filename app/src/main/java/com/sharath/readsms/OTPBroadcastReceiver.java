package com.sharath.readsms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class OTPBroadcastReceiver extends BroadcastReceiver {

    public IntentFilter getIntentFilter() {
        IntentFilter smsOTPintentFilter = new IntentFilter();
        smsOTPintentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        smsOTPintentFilter.setPriority(2147483647);//setting high priority for dual sim support
        return smsOTPintentFilter;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //OnOtpReceivedCallback onOtpReceivedCallback = (OnOtpReceivedCallback) context;
        final Bundle bundle = intent.getExtras();
        if (bundle != null && context != null) {
            final Object[] pdusObj = (Object[]) bundle.get("pdus");
            if (pdusObj == null) return;
            for (Object aPduObj : pdusObj) {
                SmsMessage currentMessage;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    String msgFormat = bundle.getString("format");
                    currentMessage = SmsMessage.createFromPdu((byte[]) aPduObj, msgFormat);
                } else {
                    currentMessage = SmsMessage.createFromPdu((byte[]) aPduObj);
                }
                String phoneNumber;
                if (currentMessage.getDisplayOriginatingAddress() != null) {
                    phoneNumber = currentMessage.getDisplayOriginatingAddress().toUpperCase();
                } else {
                    phoneNumber = "";
                }

                String message;
                if (currentMessage.getDisplayMessageBody() != null) {
                    message = currentMessage.getDisplayMessageBody().toLowerCase();
                } else {
                    message = "";
                }

//                if (phoneNumber.contains("ID-FVMEGR")
//                        && (message.contains("your"))
//                        || message.contains("BizzGear")) {
                    //final Pattern p = Pattern.compile("(|^)\\d{6}");
                    final Pattern p = Pattern.compile("(?:inr|rs)+[\\\\s]*[0-9+[\\\\,]*+[0-9]*]+[\\\\.]*[0-9]+");
                    final Matcher m = p.matcher(message);
                    if (m.find()) {
                        String otp = m.group(0).trim();
                        if (otp.endsWith(".")) {
                            otp = otp.substring(0, otp.length() - 1);
                        }
//                        if (onOtpReceivedCallback != null) {
//                            onOtpReceivedCallback.onOtpReceived(otp);
//                        }
                    }
                //}
            }
        }
    }
}

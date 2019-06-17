package com.example.collegeassistant;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.telephony.SmsManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

public class proxy_sender extends BroadcastReceiver {

    private static final int SEND_SMS_PERMISSION_REQ = 1;
    private Context contex;

    @Override
    public void onReceive(Context context, Intent intent) {
        contex = context;
        if(checkPermission(Manifest.permission.SEND_SMS))
        {
            Toast.makeText(context,"Message sent", Toast.LENGTH_SHORT).show();
            SmsManager smsManager= SmsManager.getDefault();
            smsManager.sendTextMessage(MainActivity.friend_num,null,MainActivity.remarks,null,null);
        }
        else {
            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkPermission(String sendSms) {

        int checkpermission= ContextCompat.checkSelfPermission(contex,sendSms);
        return checkpermission== PackageManager.PERMISSION_GRANTED;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case SEND_SMS_PERMISSION_REQ:
                if(grantResults.length>0 &&(grantResults[0]== PackageManager.PERMISSION_GRANTED)){

                }
                break;
        }
    }

}
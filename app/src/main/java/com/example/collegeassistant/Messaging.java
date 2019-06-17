package com.example.collegeassistant;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class Messaging extends AppCompatActivity {

    EditText e1, e2;
    Button b1;

    public void back(View view){
        Intent intent = new Intent(Messaging.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        e1= (EditText) findViewById(R.id.editText);
        e2= (EditText) findViewById(R.id.editText2);
        b1= (Button) findViewById(R.id.button);

        if(!checkPermission(Manifest.permission.SEND_SMS)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSION_REQ);
        }

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s1=e1.getText().toString();
                String s2=e2.getText().toString();
                if(checkPermission(Manifest.permission.SEND_SMS))
                {
                    Toast.makeText(Messaging.this,"Message sent", Toast.LENGTH_SHORT).show();
                    SmsManager smsManager= SmsManager.getDefault();
                    smsManager.sendTextMessage(s1,null,s2,null,null);
                }
                else {
                    Toast.makeText(Messaging.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private static final int SEND_SMS_PERMISSION_REQ = 1;

    private boolean checkPermission(String sendSms) {

        int checkpermission= ContextCompat.checkSelfPermission(this,sendSms);
        return checkpermission== PackageManager.PERMISSION_GRANTED;
    }

    @Override
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

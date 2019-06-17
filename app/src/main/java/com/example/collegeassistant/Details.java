package com.example.collegeassistant;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class Details extends AppCompatActivity {

    TextView name,mobile,proxy,roll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        name =(TextView) findViewById(R.id.name);
        mobile =(TextView) findViewById(R.id.mobile);
        proxy =(TextView) findViewById(R.id.proxy);
        roll =(TextView) findViewById(R.id.roll);

        name.setText(MainActivity.Name);
        mobile.setText(MainActivity.my_num);
        proxy.setText(MainActivity.friend_num);
        roll.setText(MainActivity.roll);
    }
}

package com.example.collegeassistant;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class Details extends AppCompatActivity {

    TextView name,mobile,proxy,roll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        name =(TextView) findViewById(R.id.name);
        mobile =(TextView) findViewById(R.id.mobile);
        proxy =(TextView) findViewById(R.id.proxy);
        roll =(TextView) findViewById(R.id.roll);

        name.setText(MainActivity.Name);
        mobile.setText(MainActivity.my_num);
        proxy.setText(MainActivity.friend_num);
        roll.setText(MainActivity.roll);
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }



}

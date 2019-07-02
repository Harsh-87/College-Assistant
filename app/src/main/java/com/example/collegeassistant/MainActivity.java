package com.example.collegeassistant;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import com.google.android.material.navigation.NavigationView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements customButtonListener,NavigationView.OnNavigationItemSelectedListener {


    ListView list;
    myList adapter;
    ArrayList<String> maintitle = new ArrayList<>();
    static String remarks;
    String message="Proxy laga dena please mera \n";
    static String Name,roll,friend_num,my_num;
    private static final int SEND_SMS_PERMISSION_REQ = 1;
    boolean alarm_status ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        final SharedPreferences sp = this.getSharedPreferences("com.example.collegeassistant",MODE_PRIVATE);
        try {
            Name =(String) sp.getString("name",null);
            roll =(String) sp.getString("roll",null);
            my_num =(String) sp.getString("my_num",null);
            friend_num =(String) sp.getString("mobile_number",null);
            remarks = (String) sp.getString("remarks",remarks);
            alarm_status = (boolean) sp.getBoolean("check",false);
            maintitle = (ArrayList<String>) ObjectSerializer.deserialize(sp.getString("notes", ObjectSerializer.serialize(new ArrayList<String>())));
            myList.attended_classes = (int[]) ObjectSerializer.deserialize(sp.getString("attended", ObjectSerializer.serialize(new ArrayList<String>())));
            myList.total_classes = (int[]) ObjectSerializer.deserialize(sp.getString("total", ObjectSerializer.serialize(new ArrayList<String>())));
            myList.percentage = (double[]) ObjectSerializer.deserialize(sp.getString("percentage", ObjectSerializer.serialize(new ArrayList<String>())));
            myList.attendedid = (int[]) ObjectSerializer.deserialize(sp.getString("attendanceid", ObjectSerializer.serialize(new ArrayList<String>())));
            myList.totalid = (int[]) ObjectSerializer.deserialize(sp.getString("totalid", ObjectSerializer.serialize(new ArrayList<String>())));
            myList.percentid = (int[]) ObjectSerializer.deserialize(sp.getString("percentageid", ObjectSerializer.serialize(new ArrayList<String>())));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(Name == null || roll == null || my_num == null || friend_num == null){
            change_details();
        }

        if(alarm_status == true){
            Menu menu = navigationView.getMenu();
            MenuItem item = (MenuItem) menu.findItem(R.id.nav_alarm);
            item.setTitle("Disable Alarm");
        }
        adapter = new myList(this, maintitle);
        list = (ListView) findViewById(R.id.list);
        adapter.setCustomButtonListner(MainActivity.this);
        list.setAdapter(adapter);
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int position = i;
                 final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Delete")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setMessage("Are you sure you want to delete this note.")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int k) {
                                if (maintitle.size() > 1) {
                                    for(int i = position+1; i < maintitle.size(); i++) {
                                        myList.attended_classes[i-1] = myList.attended_classes[i];
                                        myList.total_classes[i-1] = myList.total_classes[i];
                                        myList.percentage[i-1] = myList.percentage[i];
                                        myList.attendedid[i-1] = myList.attendedid[i];
                                        myList.totalid[i-1] = myList.totalid[i];
                                        myList.percentid[i-1] = myList.percentid[i];

                                        myList.attended_classes[i] = 0;
                                        myList.total_classes[i] = 0;
                                        myList.percentage[i] = 0;
                                        myList.attendedid[i] =0;
                                        myList.totalid[i] = 0;
                                        myList.percentid[i] = 0;
                                    }
                                }
                                else{
                                    myList.attended_classes[0] = 0;
                                    myList.total_classes[0] = 0;
                                    myList.percentage[0] = 0;
                                    myList.attendedid[0] =0;
                                    myList.totalid[0] = 0;
                                    myList.percentid[0] = 0;
                                }

                                maintitle.remove(position);
                                adapter.notifyDataSetChanged();
                                try {
                                    sp.edit().putString("notes",ObjectSerializer.serialize(maintitle)).apply();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                Toast.makeText(MainActivity.this, "Subject deleted", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(MainActivity.this, "Subject not deleted", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show();

                return true;
            }
        });


        if(!checkPermission(Manifest.permission.SEND_SMS)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSION_REQ);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_layout,menu);
        return super.onCreateOptionsMenu(menu);
    }

    //Change mobile number and other details
    public void change_details(){

        final SharedPreferences sp = this.getSharedPreferences("com.example.collegeassistant",MODE_PRIVATE);

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.fill_details, null);
        final EditText name = (EditText) dialogView.findViewById(R.id.host);
        name.setText(Name);
        final EditText mobile_num = (EditText) dialogView.findViewById(R.id.my);
        mobile_num.setText(my_num);
        final EditText roll_num = (EditText) dialogView.findViewById(R.id.roll);
        roll_num.setText(roll);
        final EditText friends_num = (EditText) dialogView.findViewById(R.id.friend);
        friends_num.setText(friend_num);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Change Details")
                .setMessage("Details")
                .setView(dialogView)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Save",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        Name = name.getText().toString();
                        my_num = mobile_num.getText().toString();
                        roll = roll_num.getText().toString();
                        friend_num = friends_num.getText().toString();
                        remarks = message+"roll no."+roll;
                        adapter.notifyDataSetChanged();
                        sp.edit().putString("remarks",remarks).apply();
                        sp.edit().putString("mobile_number",friend_num).apply();
                        sp.edit().putString("my_num",my_num).apply();
                        sp.edit().putString("roll",roll).apply();
                        sp.edit().putString("name",Name).apply();
                        Toast.makeText(MainActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    //Add items to custom ListView
    public void add_item(){
        final SharedPreferences sp = this.getSharedPreferences("com.example.collegeassistant",MODE_PRIVATE);
        final EditText input = new EditText(MainActivity.this);
        input.setHint("Subject Name");
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        input.setPadding(20,20,20,20);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("New Subject")
                .setMessage("Name of the subject.")
                .setView(input)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("YES",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        maintitle.add( input.getText().toString() );
                        adapter.notifyDataSetChanged();
                        try {
                            sp.edit().putString("notes",ObjectSerializer.serialize(maintitle)).apply();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item) {
        if (item.getItemId() == R.id.add_item){
            add_item();
        }

        if(item.getItemId() == R.id.number){
            change_details();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    private boolean checkPermission(String sendSms) {

        int checkpermission= ContextCompat.checkSelfPermission(this,sendSms);
        return checkpermission== PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case SEND_SMS_PERMISSION_REQ:
                if(grantResults.length>0 &&(grantResults[0]==PackageManager.PERMISSION_GRANTED)){

                }
                break;
        }
    }

    //on addition button clicked
    @Override
    public void addition(int position, String value) {
        myList.attended_classes[position]++;
        myList.total_classes[position]++;
        myList.percentage[position] = (double) (Double.valueOf(myList.attended_classes[position])/Double.valueOf(myList.total_classes[position]) )*100;
        myList.percentage[position] = Math.round(myList.percentage[position] * 100.0) / 100.0;

        final SharedPreferences sp = this.getSharedPreferences("com.example.collegeassistant",MODE_PRIVATE);
        try {
            sp.edit().putString("attended", ObjectSerializer.serialize(myList.attended_classes)).apply();
            sp.edit().putString("total", ObjectSerializer.serialize(myList.total_classes)).apply();
            sp.edit().putString("percentage", ObjectSerializer.serialize(myList.percentage)).apply();
            sp.edit().putString("attendanceid", ObjectSerializer.serialize(myList.attendedid)).apply();
            sp.edit().putString("totalid", ObjectSerializer.serialize(myList.totalid)).apply();
            sp.edit().putString("percentageid", ObjectSerializer.serialize(myList.percentid)).apply();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //on subtraction button clicked
    @Override
    public void subtraction(int position, String value) {
        myList.total_classes[position]++;
        myList.percentage[position] = (double) (Double.valueOf(myList.attended_classes[position])/Double.valueOf(myList.total_classes[position]) )*100;
        myList.percentage[position] = Math.round(myList.percentage[position] * 100.0) / 100.0;

        final SharedPreferences sp = this.getSharedPreferences("com.example.collegeassistant",MODE_PRIVATE);
        try {
            sp.edit().putString("attended", ObjectSerializer.serialize(myList.attended_classes)).apply();
            sp.edit().putString("total", ObjectSerializer.serialize(myList.total_classes)).apply();
            sp.edit().putString("percentage", ObjectSerializer.serialize(myList.percentage)).apply();
            sp.edit().putString("attendanceid", ObjectSerializer.serialize(myList.attendedid)).apply();
            sp.edit().putString("totalid", ObjectSerializer.serialize(myList.totalid)).apply();
            sp.edit().putString("percentageid", ObjectSerializer.serialize(myList.percentid)).apply();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(id == R.id.nav_notification){
            notification();
        }

        if(id == R.id.nav_profile){
            profile();
        }

        if(item.getItemId() == R.id.nav_send){
            Intent in = new Intent(MainActivity.this,Messaging.class);
            startActivity(in);
        }

        if(item.getItemId() == R.id.nav_alarm){
            if(alarm_status == true) {
                alarm_settings(true, item);
            } else{
                alarm_settings(false, item);
            }
        }

        if(item.getItemId() == R.id.nav_note){
            Intent in = new Intent(MainActivity.this,Notes.class);
            startActivity(in);
        }

        if(item.getItemId() == R.id.recycler){
            Intent in = new Intent(MainActivity.this,Recycler_Activity.class);
            startActivity(in);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Alarm enabling and disabling
    public void alarm_settings(boolean isActive , MenuItem item){

        final AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent alarmintent = new Intent(MainActivity.this, AlarmReceiver.class);
        final PendingIntent alarmIntent = PendingIntent.getBroadcast(MainActivity.this, 0, alarmintent, 0);
        final SharedPreferences sp = this.getSharedPreferences("com.example.collegeassistant",MODE_PRIVATE);
        if(isActive == true) {
            alarm_status = false ;
            sp.edit().putBoolean("check",alarm_status).apply();
            alarmMgr.cancel(alarmIntent);
            final String[] set ={"Enable Alarm"};
            item.setTitle(set[0]);
            Toast.makeText(MainActivity.this, "Alarm off", Toast.LENGTH_SHORT).show();

        }else{
            final MenuItem name = item;
            final String[] set = {"Disable Alarm"};
            Context context = MainActivity.this;
            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);
            final EditText hours = new EditText(context);
            hours.setInputType(InputType.TYPE_CLASS_NUMBER);
            hours.setHint("Hours (24 hour format)");
            layout.addView(hours);
            final EditText minutes = new EditText(context);
            minutes.setHint("Minutes");
            minutes.setInputType(InputType.TYPE_CLASS_NUMBER);
            layout.addView(minutes);
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
            alertDialog.setTitle("Set time")
                    .setMessage("In 24 hours format !")
                    .setView(layout)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            int hour = -1, minute = -1;
                            try {
                                hour = (int) Integer.parseInt(hours.getText().toString());
                                minute = (int) Integer.parseInt(minutes.getText().toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (hour != -1 && minute != -1) {
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTimeInMillis(System.currentTimeMillis());
                                calendar.set(Calendar.HOUR_OF_DAY, hour);
                                calendar.set(Calendar.MINUTE, minute);
//                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
//                                    alarmMgr.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),alarmIntent);
//                                else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
//                                    alarmMgr.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
//                                else
//                                    alarmMgr.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),alarmIntent);

                                alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis()+5,
                                        1000 * 60 * 5, alarmIntent);
                                Toast.makeText(MainActivity.this, "Alarm set for " + hours.getText().toString() + ":" + minutes.getText().toString(), Toast.LENGTH_SHORT).show();
                                alarm_status = true;
                                sp.edit().putBoolean("check",alarm_status).apply();
                            } else {
                                alarm_status = false ;
                                sp.edit().putBoolean("check",alarm_status).apply();
                                Toast.makeText(MainActivity.this, "Alarm not set", Toast.LENGTH_SHORT).show();
                            }

                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            set[0] = "Enable Alarm";
                            name.setTitle(set[0]);
                            alarm_status = false ;
                            sp.edit().putBoolean("check",alarm_status).apply();
                            Toast.makeText(MainActivity.this, "Alarm not set", Toast.LENGTH_SHORT).show();
                            dialog.cancel();
                        }
                    })
                    .show();

            item.setTitle(set[0]);
        }



    }


    //Sending notification to user
    public void notification() {
        String CHANNEL_ID = "my_channel_01";
        CharSequence name = "my_channel";
        String Description = "This is my channel";

        int NOTIFICATION_ID = 234;

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(Description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mChannel.setShowBadge(true);

            if (notificationManager != null) {

                notificationManager.createNotificationChannel(mChannel);
            }

        }


        Intent resultIntent = new Intent(MainActivity.this, proxy_sender.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(MainActivity.this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent attended = PendingIntent.getActivity(getApplicationContext(),0, new Intent(),PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent message = PendingIntent.getBroadcast(getApplicationContext(),0,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Attendance reminder")
                .setContentText("You have a class !!")
                .setStyle(new NotificationCompat.BigTextStyle().bigText("You have a class !! So you have 2 options\n1.Attend class\n2.Send message of proxy"))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(attended)
                .setAutoCancel(true)
                .setColor(getResources().getColor(android.R.color.holo_red_dark))
                .addAction(R.drawable.ic_launcher_foreground, "Attending", attended)
                .addAction(R.drawable.ic_launcher_foreground, "Proxy", message);


        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }

    }

    //going to saved details activity
    public void profile(){
        Intent in = new Intent(MainActivity.this,Details.class);
        startActivity(in);
    }

}

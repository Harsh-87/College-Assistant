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
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    static String remarks;
    String message = "Proxy laga dena please mera \n";
    static String Name, roll, friend_num, my_num;
    private static final int SEND_SMS_PERMISSION_REQ = 1;
    boolean alarm_status;

    ArrayList<RecyclerView_Items> exampleList;
    ImageButton add_sub;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView_Adapter mAdapter;
    public static final String data_table_name1 = "SUBJECTS";
    public static final String data_subject = "subject_name";
    public static final String data_attend = "attended";
    public static final String data_total = "total";
    public static final String data_percent = "percent";
    SQLiteDatabase mydata;

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

        final SharedPreferences sp = this.getSharedPreferences("com.example.collegeassistant", MODE_PRIVATE);
        try {
            Name = (String) sp.getString("name", null);
            roll = (String) sp.getString("roll", null);
            my_num = (String) sp.getString("my_num", null);
            friend_num = (String) sp.getString("mobile_number", null);
            remarks = (String) sp.getString("remarks", remarks);
            alarm_status = (boolean) sp.getBoolean("check", false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (Name == null || roll == null || my_num == null || friend_num == null) {
            change_details();
        }

        if (alarm_status == true) {
            Menu menu = navigationView.getMenu();
            MenuItem item = (MenuItem) menu.findItem(R.id.nav_alarm);
            item.setTitle("Disable Alarm");
        }

        add_sub = (ImageButton) findViewById(R.id.add_subject);
        add_sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertitem();
            }
        });

        buildRecyclerView();
        database_action();
        sms_permission();

    }


    public void buildRecyclerView() {
        exampleList = new ArrayList<>();
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new RecyclerView_Adapter(exampleList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new RecyclerView_Adapter.OnItemClickListener() {
            @Override
            public void onItemLongClick(int position) {
                long_click_on_item(position);
            }

            @Override
            public void onPresentClick(int position) {
                update_attendance_p(position);
            }

            @Override
            public void onAbsentClick(int position) {
                update_attendance_a(position);
            }

        });

    }

    public void long_click_on_item(final int position) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Delete")
                .setMessage("Do you want to remove this subject ?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        removeitem(position);
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MainActivity.this, "Subject not deleted", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    private void database_action() {
        try {
            mydata = MainActivity.this.openOrCreateDatabase("db", MODE_PRIVATE, null);
            mydata.execSQL("CREATE TABLE IF NOT EXISTS " + data_table_name1 + " ( " +
                    data_subject + " varchar(40), " +
                    data_attend + " int, " +
                    data_total + " int, " +
                    data_percent + " double "
                    + " ); ");
            Cursor c = mydata.rawQuery("SELECT * FROM " + data_table_name1, null);
            c.moveToFirst();
            while (c != null) {
                String ex_sub = c.getString(c.getColumnIndex(data_subject));
                int ex_attend = c.getInt(c.getColumnIndex(data_attend));
                int ex_total = c.getInt(c.getColumnIndex(data_total));
                Double ex_percent = c.getDouble(c.getColumnIndex(data_percent));
                exampleList.add(new RecyclerView_Items(ex_sub, ex_percent, ex_attend, ex_total));
                c.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertitem() {
        final EditText input = new EditText(MainActivity.this);
        input.setHint("Subject Name");
        input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        input.setPadding(20, 20, 20, 20);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("New Subject")
                .setMessage("Name of the subject.")
                .setView(input)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        exampleList.add(new RecyclerView_Items(input.getText().toString(), 0.0, 0, 0));
                        mydata.execSQL("INSERT INTO " + data_table_name1 + " VALUES ('" +
                                input.getText().toString() + "', 0.0, 0, 0"
                                + " );");
                        mAdapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MainActivity.this, "Subject not added", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    public void removeitem(int position) {
        mydata.execSQL("DELETE FROM " + data_table_name1 + " WHERE subject_name = '" + exampleList.get(position).getSubject() + "' ;");
        exampleList.remove(position);
        mAdapter.notifyDataSetChanged();
    }

    public void update_attendance_p(int position) {
        exampleList.get(position).change_attended();
        exampleList.get(position).change_total();
        exampleList.get(position).change_percentage();
        mydata.execSQL("UPDATE " + data_table_name1 + " SET " +
                data_attend + "=" + exampleList.get(position).getAttended() + "," +
                data_total + "=" + exampleList.get(position).getTotal() + "," +
                data_percent + "=" + exampleList.get(position).getPercent() +
                " WHERE subject_name = '" + exampleList.get(position).getSubject() + "' ;");
        mAdapter.notifyDataSetChanged();
    }

    public void update_attendance_a(int position) {
        exampleList.get(position).change_total();
        exampleList.get(position).change_percentage();
        mydata.execSQL("UPDATE " + data_table_name1 + " SET " +
                data_attend + "=" + exampleList.get(position).getAttended() + "," +
                data_total + "=" + exampleList.get(position).getTotal() + "," +
                data_percent + "=" + exampleList.get(position).getPercent() +
                " WHERE subject_name = '" + exampleList.get(position).getSubject() + "' ;");
        mAdapter.notifyDataSetChanged();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_layout, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //Change mobile number and other details
    public void change_details() {

        final SharedPreferences sp = this.getSharedPreferences("com.example.collegeassistant", MODE_PRIVATE);

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
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        Name = name.getText().toString();
                        my_num = mobile_num.getText().toString();
                        roll = roll_num.getText().toString();
                        friend_num = friends_num.getText().toString();
                        remarks = message + "roll no." + roll;
                        sp.edit().putString("remarks", remarks).apply();
                        sp.edit().putString("mobile_number", friend_num).apply();
                        sp.edit().putString("my_num", my_num).apply();
                        sp.edit().putString("roll", roll).apply();
                        sp.edit().putString("name", Name).apply();
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

    //Alarm enabling and disabling
    public void alarm_settings(boolean isActive, MenuItem item) {

        final AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent alarmintent = new Intent(MainActivity.this, AlarmReceiver.class);
        final PendingIntent alarmIntent = PendingIntent.getBroadcast(MainActivity.this, 0, alarmintent, 0);
        final SharedPreferences sp = this.getSharedPreferences("com.example.collegeassistant", MODE_PRIVATE);
        if (isActive == true) {
            alarm_status = false;
            sp.edit().putBoolean("check", alarm_status).apply();
            alarmMgr.cancel(alarmIntent);
            final String[] set = {"Enable Alarm"};
            item.setTitle(set[0]);
            Toast.makeText(MainActivity.this, "Alarm off", Toast.LENGTH_SHORT).show();

        } else {
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

                                alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + 5,
                                        1000 * 60 * 5, alarmIntent);
                                Toast.makeText(MainActivity.this, "Alarm set for " + hours.getText().toString() + ":" + minutes.getText().toString(), Toast.LENGTH_SHORT).show();
                                alarm_status = true;
                                sp.edit().putBoolean("check", alarm_status).apply();
                            } else {
                                alarm_status = false;
                                sp.edit().putBoolean("check", alarm_status).apply();
                                Toast.makeText(MainActivity.this, "Alarm not set", Toast.LENGTH_SHORT).show();
                            }

                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            set[0] = "Enable Alarm";
                            name.setTitle(set[0]);
                            alarm_status = false;
                            sp.edit().putBoolean("check", alarm_status).apply();
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
        PendingIntent attended = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent message = PendingIntent.getBroadcast(getApplicationContext(), 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);


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
    public void profile() {
        Intent in = new Intent(MainActivity.this, Details.class);
        startActivity(in);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_notification) {
            notification();
        }

        if (id == R.id.nav_profile) {
            profile();
        }

        if (item.getItemId() == R.id.nav_send) {
            Intent in = new Intent(MainActivity.this, Messaging.class);
            startActivity(in);
        }

        if (item.getItemId() == R.id.nav_alarm) {
            if (alarm_status == true) {
                alarm_settings(true, item);
            } else {
                alarm_settings(false, item);
            }
        }

        if (item.getItemId() == R.id.nav_note) {
            Intent in = new Intent(MainActivity.this, Notes.class);
            startActivity(in);
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add_item) {
            insertitem();
        }

        if (item.getItemId() == R.id.number) {
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

        int checkpermission = ContextCompat.checkSelfPermission(this, sendSms);
        return checkpermission == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case SEND_SMS_PERMISSION_REQ:
                if (grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                }
                break;
        }
    }

    public void sms_permission() {
        if (!checkPermission(Manifest.permission.SEND_SMS)) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSION_REQ);
        }
    }

}

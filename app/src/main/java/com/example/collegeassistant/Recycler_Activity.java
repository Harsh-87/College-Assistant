package com.example.collegeassistant;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Recycler_Activity extends AppCompatActivity {
    ArrayList<ExampleItem> exampleList;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    ExampleAdapter mAdapter;

    EditText minsert,mremove;
    Button insertbutton,removebutton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_view);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar !=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        createExampleList();
        buildRecyclerView();
        setButtons();
    }
    public boolean onOptionsItemSelected(MenuItem item){
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


    public void insertitem(int position){
        exampleList.add(position,new ExampleItem("Line 7", 0,0,0));
        mAdapter.notifyDataSetChanged();
    }

    public void removeitem(int position){
        exampleList.remove(position);
        mAdapter.notifyDataSetChanged();
    }

    public void changeitem(int position,String text){
        exampleList.get(position).changeSubject(text);
        mAdapter.notifyDataSetChanged();
    }

    public void update_attendance_p(int position){
        exampleList.get(position).change_attended();
        exampleList.get(position).change_total();
        exampleList.get(position).change_percentage();
        mAdapter.notifyDataSetChanged();
    }

    public void update_attendance_a(int position){
        exampleList.get(position).change_total();
        exampleList.get(position).change_percentage();
        mAdapter.notifyDataSetChanged();
    }

    public void createExampleList(){
        exampleList = new ArrayList<>();
        exampleList.add(new ExampleItem( "Line 1", 0,0,0));
        exampleList.add(new ExampleItem( "Line 3", 0,0,0));
        exampleList.add(new ExampleItem( "Line 5", 0,0,0));
    }

    public void buildRecyclerView(){

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new ExampleAdapter(exampleList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new ExampleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                changeitem(position,"Clicked");
            }

            @Override
            public void onDeleteClick(int position) {
                removeitem(position);
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

    public void setButtons(){
        minsert = (EditText) findViewById(R.id.insert);
        insertbutton = (Button) findViewById(R.id.insert_button);
        mremove = (EditText) findViewById(R.id.remove);
        removebutton = (Button) findViewById(R.id.remove_button);

        insertbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = Integer.valueOf(minsert.getText().toString());
                insertitem(position);
            }
        });

        removebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = Integer.valueOf(mremove.getText().toString());
                removeitem(position);
            }
        });
    }

}

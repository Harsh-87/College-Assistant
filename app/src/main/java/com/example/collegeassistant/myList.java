package com.example.collegeassistant;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;


public class myList extends ArrayAdapter<String> {

    private final Activity context;
    private final ArrayList<String> maintitle;
    public static int[] attended_classes = new int[10];
    public static int[] total_classes = new int[10];
    public static double[] percentage = new double[10];

    public static int[] attendedid = new int[10];
    public static int[] totalid = new int[10];
    public static int[] percentid = new int[10];

    static customButtonListener customListner;

    public myList(Activity context, ArrayList<String> maintitle) {
        super(context,R.layout.activity_my_list, maintitle);
        this.context = context;
        this.maintitle = maintitle;
    }

    TextView titleText,percent,attendance,total;
    Button plus,minus;

    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        final View rowView = inflater.inflate(R.layout.activity_my_list, null, true);

        titleText = (TextView) rowView.findViewById(R.id.title);
        {
            percent = (TextView) rowView.findViewById(R.id.percent);
            plus = (Button) rowView.findViewById(R.id.plus);
            minus = (Button) rowView.findViewById(R.id.minus);
            attendance = (TextView) rowView.findViewById(R.id.attendance);
            total = (TextView) rowView.findViewById(R.id.total);

            attendedid[position] = attendance.getId();
            totalid[position] = total.getId();
            percentid[position] = percent.getId();
        }
        rowView.setLongClickable(true);

        attendance = (TextView) rowView.findViewById(attendedid[position]);
        total = (TextView) rowView.findViewById(totalid[position]);
        percent = (TextView) rowView.findViewById(percentid[position]);
        titleText.setText(maintitle.get(position));
        attendance.setText(String.valueOf(attended_classes[position]));
        total.setText(String.valueOf(total_classes[position]));
        percent.setText(String.valueOf(percentage[position]+"%"));

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (customListner != null) {
                    final String temp = getItem(position);
                    customListner.addition(position,temp);
                    modify(position);
                }
            }
            public void modify(int position){
                attendance = (TextView) rowView.findViewById(attendedid[position]);
                total = (TextView) rowView.findViewById(totalid[position]);
                percent = (TextView) rowView.findViewById(percentid[position]);
                attendance.setText(String.valueOf(attended_classes[position]));
                total.setText(String.valueOf(total_classes[position]));
                percent.setText(String.valueOf(percentage[position]+"%"));
            }
        });

        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (customListner != null) {
                    final String temp = getItem(position);
                    customListner.subtraction(position,temp);
                    modify(position);
                }
            }
            public void modify(int position){
                attendance = (TextView) rowView.findViewById(attendedid[position]);
                total = (TextView) rowView.findViewById(totalid[position]);
                percent = (TextView) rowView.findViewById(percentid[position]);
                attendance.setText(String.valueOf(attended_classes[position]));
                total.setText(String.valueOf(total_classes[position]));
                percent.setText(String.valueOf(percentage[position]+"%"));
            }
        });

        return rowView;

    };

    public void setCustomButtonListner(customButtonListener listener) {
        this.customListner = listener;
    }

}
package com.example.collegeassistant;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerView_Adapter extends RecyclerView.Adapter<RecyclerView_Adapter.ExampleViewHolder> {
    ArrayList<RecyclerView_Items> mExampleList;
    OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onItemLongClick(int position);
        void onPresentClick(int position);
        void onAbsentClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public static class ExampleViewHolder extends RecyclerView.ViewHolder{
        TextView mSubject,mAttended,mTotal,mPercent;
        Button mPresent,mAbsent;

        public ExampleViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            mSubject = (TextView)  itemView.findViewById(R.id.msubject);
            mPercent = (TextView) itemView.findViewById(R.id.mfinal_percent);
            mAttended = itemView.findViewById(R.id.attended_class);
            mTotal = itemView.findViewById(R.id.total_class);
            mPresent = itemView.findViewById(R.id.mark_present);
            mAbsent = itemView.findViewById(R.id.mark_absent);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if(listener!=null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemLongClick(position);
                        }
                    }
                    return false;
                }
            });

            mPresent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener!=null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onPresentClick(position);
                        }
                    }
                }
            });

            mAbsent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener!=null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onAbsentClick(position);
                        }
                    }
                }
            });

        }
    }

    public RecyclerView_Adapter(ArrayList<RecyclerView_Items> exampleList){
        mExampleList = exampleList;
    }

    @NonNull
    @Override
    public ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout,parent,false);
        ExampleViewHolder evh = new ExampleViewHolder(v,mListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull ExampleViewHolder holder, int position) {
        RecyclerView_Items currentitem = mExampleList.get(position);
        holder.mSubject.setText(currentitem.getSubject());
        Double perc = Double.valueOf(currentitem.getPercent());
        if(perc > 75.00 ){
            holder.mPercent.setText(perc.toString());
            holder.mPercent.setTextColor(Color.parseColor("#00994C"));
        }else{
            holder.mPercent.setText(perc.toString());
            holder.mPercent.setTextColor(Color.parseColor("#FF0000"));
        }
        holder.mAttended.setText(currentitem.getAttended());
        holder.mTotal.setText(currentitem.getTotal());
    }

    @Override
    public int getItemCount() {
        return mExampleList.size();
    }

}

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

public class ExampleAdapter extends RecyclerView.Adapter<ExampleAdapter.ExampleViewHolder> {
    ArrayList<ExampleItem> mExampleList;
    OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onItemClick(int position);
        void onDeleteClick(int position);
        void onPresentClick(int position);
        void onAbsentClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public static class ExampleViewHolder extends RecyclerView.ViewHolder{
        TextView mSubject,mAttended,mTotal,mPercent;
        ImageView mDeleteImage;
        Button mPresent,mAbsent;

        public ExampleViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            mSubject = (TextView)  itemView.findViewById(R.id.msubject);
            mPercent = (TextView) itemView.findViewById(R.id.mfinal_percent);
            mDeleteImage = (ImageView) itemView.findViewById(R.id.mdelete);
            mAttended = itemView.findViewById(R.id.attended_class);
            mTotal = itemView.findViewById(R.id.total_class);
            mPresent = itemView.findViewById(R.id.mark_present);
            mAbsent = itemView.findViewById(R.id.mark_absent);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener!=null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });

            mDeleteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener!=null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onDeleteClick(position);
                        }
                    }
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

    public ExampleAdapter(ArrayList<ExampleItem> exampleList){
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
        ExampleItem currentItem = mExampleList.get(position);
        holder.mSubject.setText(currentItem.getSubject());
        if(Double.valueOf(currentItem.getPercent()) > 75.00 ){
            holder.mPercent.setText(currentItem.getPercent());
            holder.mPercent.setTextColor(Color.GREEN);
        }else{
            holder.mPercent.setText(currentItem.getPercent());
            holder.mPercent.setTextColor(Color.RED);
        }

        holder.mAttended.setText(currentItem.getAttended());
        holder.mTotal.setText(currentItem.getTotal());
    }

    @Override
    public int getItemCount() {
        return mExampleList.size();
    }


}

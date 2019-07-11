package com.example.collegeassistant;

public class RecyclerView_Items {

    private String mSubject;
    private int mAttended;
    private int mTotal;
    private double mPercentage;


    public RecyclerView_Items(String subject, double percent, int attended, int total) {
        mSubject = subject;
        mAttended = attended;
        mTotal = total;
        mPercentage = percent;
    }


    public String getSubject() {
        return mSubject;
    }

    public String getPercent() {
        return ""+mPercentage;
    }

    public String getAttended() {
        return ""+mAttended;
    }

    public String getTotal() {
        return ""+mTotal;
    }

    public void change_attended(){
        mAttended++;
    }

    public void change_total(){
        mTotal++;
    }

    public void change_percentage(){
        mPercentage = (double) mAttended / mTotal ;
        mPercentage = Math.round(mPercentage * 10000.0) / 100.0;
    }

    public void changeSubject(String text){
        mSubject = text;
    }


}

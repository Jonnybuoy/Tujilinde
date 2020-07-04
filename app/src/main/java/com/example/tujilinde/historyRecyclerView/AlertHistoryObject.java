package com.example.tujilinde.historyRecyclerView;

public class AlertHistoryObject {
    private String date;
    private String refCode;

    public AlertHistoryObject(String date, String refCode){
        this.date = date;
        this.refCode = refCode;
    }

    public String getDate(){return date;}
    public void setDate(String date){this.date = date;}

    public String getRefCode(){return refCode;}
    public void setRefCode(String refCode){this.refCode = refCode;}
}

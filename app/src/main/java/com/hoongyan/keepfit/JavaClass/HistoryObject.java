package com.hoongyan.keepfit.JavaClass;

import com.google.firebase.Timestamp;

public class HistoryObject {
    private String documentID;
    private int type;
    private String historyTitle;
    private double totalCalorie;
    private Timestamp timestamp;

    public HistoryObject(){

    }

    public HistoryObject(String documentID, int type, String historyTitle, double totalCalorie, Timestamp timestamp) {
        this.documentID = documentID;
        this.type = type;
        this.historyTitle = historyTitle;
        this.totalCalorie = totalCalorie;
        this.timestamp = timestamp;
    }

    public String getDocumentID(){return documentID;}

    public int getType(){return type;}

    public String getHistoryTitle() {
        return historyTitle;
    }

    public void setHistoryTitle(String newTitle){
        historyTitle = newTitle;
    }

    public double getTotalCalorie() {
        return totalCalorie;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }
}

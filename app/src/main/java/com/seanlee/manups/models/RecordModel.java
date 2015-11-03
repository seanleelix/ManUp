package com.seanlee.manups.models;

/**
 * Created by Sean Lee on 3/11/15.
 */
public class RecordModel {

    private String date;
    private int pushup;
    private int situp;
    private int running;

    public RecordModel(String date, int pushup, int situp, int running) {
        setDate(date);
        setPushup(pushup);
        setSitup(situp);
        setRunning(running);
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getPushup() {
        return pushup;
    }

    public void setPushup(int pushup) {
        this.pushup = pushup;
    }

    public int getSitup() {
        return situp;
    }

    public void setSitup(int situp) {
        this.situp = situp;
    }

    public int getRunning() {
        return running;
    }

    public void setRunning(int running) {
        this.running = running;
    }
}

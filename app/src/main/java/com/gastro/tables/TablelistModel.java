package com.gastro.tables;

import com.gastro.database.States;

import java.util.Map;

public class TablelistModel {


    public States curState = States.SORTNUMBER;

    private static TablelistModel instance;

    private Map<String, String> tableNumAndTimer;
    private Map<String, Long> tableNumAndStatus;

    private TablelistModel() {
    }

    public static TablelistModel getInstance() {
        if (instance == null) {
            instance = new TablelistModel();
        }
        return instance;
    }

    public void setTableNumAndTimerMap(Map<String, String> tableNumAndTimer){
        this.tableNumAndTimer = tableNumAndTimer;
    }

    public Map<String, String> getTableNumAndTimerMap(){
        return this.tableNumAndTimer;
    }

    public Map<String, Long> getTableNumAndStatus() {
        return tableNumAndStatus;
    }

    public void setTableNumAndStatus(Map<String, Long> tableNumAndStatus) {
        this.tableNumAndStatus = tableNumAndStatus;
    }
}

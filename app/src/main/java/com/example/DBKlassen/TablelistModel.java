package com.example.DBKlassen;

import java.util.Map;

public class TablelistModel {


    public sortState curState = sortState.SORTTIMER;

    private static TablelistModel instance;

    private Map<String, String> tableNumAndTimer;

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

}

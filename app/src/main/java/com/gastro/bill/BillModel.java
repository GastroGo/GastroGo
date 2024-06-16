package com.gastro.bill;

import java.util.HashMap;
import java.util.Map;

public class BillModel {
    public Map<String, Long> orders = new HashMap<>();
    public Map<String, String> dishNames = new HashMap<>();
    public Map<String, Double> dishCosts = new HashMap<>();
    static BillModel instance;

    private BillModel(){

    }

    public static BillModel getInstance() {
        if (instance == null) {
            instance = new BillModel();
        }
        return instance;
    }
}

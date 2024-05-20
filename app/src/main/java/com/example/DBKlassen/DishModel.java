package com.example.DBKlassen;

import java.util.List;
import java.util.Map;

public class DishModel {
    private static DishModel instance;
    public states curState = states.OPEN;
    private Map<String, Long> orders;
    private Map<String, String> dishNames;
    private List<String> closingDishes;

    private DishModel() {}

    public static DishModel getInstance(){
        if (instance == null){
            instance = new DishModel();
        }
        return instance;
    }

    public Map<String, Long> getOrders() {
        return orders;
    }

    public void setOrders(Map<String, Long> orders) {
        this.orders = orders;
    }

    public Map<String, String> getDishNames() {
        return dishNames;
    }

    public void setDishNames(Map<String, String> dishNames) {
        this.dishNames = dishNames;
    }

    public List<String> getClosingDishes() {
        return closingDishes;
    }

    public void setClosingDishes(List<String> closingDishes) {
        this.closingDishes = closingDishes;
    }
}

package com.gastro.database;

import java.util.Map;

public class Restaurant {

    private Data data;
    private Map<String, Menu> speisekarte;
    private Map<String, Table> tische;

    public Restaurant(Data data, Map<String, Menu> speisekarte, Map<String, Table> tische) {
        this.data = data;
        this.speisekarte = speisekarte;
        this.tische = tische;
    }

    public Restaurant() {
    }

    public Data getDaten() {
        return data;
    }

    public void setDaten(Data data) {
        this.data = data;
    }


    public Map<String, Menu> getSpeisekarte() {
        return speisekarte;
    }

    public void setSpeisekarte(Map<String, Menu> speisekarte) {
        this.speisekarte = speisekarte;
    }

    public Map<String, Table> getTische() {
        return tische;
    }

    public void setTische(Map<String, Table> tische) {
        this.tische = tische;
    }
}
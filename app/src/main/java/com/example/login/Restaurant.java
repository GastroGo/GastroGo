package com.example.login;

import java.util.Map;

public class Restaurant {

    private Data data;
    private Map<String, Speisekarte> speisekarte;
    private Map<String, Tisch> tische;

    public Restaurant(Data data, Map<String, Speisekarte> speisekarte, Map<String, Tisch> tische) {
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


    public Map<String, Speisekarte> getSpeisekarte() {
        return speisekarte;
    }

    public void setSpeisekarte(Map<String, Speisekarte> speisekarte) {
        this.speisekarte = speisekarte;
    }

    public Map<String, Tisch> getTische() {
        return tische;
    }

    public void setTische(Map<String, Tisch> tische) {
        this.tische = tische;
    }
}
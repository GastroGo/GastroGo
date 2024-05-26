package com.example.login;

import java.util.Map;

public class Restaurant {

    private Data daten;
    private Map<String, Speisekarte> speisekarte;
    private Map<String, Tisch> tische;

    public Restaurant(Data daten, Map<String, Speisekarte> speisekarte, Map<String, Tisch> tische) {
        this.daten = daten;
        this.speisekarte = speisekarte;
        this.tische = tische;
    }

    public Restaurant() {
    }

    public Data getDaten() {
        return daten;
    }

    public void setDaten(Data daten) {
        this.daten = daten;
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
package com.example.login;

import java.util.Map;

public class Restaurant {

    private Daten daten;
    private Map<String, String> schluessel;
    private Map<String, Speisekarte> speisekarte;
    private Map<String, Tisch> tische;

    public Restaurant(Daten daten, Map<String, String> schluessel, Map<String, Speisekarte> speisekarte, Map<String, Tisch> tische) {
        this.daten = daten;
        this.schluessel = schluessel;
        this.speisekarte = speisekarte;
        this.tische = tische;
    }

    public Restaurant() {
    }

    public Daten getDaten() {
        return daten;
    }

    public void setDaten(Daten daten) {
        this.daten = daten;
    }

    public Map<String, String> getSchluessel() {
        return schluessel;
    }

    public void setSchluessel(Map<String, String> schluessel) {
        this.schluessel = schluessel;
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
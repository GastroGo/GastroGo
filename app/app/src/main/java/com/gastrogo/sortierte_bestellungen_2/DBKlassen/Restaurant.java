package com.gastrogo.sortierte_bestellungen_2.DBKlassen;

import java.util.Map;

public class Restaurant {

    private Daten daten;
    private Map<String, Gericht> speisekarte;

    public Restaurant(Daten daten, Map<String, Gericht> speisekarte) {
        this.daten = daten;
        this.speisekarte = speisekarte;
    }

    public Restaurant() {
    }

    public Daten getDaten() {
        return daten;
    }

    public void setDaten(Daten daten) {
        this.daten = daten;
    }

    public Map<String, Gericht> getSpeisekarte() {
        return speisekarte;
    }

    public void setSpeisekarte(Map<String, Gericht> speisekarte) {
        this.speisekarte = speisekarte;
    }
}
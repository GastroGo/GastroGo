package com.example.login;

import java.util.Map;

public class Speisekarte {
    private String gericht;
    private Double preis;
    private Map<String, Boolean> allergien, zutaten;

    public Speisekarte() {
    }

    public Speisekarte(String gericht, Double preis, Map<String, Boolean> allergien, Map<String, Boolean> zutaten) {
        this.gericht = gericht;
        this.preis = preis;
        this.allergien = allergien;
        this.zutaten = zutaten;
    }

    public Speisekarte(String gericht, double preis) {
        this.gericht = gericht;
        this.preis = preis;
    }

    public String getGericht() {
        return gericht;
    }

    public void setGericht(String gericht) {
        this.gericht = gericht;
    }

    public Double getPreis() {
        return preis;
    }

    public void setPreis(Double preis) {
        this.preis = preis;
    }

    public Map<String, Boolean> getAllergien() {
        return allergien;
    }

    public void setAllergien(Map<String, Boolean> allergien) {
        this.allergien = allergien;
    }

    public Map<String, Boolean> getZutaten() {
        return zutaten;
    }

    public void setZutaten(Map<String, Boolean> zutaten) {
        this.zutaten = zutaten;
    }
}
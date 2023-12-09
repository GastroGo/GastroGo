package com.example.login;

import java.util.Map;

public class Tisch {
    private Map<String, Integer> bestellungen;
    private int personen;

    public Tisch(Map<String, Integer> bestellungen, int personen) {
        this.bestellungen = bestellungen;
        this.personen = personen;
    }

    public Map<String, Integer> getBestellungen() {
        return bestellungen;
    }

    public void setBestellungen(Map<String, Integer> bestellungen) {
        this.bestellungen = bestellungen;
    }

    public int getPersonen() {
        return personen;
    }

    public void setPersonen(int personen) {
        this.personen = personen;
    }
}

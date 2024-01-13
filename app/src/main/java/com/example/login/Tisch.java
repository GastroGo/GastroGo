package com.example.login;

import java.util.Map;

public class Tisch {
    private Map<String, Integer> bestellungen;
    private Map<String, Integer> geschlosseneBestellungen;
    private int personen;

    public Tisch(Map<String, Integer> bestellungen, Map<String, Integer> geschlosseneBestellungen, int personen) {
        this.bestellungen = bestellungen;
        this.geschlosseneBestellungen = geschlosseneBestellungen;
        this.personen = personen;
    }

    public Tisch() {
    }

    public Map<String, Integer> getBestellungen() {
        return bestellungen;
    }

    public void setBestellungen(Map<String, Integer> bestellungen) {
        this.bestellungen = bestellungen;
    }

    public Map<String, Integer> getGeschlosseneBestellungen() {
        return geschlosseneBestellungen;
    }

    public void setGeschlosseneBestellungen(Map<String, Integer> geschlosseneBestellungen) {
        this.geschlosseneBestellungen = geschlosseneBestellungen;
    }

    public int getPersonen() {
        return personen;
    }

    public void setPersonen(int personen) {
        this.personen = personen;
    }
}

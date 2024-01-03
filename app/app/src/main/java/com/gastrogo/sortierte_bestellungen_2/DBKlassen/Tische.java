package com.gastrogo.sortierte_bestellungen_2.DBKlassen;

import java.util.Map;

public class Tische {
    private int personen;
    private Map<String, Integer> bestellungen;

    public Tische() {
    }

    public int getPersonen() {
        return personen;
    }

    public void setPersonen(int personen) {
        this.personen = personen;
    }

    public Map<String, Integer> getBestellungen() {
        return bestellungen;
    }

    public void setBestellungen(Map<String, Integer> bestellungen) {
        this.bestellungen = bestellungen;
    }
}

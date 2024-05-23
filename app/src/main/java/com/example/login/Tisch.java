package com.example.login;

import java.util.Map;

public class Tisch {
    private Map<String, Integer> bestellungen;
    private Map<String, Integer> geschlosseneBestellungen;
    private int personen;
    private String letzteBestellung;
    private int status;

    public Tisch(Map<String, Integer> bestellungen, Map<String, Integer> geschlosseneBestellungen, int personen, String letzteBestellung, int Status) {
        this.bestellungen = bestellungen;
        this.geschlosseneBestellungen = geschlosseneBestellungen;
        this.personen = personen;
        this.letzteBestellung = letzteBestellung;
        this.status = Status;
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

    public String getLetzteBestellung() {
        return letzteBestellung;
    }

    public void setLetzteBestellung(String letzteBestellung) {
        this.letzteBestellung = letzteBestellung;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}

package com.gastrogo.sortierte_bestellungen_2.DBKlassen;

import android.util.Log;

public class GerichteModel {
    Gericht[] gerichte;

    private static GerichteModel instance;

    private GerichteModel() {}

    public static GerichteModel getInstance() {
        if (instance == null){
            instance = new GerichteModel();
        }
        return instance;
    }

    public Gericht[] getGerichte() {
        return gerichte;
    }

    public void setGerichte(Gericht[] gerichte) {
        this.gerichte = gerichte;
    }

    public void setup(int anzahlGerichte){
        gerichte = new Gericht[anzahlGerichte];
    }

    public String getGerichtName(String gerichtNummer){
        String gerichtName = gerichte[getGerichNummer(gerichtNummer) - 1].getGericht();

        return gerichtName;
    }

    public int getGerichNummer(String gerichtNummer){
        StringBuilder gericht = new StringBuilder(gerichtNummer);
        StringBuilder sbNummer = new StringBuilder(gericht.length() - 1);
        String sNummer;
        int iNummer;

        for (int x = gericht.length() - 1; x > 0; x--){
            if (String.valueOf(gericht.charAt(x)).matches("[0-9]")){
                sbNummer.insert(0, gericht.charAt(x));
            }
        }

        sNummer = sbNummer.toString();
        iNummer = Integer.parseInt(sNummer);
        return iNummer;
    }

}

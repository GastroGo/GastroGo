package com.gastro.database;

public class DishModel {
    Dish[] gerichte;

    private static DishModel instance;

    private DishModel() {}

    public static DishModel getInstance() {
        if (instance == null){
            instance = new DishModel();
        }
        return instance;
    }

    public Dish[] getGerichte() {
        return gerichte;
    }

    public void setGerichte(Dish[] gerichte) {
        this.gerichte = gerichte;
    }

    public void setup(int anzahlGerichte){
        gerichte = new Dish[anzahlGerichte];
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

    public int getGerichtIndex(String gerichtName){
        for(int x = 0; x < gerichte.length; x++) {
            if (gerichte[x].getGericht().equalsIgnoreCase(gerichtName)) {
                return x;
            }
        }
        return -1;
    }

}

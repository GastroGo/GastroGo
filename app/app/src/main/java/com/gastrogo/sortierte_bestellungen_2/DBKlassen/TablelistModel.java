package com.gastrogo.sortierte_bestellungen_2.DBKlassen;

public class TablelistModel {

    private static TablelistModel instance;

    private Tische tischeArray[];



    private int numberOfTables;

    private TablelistModel() {
    }

    public static TablelistModel getInstance() {
        if (instance == null) {
            instance = new TablelistModel();
        }
        return instance;
    }

    public Tische[] getTischeArray(){
        return tischeArray;
    }

    public int getNumberOfTables() {
        return numberOfTables;
    }

    public void setNumberOfTables(int numberOfTables) {
        this.numberOfTables = numberOfTables;
    }

    public void setup(){
        tischeArray = new Tische[numberOfTables];
    }

    public void setup(int numberOfTables){
        this.numberOfTables = numberOfTables;
        tischeArray = new Tische[this.numberOfTables];
    }

    public void setTable(Tische table, int index){
        tischeArray[index] = table;
    }

    public int getIndexOf(String tableName){
        StringBuilder tName = new StringBuilder(tableName);
        StringBuilder sbNummer = new StringBuilder(tName.length() - 1);
        String sNummer;
        int iNummer;

        for (int x = tName.length() - 1; x > 0; x--){
            if (String.valueOf(tName.charAt(x)).matches("[0-9]")){
                sbNummer.insert(0, tName.charAt(x));
            }
        }

        sNummer = sbNummer.toString();
        iNummer = Integer.parseInt(sNummer);

        return iNummer;
    }

}

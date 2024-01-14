package com.example.DBKlassen;
import com.example.login.Tisch;

public class TablelistModel {

    private static TablelistModel instance;

    private Tisch tischeArray[];

    private byte bestellungsFilter;

    private int numberOfTables;

    private TablelistModel() {
    }

    public static TablelistModel getInstance() {
        if (instance == null) {
            instance = new TablelistModel();
        }
        return instance;
    }

    public Tisch[] getTischeArray(){
        return tischeArray;
    }

    public int getNumberOfTables() {
        return numberOfTables;
    }

    public void setNumberOfTables(int numberOfTables) {
        this.numberOfTables = numberOfTables;
    }

    public void setup(){
        tischeArray = new Tisch[numberOfTables];
    }

    public void setup(int numberOfTables){
        this.numberOfTables = numberOfTables;
        tischeArray = new Tisch[this.numberOfTables];
        bestellungsFilter = 1;
    }

    public void setTable(Tisch table, int index){
        tischeArray[index] = table;
    }

    public byte getBestellungsFilter() {
        return bestellungsFilter;
    }

    public void setBestellungsFilter(byte bestellungsFilter) {
        this.bestellungsFilter = bestellungsFilter;
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

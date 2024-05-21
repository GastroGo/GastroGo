package com.example.login;

public class Data {
    private int hausnr, plz;
    private String id, name, ort, strasse, uid;
    private boolean speisekarte;

    public Data(int hausnr, int plz, String id, String name, String ort, String strasse, String uid, boolean speisekarte) {
        this.hausnr = hausnr;
        this.plz = plz;
        this.id = id;
        this.name = name;
        this.ort = ort;
        this.strasse = strasse;
        this.speisekarte = speisekarte;
        this.uid = uid;
    }

    public Data(String id) {
        this.id = id;
    }

    public Data() {

    }

    public int getHausnr() {
        return hausnr;
    }

    public void setHausnr(int hausnr) {
        this.hausnr = hausnr;
    }

    public int getPlz() {
        return plz;
    }

    public void setPlz(int plz) {
        this.plz = plz;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrt() {
        return ort;
    }

    public void setOrt(String ort) {
        this.ort = ort;
    }

    public String getStrasse() {
        return strasse;
    }

    public void setStrasse(String strasse) {
        this.strasse = strasse;
    }

    public boolean isSpeisekarte() {
        return speisekarte;
    }

    public void setSpeisekarte(boolean speisekarte) {
        this.speisekarte = speisekarte;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
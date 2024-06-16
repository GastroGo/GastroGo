package com.gastro.qrcodereader;

import java.io.Serializable;
import java.util.List;

public class Dish implements Serializable {
    private String id, gericht;
    private List<String> allergien, zutaten;
    private Double preis;
    private boolean isSelected = false;
    private int amount;
    private int finalAmount = 0;
    private AmountChangeListener amountChangeListener;

    public Dish() {
    }

    public Dish(String id, Double preis, String gericht, List<String> allergien, List<String> zutaten, boolean isSelected) {
        this.id = id;
        this.preis = preis;
        this.gericht = gericht;
        this.allergien = allergien;
        this.zutaten = zutaten;
        this.isSelected = isSelected;
    }

    public Dish(String id, Double preis, String gericht, List<String> zutaten) {
        this.id = id;
        this.preis = preis;
        this.gericht = gericht;
        this.zutaten = zutaten;
    }

    public void setAmountChangeListener(AmountChangeListener listener) {
        this.amountChangeListener = listener;
    }

    private void notifyOnAmountChanged() {
        if (amountChangeListener != null) {
            amountChangeListener.onAmountChanged();
        }
    }

    public Double getPreis() {
        return preis;
    }

    public void setPreis(Double preis) {
        this.preis = preis;
    }

    public String getGerichtName() {
        return gericht;
    }

    public void setGerichtName(String gericht) {
        this.gericht = gericht;
    }

    public List<String> getAllergien() {
        return allergien;
    }

    public void setAllergien(List<String> allergien) {
        this.allergien = allergien;
    }

    public List<String> getZutaten() {
        return zutaten;
    }

    public void setZutaten(List<String> zutaten) {
        this.zutaten = zutaten;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getFinalAmount() {
        return finalAmount;
    }
    public String getId(){
        return id;
    }
    public void setId(String id){
        this.id = id;
    }

    public void setFinalAmount(int finalAmount) {
        this.finalAmount = finalAmount;
        notifyOnAmountChanged();
    }


}
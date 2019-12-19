package com.ygaps.travelapp.Model;

public class SpinnerReportItem {
    private String text;
    private int img;

    public SpinnerReportItem(String countryName, int flagImage) {
        text = countryName;
        img = flagImage;
    }

    public String getText() {
        return text;
    }

    public int getImg() {
        return img;
    }
}

package com.project.scanner.data;


import android.graphics.Bitmap;
import android.os.Parcelable;

import com.google.android.gms.vision.barcode.Barcode;
import com.project.scanner.util.ScannerUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class BarcodeData {

    private String displayValue;
    private int format;
    private String type;
    private String date;
    private Bitmap bitmap;

    public BarcodeData(){
        displayValue = "";
        format = 0;
        type = "";
        date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    public BarcodeData(String displayValue, int format, String type, String date){
        this.displayValue = displayValue;
        this.format = format;
        this.type = type;
        this.date = date;
    }

    public BarcodeData(Barcode barcode){
        displayValue = barcode.displayValue;
        format = barcode.format;
        type = ScannerUtil.getBarcodeType(barcode.valueFormat);
        date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
    }

    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
    }

    public void setFormat(int format) {
        this.format = format;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    public int getFormat() {
        return format;
    }

    public String getType() {
        return type;
    }

    public String getDate() {
        return date;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }



}

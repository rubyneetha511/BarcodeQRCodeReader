package com.project.scanner.util;

import com.google.android.gms.vision.barcode.Barcode;

/**
 * Created by vinaypra on 8/4/17.
 */

public class ScannerUtil {

    public static String getBarcodeFormat(int format) {
        String result = "";
        switch (format) {
            case Barcode.CODE_128:
                result = "CODE_128"; break;
            case Barcode.CODE_39:
                result = "CODE_39"; break;
            case Barcode.CODE_93:
                result = "CODE_93"; break;
            case Barcode.CODABAR:
                result = "CODABAR"; break;
            case Barcode.DATA_MATRIX:
                result = "DATA_MATRIX"; break;
            case Barcode.EAN_13:
                result = "EAN_13"; break;
            case Barcode.EAN_8:
                result = "EAN_8"; break;
            case Barcode.ITF:
                result = "ITF"; break;
            case Barcode.QR_CODE:
                result = "QR_CODE"; break;
            case Barcode.UPC_A:
                result = "UPC_A"; break;
            case Barcode.UPC_E:
                result = "UPC_E"; break;
            case Barcode.PDF417:
                result = "PDF417"; break;
            case Barcode.AZTEC:
                result = "AZTEC"; break;
        }
        return result;

    }

    public static String getBarcodeType(int valueFormat) {
        String resultType = "";
        switch (valueFormat) {
            case Barcode.EMAIL:
                resultType = "EMAIL"; break;
            case Barcode.ISBN :
                resultType = "ISBN"; break;
            case Barcode.PHONE:
                resultType = "PHONE"; break;
            case Barcode.PRODUCT:
                resultType = "PRODUCT"; break;
            case Barcode.SMS:
                resultType = "SMS"; break;
            case Barcode.TEXT:
                resultType = "TEXT"; break;
            case Barcode.URL:
                resultType = "URL"; break;
            case Barcode.WIFI:
                resultType = "WIFI"; break;
            case Barcode.GEO:
                resultType = "GEO"; break;
            case Barcode.CALENDAR_EVENT:
                resultType = "CALENDAR_EVENT"; break;
            case Barcode.DRIVER_LICENSE:
                resultType = "DRIVER_LICENSE"; break;
        }
        return resultType;

    }
}

package com.project.scanner.util;

import android.graphics.Bitmap;

import com.google.android.gms.vision.barcode.Barcode;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.EnumMap;
import java.util.Map;

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

    private static BarcodeFormat convertToZXingFormat(int format)
    {
        switch (format)
        {
            case 8:
                return BarcodeFormat.CODABAR;
            case 1:
                return BarcodeFormat.CODE_128;
            case 2:
                return BarcodeFormat.CODE_39;
            case 4:
                return BarcodeFormat.CODE_93;
            case 32:
                return BarcodeFormat.EAN_13;
            case 64:
                return BarcodeFormat.EAN_8;
            case 128:
                return BarcodeFormat.ITF;
            case 512:
                return BarcodeFormat.UPC_A;
            case 1024:
                return BarcodeFormat.UPC_E;
            //default 128?
            default:
                return BarcodeFormat.CODE_128;
        }
    }

    public static Bitmap getBitmap(String barcode, int barcodeType, int width, int height) {
        Bitmap barcodeBitmap = null;
        BarcodeFormat barcodeFormat = convertToZXingFormat(barcodeType);
        try
        {
            barcodeBitmap = encodeAsBitmap(barcode, barcodeFormat, width, height);
        }
        catch (WriterException e)
        {
            e.printStackTrace();
        }
        return barcodeBitmap;
    }

    /**************************************************************
     * getting from com.google.zxing.client.android.encode.QRCodeEncoder
     *
     * See the sites below
     * http://code.google.com/p/zxing/
     * http://code.google.com/p/zxing/source/browse/trunk/android/src/com/google/zxing/client/android/encode/EncodeActivity.java
     * http://code.google.com/p/zxing/source/browse/trunk/android/src/com/google/zxing/client/android/encode/QRCodeEncoder.java
     */

    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;

    private static Bitmap encodeAsBitmap(String contents, BarcodeFormat format, int img_width, int img_height) throws WriterException
    {
        if (contents == null) {
            return null;
        }
        Map<EncodeHintType, Object> hints = null;
        String encoding = guessAppropriateEncoding(contents);
        if (encoding != null) {
            hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        }
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result;
        try {
            result = writer.encode(contents, format, img_width, img_height, hints);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    private static String guessAppropriateEncoding(CharSequence contents) {
        // Very crude at the moment
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) {
                return "UTF-8";
            }
        }
        return null;
    }
}

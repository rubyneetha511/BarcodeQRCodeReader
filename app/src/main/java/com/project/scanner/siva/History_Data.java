package com.project.scanner.siva;

/**
 * Created by siva on 7/11/17.
 */

public class History_Data {

    String scantext;
    String scanformat;

    History_Data(String scantext, String scanformat){
        this.scantext=scantext;
        this.scanformat=scanformat;

    }

    public void setScantext(String scantext) {
        this.scantext = scantext;
    }

    public void setScanformat(String scanformat) {
        this.scanformat = scanformat;
    }

    public String getScantext() {

        return scantext;
    }

    public String getScanformat() {
        return scanformat;
    }
}

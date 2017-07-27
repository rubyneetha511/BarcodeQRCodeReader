package com.project.scanner.data;


public class HistoryData {

    public String dataValue;
    public String dataFormat;
    public String dataType;

    public HistoryData(String dataValue, String dataFormat){
        this.dataValue = dataValue;
        this.dataFormat = dataFormat;
    }

    public void setDataValue(String dataValue) {
        this.dataValue = dataValue;
    }

    public void setDataFormat(String dataFormat) {
        this.dataFormat = dataFormat;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
    public String getDataValue() {
        return dataValue;
    }

    public String getDataFormat() {
        return dataFormat;
    }

    public String getDataType() {
        return dataType;
    }

}

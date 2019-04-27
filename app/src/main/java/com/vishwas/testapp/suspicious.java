package com.vishwas.testapp;

public class suspicious {
    String mobileno;
    String url;

    public suspicious(String mobileno, String url) {
        this.mobileno = mobileno;
        this.url = url;
    }


    public String getMobileno() {
        return mobileno;
    }

    public void setMobileno(String mobileno) {
        this.mobileno = mobileno;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

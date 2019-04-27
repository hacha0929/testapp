package com.vishwas.testapp;

public class visitor {

    String url;
    String mobileno;
    int visitcount;

    public visitor() {
    }

    public visitor(String url, String mobileno, int visitcount) {
        this.url = url;
        this.mobileno = mobileno;
        this.visitcount = visitcount;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMobileno() {
        return mobileno;
    }

    public void setMobileno(String mobileno) {
        this.mobileno = mobileno;
    }

    public int getVisitcount() {
        return visitcount;
    }

    public void setVisitcount(int visitcount) {
        this.visitcount = visitcount;
    }
}

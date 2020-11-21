package com.xinlan.imageeditlibrary.editimage.bean;

import java.util.List;

public class ImgList {

    private List<ImageLogo> results;
    private int total;
    private int total_pages;

    public void setResults(List<ImageLogo> results) {
        this.results = results;
    }

    public List<ImageLogo> getResults() {
        return results;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal_pages(int total_pages) {
        this.total_pages = total_pages;
    }

    public int getTotal_pages() {
        return total_pages;
    }

    @Override
    public String toString() {
        return "ImgList{" +
                "results=" + results +
                ", total=" + total +
                ", total_pages=" + total_pages +
                '}';
    }
}
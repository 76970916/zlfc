package com.zlfcapp.poster.bean;

import java.util.List;

public class FeedBacks {

    private List<Results> results;
    private int total;
    private int total_pages;

    public void setResults(List<Results> results) {
        this.results = results;
    }

    public List<Results> getResults() {
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

}
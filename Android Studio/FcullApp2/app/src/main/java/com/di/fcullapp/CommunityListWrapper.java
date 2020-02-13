package com.di.fcullapp;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class CommunityListWrapper {

    @JsonProperty("list")
    private List<Community> list;


    public CommunityListWrapper() {
        // TODO Auto-generated constructor stub
    }

    public List<Community> getList() {
        return list;
    }

    public void setList(List<Community> someList) {
        this.list = someList;
    }
}

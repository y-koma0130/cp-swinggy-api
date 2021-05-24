package com.example.demo.application.prefecture;

public class PrefectureModel {

    private final int id;
    private final String name;
    private final int region_id;

    public PrefectureModel(int id, String name, int region_id) {
        this.id = id;
        this.region_id = region_id;
        this.name = name;
    }

    public int getId() {return id;}
    public String getName() {return name;}
    public int getRegion_Id() {return region_id;}
}
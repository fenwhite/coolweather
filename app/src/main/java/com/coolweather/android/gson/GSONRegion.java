package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

public class GSONRegion{
    private int id;
    private String name;
    @SerializedName("weather_id")
    private String weatherId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }
}

package com.coolweather.android.db;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

public class Region extends LitePalSupport {
    @Column(unique = true)
    private String id;
    private String name;
    private String cityId;
    private String weatherId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }
}

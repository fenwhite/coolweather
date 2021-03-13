package com.coolweather.android.db;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

public class Region extends LitePalSupport {
    private int code;
    private String name;
    private int cityId;
    private String weatherId;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }
}

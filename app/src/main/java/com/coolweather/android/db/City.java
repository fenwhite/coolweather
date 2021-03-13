package com.coolweather.android.db;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

public class City extends LitePalSupport {
    private int code;
    private String name;
    private int provinceId;

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

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }
}

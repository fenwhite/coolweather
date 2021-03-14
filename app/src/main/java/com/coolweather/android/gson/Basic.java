package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

public class Basic {
    @SerializedName("id")
    public String weatherId;

    @SerializedName("city")
    public String regionName;

    @SerializedName("lon")
    public String longitude;
    @SerializedName("lat")
    public String latitude;

    @SerializedName("admin_area")
    public String provinceName;
    @SerializedName("cnty")
    public String countryName;
    @SerializedName("parent_city")
    public String cityName;

    public Update update;

    public class Update{
        @SerializedName("loc")
        public String updateTime;
        @SerializedName("utc")
        public String utcTime;
    }
}

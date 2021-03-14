package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

public class Now {
    @SerializedName("tmp")
    public String temperature;

    @SerializedName("wind_dir")
    public String windDir;
    @SerializedName("cond_txt")
    public String weather;

    public class More{
        @SerializedName("txt")
        public String info;
    }
}

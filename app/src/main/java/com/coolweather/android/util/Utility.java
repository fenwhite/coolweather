package com.coolweather.android.util;

import android.text.TextUtils;

import com.coolweather.android.db.City;
import com.coolweather.android.db.Province;
import com.coolweather.android.db.Region;
import com.coolweather.android.gson.GSONRegion;
import com.coolweather.android.gson.Weather;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class Utility {
    /**
     * 拼装get请求
     * @param base 服务器地址
     * @param params 请求参数
     * @return url
     */
    public static String makeUrl(String base, Map<String,String> params){
        return "";
    }

    /**
     * 拼装get请求 将base和others直接衔接
     * @param base 服务器地址
     * @param others
     * @return
     */
    public static String makeUrl(String base,String others){
        return base+others;
    }

    /**
     * 解析和存储服务器返回省级数据
     * @param response 返回请求
     * @return 存储结果
     */
    public static boolean handleProvinceResponse(String response){
        if(!TextUtils.isEmpty(response)){
            try{
                JSONArray all = new JSONArray(response);
                for (int i = 0; i < all.length(); i++) {
                    JSONObject obj = all.getJSONObject(i);
                    Province province = new Province();
                    province.setCode(Integer.valueOf(obj.getString("id")));
                    province.setName(obj.getString("name"));
                    province.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析和存储服务器返回市级数据
     * @param response 同上
     * @param provinceId 所属省编号
     * @return 同上
     */
    public static boolean handleCityResponse(String response,int provinceId){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray all = new JSONArray(response);
                for (int i = 0; i < all.length(); i++) {
                    JSONObject obj = all.getJSONObject(i);
                    City city = new City();
                    city.setCode(Integer.valueOf(obj.getString("id")));
                    city.setName(obj.getString("name"));;
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析和存储服务器返回市级数据
     * Gson使用测试
     * @param response 同上
     * @param cityId 区所属市编号
     * @return 同上
     */
    public static boolean handleRegionResponse(String response,int cityId){
        if(!TextUtils.isEmpty(response)){
            Gson gson = new Gson();
            try {
                List<GSONRegion> all = gson.fromJson(response,new TypeToken<List<GSONRegion> >(){}.getType());
                for (GSONRegion GRegion: all) {
                    Region region = new Region();
                    region.setCode(GRegion.getId());
                    region.setName(GRegion.getName());
                    region.setWeatherId(GRegion.getWeatherId());
                    region.setCityId(cityId);
                    region.save();
                }
                return true;
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 处理从服务器接受天气数据
     * @param response
     * @return 正常处理返回GSON类Weather,异常处理返回null
     */
    public static Weather handleWeatherResponse(String response){
        try {
            JSONObject obj = new JSONObject(response);
            JSONArray arr = obj.getJSONArray("HeWeather");
            String weatherContent = arr.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent,Weather.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String handleAnimePic(String response){
        try {
            JSONObject obj = new JSONObject(response);
            return obj.getString("imgurl");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "https://mfiles.alphacoders.com/894/thumb-894710.png";
    }
}

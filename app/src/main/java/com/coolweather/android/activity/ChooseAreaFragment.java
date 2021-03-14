package com.coolweather.android.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.coolweather.android.R;
import com.coolweather.android.db.City;
import com.coolweather.android.db.Province;
import com.coolweather.android.db.Region;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Utility;

import org.jetbrains.annotations.NotNull;
import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChooseAreaFragment extends Fragment {
    private static final String TAG = "ChooseAreaFragment";

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_REGION = 2;
    private int nowLevel;

    private TextView title;
    private Button backButton,refreshButton;
    private ProgressBar progressBar;
    private ListView listView;

    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();
    private List<Province> provinceList;
    private List<City> cityList;
    private List<Region> regionList;

    private Province selectedProvince;
    private City selectedCity;
    private Region selectedRegion;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: view create");
        View view = inflater.inflate(R.layout.choose_area,container,false);
        title = (TextView) view.findViewById(R.id.title);
        refreshButton = (Button) view.findViewById(R.id.refresh_button);
        backButton = (Button) view.findViewById(R.id.back_button);
        listView = (ListView) view.findViewById(R.id.list_view);
        adapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        nowLevel = LEVEL_PROVINCE;
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated: activity create ");
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: now level is "+nowLevel);
                switch (nowLevel){
                    case LEVEL_PROVINCE:
                        selectedProvince = provinceList.get(position);
                        Log.d(TAG, "onItemClick: select province name:"+selectedProvince.getName());
                        queryCity();
                        break;
                    case LEVEL_CITY:
                        selectedCity = cityList.get(position);
                        Log.d(TAG, "onItemClick: select city name:"+selectedCity.getName());
                        queryRegion();
                        break;
                    case LEVEL_REGION:
                        selectedRegion = regionList.get(position);
                        Log.d(TAG, "onItemClick: select region name:"+selectedRegion.getName());
                        // todo: turn to weather info show activity
                        break;
                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onItemClick: now level is "+nowLevel);
                switch (nowLevel){
                    case LEVEL_CITY:
                        nowLevel = LEVEL_PROVINCE;
                        selectedCity = null;
                        queryProvince();
                        break;
                    case LEVEL_REGION:
                        nowLevel = LEVEL_CITY;
                        selectedRegion = null;
                        queryCity();
                        break;
                }
            }
        });
        queryProvince();
    }

    private void queryProvince(){
        title.setText("中国");
        backButton.setVisibility(View.GONE);
        provinceList = LitePal.findAll(Province.class);
        if(provinceList.size()!=0){
            dataList.clear();
            for (Province province: provinceList) {
                dataList.add(province.getName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            nowLevel = LEVEL_PROVINCE;
        }else{
            queryNet(getResources().getString(R.string.base_url),0);
        }
    }

    private void queryCity(){
        backButton.setVisibility(View.VISIBLE);
        title.setText(selectedProvince.getName());
        cityList = LitePal.where("provinceId = ?",String.valueOf(selectedProvince.getCode())).find(City.class);
        if(cityList.size()!=0){
            dataList.clear();
            for (City city: cityList) {
                dataList.add(city.getName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            nowLevel = LEVEL_CITY;
        }else{
            queryNet(getResources().getString(R.string.base_url)+selectedProvince.getCode(),1);
        }
    }

    private void queryRegion(){
        title.setText(selectedCity.getName());
        regionList = LitePal.where("cityId = ?", String.valueOf(selectedCity.getCode())).find(Region.class);
        if(regionList.size()!=0){
            dataList.clear();
            for (Region region : regionList) {
                dataList.add(region.getName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            nowLevel = LEVEL_REGION;
        }else{
            queryNet(getResources().getString(R.string.base_url)+selectedProvince.getCode()+'/'+selectedCity.getCode(),2);
        }
    }

    private void queryNet(String adress, final int type){
        Log.d(TAG, "queryNet: adress "+adress);
        showProgress();
        HttpUtil.requestGet(adress, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgress();
                        Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseText = response.body().string();
                Log.d(TAG, "response text: "+responseText);
                boolean res = false;
                switch (type){
                    case 0: // province
                        res = Utility.handleProvinceResponse(responseText);
                        break;
                    case 1: //city
                        res = Utility.handleCityResponse(responseText,selectedProvince.getCode());
                        break;
                    case 2:
                        res = Utility.handleRegionResponse(responseText,selectedCity.getCode());
                        break;
                }
                if(res){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgress();
                            switch (type){
                                case 0: //province
                                    queryProvince();
                                    break;
                                case 1: // city
                                    queryCity();
                                    break;
                                case 2:
                                    queryRegion();
                                    break;
                            }
                        }
                    });
                }
            }
        });
    }

    private void showProgress(){
        if(progressBar==null){
            progressBar = new ProgressBar(getActivity());
            progressBar.setIndeterminate(true);
            // todo: add text info
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void closeProgress(){
        if(progressBar!=null){
            progressBar.setVisibility(View.GONE);
        }
    }
}

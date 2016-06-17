package com.yennefer.estatefinder.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.yennefer.estatefinder.model.Advert;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Yennefer on 07.06.2016.
 * Класс-одиночка, инкапсулирующий работу с БД.
 */
public class DBManager {

    static private DBManager instance;
    private DBHelper helper;

    static public void init(Context context) {
        if (null == instance) {
            instance = new DBManager(context);
        }
    }

    static public DBManager getInstance() {
        return instance;
    }

    private DBManager(Context context) {
        this.helper = new DBHelper(context);
        try {
            this.helper.createDataBase();
            this.helper.openDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Cursor getSearches() {
        return helper.getSearches();
    }

    public Cursor getResults(long searchID) {
        return helper.getResults(searchID);
    }

    public Cursor getAdvert(long advertID) {
        return helper.getAdvert(advertID);
    }

    public Cursor getAdParameters(long advertID) {
        return helper.getAdParameters(advertID);
    }

    public Cursor getCategories() {
        return helper.getCategories();
    }

    public Cursor getRegions() {
        return helper.getRegions();
    }

    public Cursor getAdTypes() {
        return helper.getAdTypes();
    }

    public Cursor getCitiesByRegion(long id) {
        return helper.getCitiesByRegion(id);
    }

    public Cursor getLease() {
        return helper.getParametersByID(Constants.LEASE_ID);
    }

    public Cursor getRoomCount() {
        return helper.getParametersByID(Constants.ROOM_COUNT_ID);
    }

    public Cursor getHomeType() {
        return helper.getParametersByID(Constants.HOME_TYPE_ID);
    }

    public Cursor getObjectType() {
        return helper.getParametersByID(Constants.OBJECT_TYPE_ID);
    }

    public Cursor getLocation() {
        return helper.getParametersByID(Constants.LOCATION_ID);
    }

    public long addSearch(ContentValues values) {
        return helper.addSearch(values);
    }

    public long addSearchParameter(ContentValues values) {
        return helper.addSearchParameter(values);
    }

    public void deleteSearchByID(long id) {
        helper.deleteSearchByID(id);
    }

    public int getSearchToDoID() {
        int searchID = 0;
        Cursor cursor = helper.getSearchToDoID();
        if (cursor.moveToFirst()) {
            searchID = cursor.getInt(cursor.getColumnIndex(Constants.ID_FIELD));
        }
        cursor.close();
        return searchID;
    }

    public int getSearchCategory(String searchID) {
        int searchCategory = 0;
        Cursor cursor = helper.getSearchCategory(searchID);
        if (cursor.moveToFirst()) {
            searchCategory = cursor.getInt(cursor.getColumnIndex(Constants.API_ID_FIELD));
        }
        cursor.close();
        return searchCategory;
    }

    public int getSearchRegion(String searchID) {
        int searchRegion = 0;
        Cursor cursor = helper.getSearchRegion(searchID);
        if (cursor.moveToFirst()) {
            searchRegion = cursor.getInt(cursor.getColumnIndex(Constants.API_ID_FIELD));
        }
        cursor.close();
        return searchRegion;
    }

    public int getSearchCity(String searchID) {
        int searchCity = 0;
        Cursor cursor = helper.getSearchCity(searchID);
        if (cursor.moveToFirst()) {
            searchCity = cursor.getInt(cursor.getColumnIndex(Constants.CITY_ID_FIELD));
        }
        cursor.close();
        return searchCity;
    }

    public String getCityByID(int cityID) {
        String cityName = "";
        Cursor cursor = helper.getCityByID(cityID);
        if (cursor.moveToFirst()) {
            cityName = cursor.getString(cursor.getColumnIndex(Constants.NAME_FIELD));
        }
        cursor.close();
        return cityName;
    }

    public int getSearchWithPhoto(String searchID) {
        int withPhoto = 0;
        Cursor cursor = helper.getSearchWithPhoto(searchID);
        if (cursor.moveToFirst()) {
            withPhoto = cursor.getInt(cursor.getColumnIndex(Constants.WITH_PHOTO_FIELD));
        }
        cursor.close();
        return withPhoto;
    }

    public String getSearchStartDate(String searchID) {
        String startDate = "";
        Cursor cursor = helper.getSearchStartDate(searchID);
        if (cursor.moveToFirst()) {
            startDate = cursor.getString(cursor.getColumnIndex(Constants.START_DATE_FIELD));
        }
        cursor.close();
        return startDate;
    }

    public String getSearchEndDate(String searchID) {
        String endDate = "";
        Cursor cursor = helper.getSearchEndDate(searchID);
        if (cursor.moveToFirst()) {
            endDate = cursor.getString(cursor.getColumnIndex(Constants.END_DATE_FIELD));
        }
        cursor.close();
        return endDate;
    }

    public float getSearchMinPrice(String searchID) {
        float minPrice = 0;
        Cursor cursor = helper.getSearchMinPrice(searchID);
        if (cursor.moveToFirst()) {
            minPrice = cursor.getFloat(cursor.getColumnIndex(Constants.PRICE_FROM_FIELD));
        }
        cursor.close();
        return minPrice;
    }

    public float getSearchMaxPrice(String searchID) {
        float maxPrice = 0;
        Cursor cursor = helper.getSearchMaxPrice(searchID);
        if (cursor.moveToFirst()) {
            maxPrice = cursor.getFloat(cursor.getColumnIndex(Constants.PRICE_TO_FIELD));
        }
        cursor.close();
        return maxPrice;
    }

    public Map<String,String> getSearchParams(String searchID) {
        Cursor cursor = helper.getSearchParams(searchID);
        Map<String, String> searchParams = new HashMap<String, String>();
        if (cursor.moveToFirst()) {
            for (int i = 0; i < cursor.getCount(); i++) {
                searchParams.put( cursor.getString(cursor.getColumnIndex(Constants.PARAMETER_NAME_FIELD)),
                        cursor.getString(cursor.getColumnIndex(Constants.PARAMETER_VALUE_FIELD)) );
                cursor.moveToNext();
            }
        }
        cursor.close();
        return searchParams;
    }

    public void saveAds(List<Advert> adverts, int searchID) {
        for (Advert advert : adverts) {
            helper.saveAd(advert, searchID);
        }
    }

    public void updateSearchDate(int searchID) {
        helper.updateSearchDate(searchID);
    }

}

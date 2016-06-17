package com.yennefer.estatefinder.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.yennefer.estatefinder.model.Advert;
import com.yennefer.estatefinder.model.AdvertParam;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Yennefer on 07.06.2016.
 * Класс предоставляет методы создания и обновления базы данных.
 */
public class DBHelper extends SQLiteOpenHelper {

    private SQLiteDatabase myDataBase;
    private final Context mContext;

    public DBHelper(Context context) {
        super(context, Constants.DB_NAME, null, 1);
        this.mContext = context;
    }

    // Создает пустую базу данных и перезаписывает ее моей базой
    public void createDataBase() throws IOException{
        boolean dbExist = checkDataBase();

        if (!dbExist) {

            // Создаем пустую базу
            this.getReadableDatabase();

            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error(Constants.ERROR_COPYING_DATABASE);
            }
        }
    }

    // Проверка на существование БД
    private boolean checkDataBase(){
        SQLiteDatabase checkDB = null;

        try{
            String myPath = Constants.DB_PATH + Constants.DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        }catch(SQLiteException e){
            // База еще не существует
            e.printStackTrace();
        }
        if(checkDB != null){
            checkDB.close();
        }
        return checkDB != null;
    }

    // Копирование базы из папки assets
    private void copyDataBase() throws IOException{

        // Открываем локальную БД как входящий поток
        InputStream myInput = mContext.getAssets().open(Constants.DB_NAME);

        // Путь к созданной БД
        String outFileName = Constants.DB_PATH + Constants.DB_NAME;

        // Открываем пустую базу данных как исходящий поток
        OutputStream myOutput = new FileOutputStream(outFileName);

        // Перемещаем байты из входящего файла в исходящий
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        // Закрываем потоки
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    public void openDataBase() throws SQLException {

        // Открываем БД
        String myPath = Constants.DB_PATH + Constants.DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    @Override
    public synchronized void close() {
        if(myDataBase != null)
            myDataBase.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Действия при создании БД
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Действия при обновлении БД
    }

    public Cursor getSearches() {
        return myDataBase.rawQuery(Constants.GET_SEARCHES_QUERY, null);
    }

    public Cursor getResults(long searchID) {
        return myDataBase.rawQuery(Constants.GET_RESULTS_QUERY, new String[] { String.valueOf(searchID) });
    }

    public Cursor getAdvert(long advertID) {
        return myDataBase.rawQuery(Constants.GET_ADVERT_QUERY, new String[] {String.valueOf(advertID)});
    }

    public Cursor getAdParameters(long advertID) {
        return myDataBase.rawQuery(Constants.GET_ADVERT_PARAMETERS_QUERY, new String[] {String.valueOf(advertID)});
    }

    public Cursor getCategories() {
        return myDataBase.rawQuery(Constants.GET_CATEGORIES_QUERY, null);
    }

    public Cursor getAdTypes() {
        return myDataBase.rawQuery(Constants.GET_AD_TYPES_QUERY, null);
    }

    public Cursor getRegions() {
        return myDataBase.rawQuery(Constants.GET_REGIONS_QUERY, null);
    }

    public Cursor getCitiesByRegion(long id) {
        return myDataBase.rawQuery(Constants.GET_CITIES_BY_REGION, new String[] { String.valueOf(id) });
    }

    public Cursor getCityByID(int cityID) {
        return myDataBase.rawQuery(Constants.GET_CITY_BY_ID_QUERY, new String[] { String.valueOf(cityID) });
    }

    public Cursor getSearchWithPhoto(String searchID) {
        return myDataBase.rawQuery(Constants.GET_WITH_PHOTO_BY_SEARCH_ID_QUERY, new String[] { String.valueOf(searchID) });
    }

    public Cursor getParametersByID(int id) {
        return myDataBase.rawQuery(Constants.GET_PARAMETERS_QUERY, new String[] { String.valueOf(id) });
    }

    public long addSearch(ContentValues values) {
        return myDataBase.insert(Constants.SEARCHES_TABLE, null, values);
    }

    public long addSearchParameter(ContentValues values) {
        return myDataBase.insert(Constants.SEARCH_PARAMETERS_TABLE, null, values);
    }

    public void deleteSearchByID(long id) {
        String strID = String.valueOf(id);
        myDataBase.beginTransaction();
        myDataBase.execSQL(Constants.DELETE_FROM_ADVERT_PARAMETERS_BY_SEARCH_ID_QUERY, new String[] {strID});
        myDataBase.execSQL(Constants.DELETE_FROM_ADVERTS_BY_SEARCH_ID_QUERY, new String[] {strID});
        myDataBase.delete(Constants.SEARCHES_ADVERTS_TABLE, Constants.SEARCH_ID_FIELD + " = " + strID, null);
        myDataBase.delete(Constants.SEARCH_PARAMETERS_TABLE, Constants.SEARCH_ID_FIELD + " = " + strID, null);
        myDataBase.delete(Constants.SEARCHES_TABLE, Constants.ID_FIELD + " = " + strID, null);
        myDataBase.setTransactionSuccessful();
        myDataBase.endTransaction();
    }

    public Cursor getSearchToDoID() {
        return myDataBase.rawQuery(Constants.GET_SEARCH_TO_DO_QUERY, null);
    }

    public Cursor getSearchCategory(String searchID) {
        return myDataBase.rawQuery(Constants.GET_CATEGORY_ID_BY_SEARCH_ID_QUERY, new String[] { searchID });
    }

    public Cursor getSearchRegion(String searchID) {
        return myDataBase.rawQuery(Constants.GET_REGION_ID_BY_SEARCH_ID_QUERY, new String[] { searchID });
    }

    public Cursor getSearchCity(String searchID) {
        return myDataBase.rawQuery(Constants.GET_CITY_ID_BY_SEARCH_ID_QUERY, new String[] { searchID });
    }

    public Cursor getSearchStartDate(String searchID) {
        return myDataBase.rawQuery(Constants.GET_DATE_MINUS_DAY_BY_SEARCH_ID_QUERY, new String[] { searchID });
    }

    public Cursor getSearchEndDate(String searchID) {
        return myDataBase.rawQuery(Constants.GET_DATE_BY_SEARCH_ID_QUERY, new String[] { searchID });
    }

    public Cursor getSearchMinPrice(String searchID) {
        return myDataBase.rawQuery(Constants.GET_PRICE_FROM_BY_SEARCH_ID_QUERY, new String[] { searchID });
    }

    public Cursor getSearchMaxPrice(String searchID) {
        return myDataBase.rawQuery(Constants.GET_PRICE_TO_BY_SEARCH_ID_QUERY, new String[] { searchID });
    }

    public Cursor getSearchParams(String searchID) {
        return myDataBase.rawQuery(Constants.GET_PARAMS_BY_SEARCH_ID_QUERY, new String[] { searchID });
    }

    public void saveAd(Advert advert, int searchID) {
        // Не сохраняем объявление, если оно уже есть в БД
        Cursor cursor = myDataBase.rawQuery(Constants.GET_ADVERT_BY_AVITO_ID_AND_SEARCH_ID_QUERY,
                new String[] { String.valueOf(searchID), advert.getId(), advert.getAvito_id() });
        if (cursor.moveToFirst()) {
            cursor.close();
            return;
        }

        // Сохранем объявление
        myDataBase.beginTransaction();
        ContentValues cv = new ContentValues();
        cv.put(Constants.API_ID_FIELD, advert.getId());
        cv.put(Constants.AVITO_ID_FIELD, advert.getAvito_id());
        cv.put(Constants.URL_FIELD, advert.getUrl());
        cv.put(Constants.TITLE_FIELD, advert.getTitle());
        cv.put(Constants.PRICE_FIELD, advert.getPrice());
        cv.put(Constants.DATE_FIELD, advert.getTime());
        cv.put(Constants.CITY_FIELD, advert.getCity());
        cv.put(Constants.DISTRICT_FIELD, advert.getDistrict());
        cv.put(Constants.ADDRESS_FIELD, advert.getAddress());
        cv.put(Constants.METRO_FIELD, advert.getMetro());
        cv.put(Constants.OPERATOR_FIELD, advert.getOperator());
        cv.put(Constants.PHONE_FIELD, advert.getPhone());
        cv.put(Constants.NAME_FIELD, advert.getName());
        cv.put(Constants.IMAGES_FIELD, advert.getImages());
        cv.put(Constants.DESCRIPTION_FIELD, advert.getDescription());
        cv.put(Constants.LAT_FIELD, advert.getCoords().getLat());
        cv.put(Constants.LNG_FIELD, advert.getCoords().getLng());
        long advertID = myDataBase.insert(Constants.ADVERTS_TABLE, null, cv);

        // Сохраняем параметры объявления
        for (AdvertParam adParam : advert.getParams()) {
            long paramNameID;
            cursor.close();
            cursor = myDataBase.rawQuery(Constants.GET_PARAMETER_ID_BY_NAME_QUERY,
                    new String[]{ adParam.getName() });
            if (cursor.moveToFirst()) {
                paramNameID = cursor.getLong(cursor.getColumnIndex(Constants.ID_FIELD));
            } else {
                cv.clear();
                cv.put(Constants.NAME_FIELD, adParam.getName());
                paramNameID = myDataBase.insert(Constants.PARAMETER_NAMES_TABLE, null, cv);
            }
            long paramValueID;
            cursor.close();
            cursor = myDataBase.rawQuery(Constants.GET_PARAMETER_VALUE_ID_BY_PARAMETER_NAME_QUERY,
                    new String[]{ String.valueOf(paramNameID), adParam.getValue() });
            if (cursor.moveToFirst()) {
                paramValueID = cursor.getLong(cursor.getColumnIndex(Constants.ID_FIELD));
            } else {
                cv.clear();
                cv.put(Constants.NAME_FIELD, adParam.getValue());
                cv.put(Constants.PARAMETER_NAME_ID_FIELD, paramNameID);
                paramValueID = myDataBase.insert(Constants.PARAMETER_VALUES_TABLE, null, cv);
            }
            cv.clear();
            cv.put(Constants.PARAMETER_VALUE_ID_FIELD, paramValueID);
            cv.put(Constants.ADVERT_ID_FIELD, advertID);
            myDataBase.insert(Constants.ADVERT_PARAMETERS_TABLE, null, cv);
        }

        // Сохраняем ссылку на идентификатор поиска
        cv.clear();
        cv.put(Constants.SEARCH_ID_FIELD, searchID);
        cv.put(Constants.ADVERT_ID_FIELD, advertID);
        myDataBase.insert(Constants.SEARCHES_ADVERTS_TABLE, null, cv);

        myDataBase.setTransactionSuccessful();
        myDataBase.endTransaction();
    }

    public void updateSearchDate(int searchID) {
        myDataBase.execSQL(Constants.UPDATE_SEARCH_DATE_QUERY, new String[] { String.valueOf(searchID) });
    }
}

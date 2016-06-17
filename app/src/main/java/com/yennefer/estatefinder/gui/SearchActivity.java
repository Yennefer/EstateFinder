package com.yennefer.estatefinder.gui;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.yennefer.estatefinder.R;
import com.yennefer.estatefinder.database.Constants;
import com.yennefer.estatefinder.database.DBManager;

/**
 * Created by Yennefer on 29.05.2016.
 * Экран поиска. Отображает параметры поиска.
 */
public class SearchActivity extends AppCompatActivity {

    // Идентификаторы выбранных параметров
    private long categoryID = 0,
            adTypeID = 0,
            regionID = 0,
            cityID = 0,
            leaseID = 0,
            roomCountID = 0,
            homeTypeID = 0,
            objectTypeID = 0,
            locationID = 0;

    // Элементы экрана
    View contentCity,
            contentLease,
            contentRoomCount,
            contentHomeType,
            contentTypeAndLocation;
    Spinner spCategory,
            spAdType,
            spRegions,
            spLease,
            spRoomCount,
            spHomeType,
            spObjectType,
            spLocation,
            spCity;

    // Поле с названием поиска
    EditText etSearchName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Настройка панели действий
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Инициализация
        DBManager.init(getApplicationContext());

        // Заполняем визуальные компоненты данными
        fillViews();
    }

    private void fillViews() {

        // Ищем элементы экрана
        contentLease = findViewById(R.id.contentLease);
        contentRoomCount = findViewById(R.id.contentRoomCount);
        contentHomeType = findViewById(R.id.contentHomeType);
        contentTypeAndLocation = findViewById(R.id.contentTypeAndLocation);

        // Параметры для адаптера
        String[] from = new String[] { "name" };
        int[] to = new int[] { R.id.tvSpinnerItem };

        // Задаем обработку выбора параметра из списка
        AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // Обновляем внешний вид экрана
                updateLayout(parent, id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };

        // Заполняем список категорий
        Cursor categories = DBManager.getInstance().getCategories();
        spCategory = (Spinner) findViewById(R.id.spCategory);
        SimpleCursorAdapter categoryAdapter = new SimpleCursorAdapter(getApplicationContext(),
                R.layout.item_spinner, categories, from, to, 0);
        if (spCategory != null) {
            spCategory.setAdapter(categoryAdapter);
            spCategory.setOnItemSelectedListener(itemSelectedListener);
        }

        // Заполняем список типов объявления
        Cursor adTypes = DBManager.getInstance().getAdTypes();
        spAdType = (Spinner) findViewById(R.id.spAdType);
        SimpleCursorAdapter adTypeAdapter = new SimpleCursorAdapter(getApplicationContext(),
                R.layout.item_spinner, adTypes, from, to, 0);
        if (spAdType != null) {
            spAdType.setAdapter(adTypeAdapter);
            spAdType.setOnItemSelectedListener(itemSelectedListener);
        }

        // Заполняем список регионов
        Cursor regions = DBManager.getInstance().getRegions();
        spRegions = (Spinner) findViewById(R.id.spRegions);
        SimpleCursorAdapter regionsAdapter = new SimpleCursorAdapter(getApplicationContext(),
                R.layout.item_spinner, regions, from, to, 0);
        if (spRegions != null) {
            spRegions.setAdapter(regionsAdapter);
            spRegions.setOnItemSelectedListener(itemSelectedListener);
        }

        // Заполняем список сроков аренды
        Cursor lease = DBManager.getInstance().getLease();
        spLease = (Spinner) findViewById(R.id.spLease);
        SimpleCursorAdapter leaseAdapter = new SimpleCursorAdapter(getApplicationContext(),
                R.layout.item_spinner, lease, from, to, 0);
        if (spLease != null) {
            spLease.setAdapter(leaseAdapter);
            spLease.setOnItemSelectedListener(itemSelectedListener);
        }

        // Заполняем список количества комнат
        Cursor roomCount = DBManager.getInstance().getRoomCount();
        spRoomCount = (Spinner) findViewById(R.id.spRoomCount);
        SimpleCursorAdapter roomCountAdapter = new SimpleCursorAdapter(getApplicationContext(),
                R.layout.item_spinner, roomCount, from, to, 0);
        if (spRoomCount != null) {
            spRoomCount.setAdapter(roomCountAdapter);
            spRoomCount.setOnItemSelectedListener(itemSelectedListener);
        }

        // Заполняем список типов домов
        Cursor homeType = DBManager.getInstance().getHomeType();
        spHomeType = (Spinner) findViewById(R.id.spHomeType);
        SimpleCursorAdapter homeTypeAdapter = new SimpleCursorAdapter(getApplicationContext(),
                R.layout.item_spinner, homeType, from, to, 0);
        if (spHomeType != null) {
            spHomeType.setAdapter(homeTypeAdapter);
            spHomeType.setOnItemSelectedListener(itemSelectedListener);
        }

        // Заполняем список типов объектов
        Cursor objectType = DBManager.getInstance().getObjectType();
        spObjectType = (Spinner) findViewById(R.id.spObjectType);
        SimpleCursorAdapter objectTypeAdapter = new SimpleCursorAdapter(getApplicationContext(),
                R.layout.item_spinner, objectType, from, to, 0);
        if (spObjectType != null) {
            spObjectType.setAdapter(objectTypeAdapter);
            spObjectType.setOnItemSelectedListener(itemSelectedListener);
        }

        // Заполняем список местоположений
        Cursor location = DBManager.getInstance().getLocation();
        spLocation = (Spinner) findViewById(R.id.spLocation);
        SimpleCursorAdapter locationAdapter = new SimpleCursorAdapter(getApplicationContext(),
                R.layout.item_spinner, location, from, to, 0);
        if (spLocation != null) {
            spLocation.setAdapter(locationAdapter);
            spLocation.setOnItemSelectedListener(itemSelectedListener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_about) {
            startActivity( new Intent(getApplicationContext(), AboutActivity.class) );
            return true;
        }
        else if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void btnSaveClick(View view) {

        // Ищем поле с названием поиска
        etSearchName = (EditText) findViewById(R.id.etSearchName);
        if (etSearchName != null) {

            // Если название поиска не задано, выводим предупреждение
            if (etSearchName.getText().toString().isEmpty()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(getResources().getString(R.string.text_set_search_name))
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                })
                        .show();
            // Иначе, сохраняем созданный поисковый запрос
            } else {
                saveSearchData();
                finish();
            }
        }
    }

    private void updateLayout(AdapterView<?> parent, long id) {

        // Идентификаторы категорий и типов объявлений
        final int CATEGORY_APARTMENT = 1;
        final int CATEGORY_HOUSE = 3;
        final int AD_TYPE_SELL = 1;
        final int AD_TYPE_OFFER_RENT= 2;
        final int AD_TYPE_RENT = 4;

        // Сохраняем идентификатор измененного параметра
        switch (parent.getId()) {
            case R.id.spCategory: {
                categoryID = id;
                break;
            }
            case R.id.spAdType: {
                adTypeID = id;
                break;
            }
            case R.id.spRegions: {
                regionID = id;
                updateCities(id);
                return;
            }
            case R.id.spLease: {
                leaseID = id;
                return;
            }
            case R.id.spRoomCount: {
                roomCountID = id;
                return;
            }
            case R.id.spHomeType: {
                homeTypeID = id;
                return;
            }
            case R.id.spObjectType: {
                objectTypeID = id;
                return;
            }
            case R.id.spLocation: {
                locationID = id;
                return;
            }
            default:
                return;
        }

        // Обновляем внешний вид экрана в зависимости от выбранной категории и типа объявления
        if (contentLease != null) {
            if (adTypeID == AD_TYPE_OFFER_RENT || adTypeID == AD_TYPE_RENT) {
                contentLease.setVisibility(View.VISIBLE);
            }
            else {
                contentLease.setVisibility(View.GONE);
            }
        }

        if (contentRoomCount != null) {
            if (categoryID == CATEGORY_APARTMENT) {
                contentRoomCount.setVisibility(View.VISIBLE);
            }
            else {
                contentRoomCount.setVisibility(View.GONE);
            }
        }

        if (contentHomeType != null) {
            if (categoryID == CATEGORY_APARTMENT && adTypeID == AD_TYPE_SELL) {
                contentHomeType.setVisibility(View.VISIBLE);
            }
            else {
                contentHomeType.setVisibility(View.GONE);
            }
        }

        if (contentTypeAndLocation != null) {
            if (categoryID == CATEGORY_HOUSE) {
                contentTypeAndLocation.setVisibility(View.VISIBLE);
            }
            else {
                contentTypeAndLocation.setVisibility(View.GONE);
            }
        }
    }

    private void updateCities(long id) {

        // Параметры для адаптера
        String[] from = new String[] { "name" };
        int[] to = new int[] { R.id.tvSpinnerItem };

        // Создаем курсор для получения списка городов
        Cursor cities = DBManager.getInstance().getCitiesByRegion(id);

        // Находим список ородов
        spCity = (Spinner) findViewById(R.id.spCity);

        // Создаем адаптер
        SimpleCursorAdapter cityAdapter = new SimpleCursorAdapter(getApplicationContext(),
                R.layout.item_spinner, cities, from, to, 0);

        // Настраиваем список
        if (spCity != null) {
            spCity.setAdapter(cityAdapter);
            spCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    // Сохраняем идентификатор выбранного города
                    cityID = id;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

        // Настраиваем видимость списка городов в зависимости от его содержания
        contentCity = findViewById(R.id.contentCity);
        if (contentCity != null) {
            if (cities.getCount() > 0) {
                contentCity.setVisibility(View.VISIBLE);
            } else {
                contentCity.setVisibility(View.GONE);
            }
        }
    }

    private void saveSearchData() {

        // Находим элементы экрана с заполненными параметрами
        EditText etPriceFrom = (EditText) findViewById(R.id.etPriceFrom);
        EditText etPriceTo = (EditText) findViewById(R.id.etPriceTo);
        CheckBox cbOnlyWithPhoto = (CheckBox) findViewById(R.id.cbOnlyWithPhoto);

        // Сохраняем поисковый запрос и его параметры
        if (etSearchName != null && etPriceFrom != null && etPriceTo != null && cbOnlyWithPhoto != null) {
            ContentValues cv = new ContentValues();
            cv.put(Constants.NAME_FIELD, etSearchName.getText().toString());
            cv.put(Constants.CATEGORY_ID_FIELD, categoryID);
            cv.put(Constants.REGION_ID_FIELD, regionID);
            if (contentCity.getVisibility() == View.VISIBLE) {
                cv.put(Constants.CITY_ID_FIELD, cityID);
            }
            if (!etPriceFrom.getText().toString().isEmpty()) {
                cv.put(Constants.PRICE_FROM_FIELD, etPriceFrom.getText().toString());
            }
            if (!etPriceTo.getText().toString().isEmpty()) {
                cv.put(Constants.PRICE_TO_FIELD, etPriceTo.getText().toString());
            }
            if ( cbOnlyWithPhoto.isChecked() ) {
                cv.put(Constants.WITH_PHOTO_FIELD, 1);
            }
            long searchID = DBManager.getInstance().addSearch(cv);

            if (searchID > 0) {
                cv.clear();
                cv.put(Constants.PARAMETER_VALUE_ID_FIELD, adTypeID);
                cv.put(Constants.SEARCH_ID_FIELD, searchID);
                DBManager.getInstance().addSearchParameter(cv);
                if (contentLease.getVisibility() == View.VISIBLE) {
                    cv.clear();
                    cv.put(Constants.PARAMETER_VALUE_ID_FIELD, leaseID);
                    cv.put(Constants.SEARCH_ID_FIELD, searchID);
                    DBManager.getInstance().addSearchParameter(cv);
                }
                if (contentRoomCount.getVisibility() == View.VISIBLE) {
                    cv.clear();
                    cv.put(Constants.PARAMETER_VALUE_ID_FIELD, roomCountID);
                    cv.put(Constants.SEARCH_ID_FIELD, searchID);
                    DBManager.getInstance().addSearchParameter(cv);
                }
                if (contentHomeType.getVisibility() == View.VISIBLE) {
                    cv.clear();
                    cv.put(Constants.PARAMETER_VALUE_ID_FIELD, homeTypeID);
                    cv.put(Constants.SEARCH_ID_FIELD, searchID);
                    DBManager.getInstance().addSearchParameter(cv);
                }
                if (contentTypeAndLocation.getVisibility() == View.VISIBLE) {
                    cv.clear();
                    cv.put(Constants.PARAMETER_VALUE_ID_FIELD, objectTypeID);
                    cv.put(Constants.SEARCH_ID_FIELD, searchID);
                    DBManager.getInstance().addSearchParameter(cv);
                    cv.clear();
                    cv.put(Constants.PARAMETER_VALUE_ID_FIELD, locationID);
                    cv.put(Constants.SEARCH_ID_FIELD, searchID);
                    DBManager.getInstance().addSearchParameter(cv);
                }
            }
        }
    }

    public void spinnerOverlayClick(View v) {
        switch (v.getId()) {
            case R.id.spCategoryOverlay: {
                spCategory.performClick();
                break;
            }
            case R.id.spAdTypeOverlay: {
                spAdType.performClick();
                break;
            }
            case R.id.spRegionsOverlay: {
                spRegions.performClick();
                break;
            }
            case R.id.spLeaseOverlay: {
                spLease.performClick();
                break;
            }
            case R.id.spRoomCountOverlay: {
                spRoomCount.performClick();
                break;
            }
            case R.id.spHomeTypeOverlay: {
                spHomeType.performClick();
                break;
            }
            case R.id.spObjectTypeOverlay: {
                spObjectType.performClick();
                break;
            }
            case R.id.spLocationOverlay: {
                spLocation.performClick();
                break;
            }
            case R.id.spCityOverlay: {
                spCity.performClick();
                break;
            }
            default:
                break;
        }
    }
}

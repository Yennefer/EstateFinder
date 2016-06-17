package com.yennefer.estatefinder.gui;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.koushikdutta.ion.Ion;
import com.yennefer.estatefinder.R;
import com.yennefer.estatefinder.database.Constants;
import com.yennefer.estatefinder.database.DBManager;

/**
 * Created by Yennefer on 29.05.2016.
 * Экран объявления. Отображает информацию об объявлении.
 */
public class AdvertActivity extends AppCompatActivity {

    // Идентификатор объявления
    private long advertID;

    // Параметры объявления
    private String advertTitle,
            advertURL,
            advertAddress,
            advertLng,
            advertLat,
            advertPhone;

    // Начальная позиция при обработке касания
    float startX = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advert);

        // Настройка панели действий
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Инициализация
        advertID = getIntent().getLongExtra("advert_id", 0);
        DBManager.init(getApplicationContext());

        // Заполняем визуальные компоненты данными
        fillViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_advert, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_about) {
            startActivity( new Intent(getApplicationContext(), AboutActivity.class) );
            return true;
        } else if (id == R.id.action_share) {

            // Открываем окно выбора приложений для выполнения команды "поделиться"
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, advertTitle + " " + advertURL);
            sendIntent.setType("text/plain");
            startActivity( Intent.createChooser(sendIntent, null) );
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void fillViews() {

        // Находим компоненты экрана
        ViewFlipper vfPhoto = (ViewFlipper) findViewById(R.id.vfPhoto);
        TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
        TextView tvPrice = (TextView) findViewById(R.id.tvPrice);
        TextView tvAddress = (TextView) findViewById(R.id.tvAddress);
        TextView tvDescription = (TextView) findViewById(R.id.tvDescription);
        TextView tvPhone = (TextView) findViewById(R.id.tvPhoneNumber);
        ImageView ivLocation = (ImageView) findViewById(R.id.ivLocation);
        LinearLayout llLocation = (LinearLayout) findViewById(R.id.llLocation);
        LinearLayout llCall = (LinearLayout) findViewById(R.id.llCall);
        LinearLayout llParams = (LinearLayout) findViewById(R.id.llParams);

        // Получаем данные выбранного объявления
        Cursor cursor = DBManager.getInstance().getAdvert(advertID);

        if (cursor.moveToFirst()) {

            // Заполняем компоненты и переменные полученными данными
            String images = cursor.getString(cursor.getColumnIndex(Constants.IMAGES_FIELD));
            advertTitle = cursor.getString(cursor.getColumnIndex(Constants.TITLE_FIELD));
            advertURL = cursor.getString(cursor.getColumnIndex(Constants.URL_FIELD));
            advertLng = cursor.getString(cursor.getColumnIndex(Constants.LNG_FIELD));
            advertLat = cursor.getString(cursor.getColumnIndex(Constants.LAT_FIELD));
            advertPhone = cursor.getString(cursor.getColumnIndex(Constants.PHONE_FIELD));
            advertAddress = cursor.getString(cursor.getColumnIndex(Constants.ADDRESS_FIELD));
            advertLng = advertLng.replace(",", ".");
            advertLat = advertLat.replace(",", ".");

            if (tvTitle != null && tvPrice != null && tvDescription != null && tvPhone != null &&
                    tvAddress != null && ivLocation != null && llLocation != null && llCall != null &&
                    vfPhoto != null) {

                if ( images.equals("") ) {
                    vfPhoto.setVisibility(View.GONE);
                } else {
                    vfPhoto.setVisibility(View.VISIBLE);

                    // Назначаем обработчик касания
                    vfPhoto.setOnTouchListener(new View.OnTouchListener() {

                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            switch (event.getAction()) {

                                // При опускании пальца
                                case MotionEvent.ACTION_DOWN: {

                                    // Запоминаем начальную позицию пальца
                                    startX = event.getX();
                                    break;
                                }

                                // При поднятии пальца
                                case MotionEvent.ACTION_UP: {
                                    ViewFlipper flipper = (ViewFlipper) v;

                                    // Получаем конечную позицию пальца
                                    float finalX = event.getX();

                                    // Определяем напрваление свайпа и меняем картинку
                                    if (startX  > finalX) {
                                        flipper.setInAnimation(getApplicationContext(), R.anim.go_next_in);
                                        flipper.setOutAnimation(getApplicationContext(), R.anim.go_next_out);
                                        flipper.showNext();

                                    } else if (startX < finalX) {
                                        flipper.setInAnimation(getApplicationContext(), R.anim.go_prev_in);
                                        flipper.setOutAnimation(getApplicationContext(), R.anim.go_prev_out);
                                        flipper.showPrevious();
                                    }
                                    break;
                                }
                            }
                            return true;
                        }
                    });

                    String[] imagesURL = images.split(",");

                    // Заполняем контейнер картинками
                    for (String imageURL : imagesURL) {

                        ImageView image = new ImageView(getApplicationContext());
                        image.setScaleType(ImageView.ScaleType.FIT_CENTER);

                        ViewGroup.LayoutParams lParams = new ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT);

                        Ion.with(image)
                                .error(R.drawable.image_placeholder)
                                .load(imageURL);

                        vfPhoto.addView(image, lParams);
                    }
                }

                if (!advertTitle.equals("")) {
                    tvTitle.setText(advertTitle);
                    tvTitle.setVisibility(View.VISIBLE);
                } else {
                    tvTitle.setVisibility(View.GONE);
                }

                String price = cursor.getString(cursor.getColumnIndex(Constants.PRICE_FIELD));
                if (!price.equals("")) {
                    tvPrice.setText(price);
                    tvPrice.setVisibility(View.VISIBLE);
                } else {
                    tvPrice.setVisibility(View.GONE);
                }


                String address = cursor.getString(cursor.getColumnIndex(Constants.CITY_FIELD)) + ", " + advertAddress;
                if (!address.equals("")) {
                    tvAddress.setText(address);
                    llLocation.setVisibility(View.VISIBLE);
                } else {
                    llLocation.setVisibility(View.GONE);
                }

                String description = cursor.getString(cursor.getColumnIndex(Constants.DESCRIPTION_FIELD));
                if (!description.equals("")) {
                    tvDescription.setText(description);
                    tvDescription.setVisibility(View.VISIBLE);
                } else {
                    tvDescription.setVisibility(View.GONE);
                }

                if (!advertPhone.equals("")) {
                    String adPhoneName = cursor.getString(cursor.getColumnIndex(Constants.NAME_FIELD));
                    if (!adPhoneName.equals("")) {
                        tvPhone.setText(String.format(getResources().getString(R.string.text_phone),
                                advertPhone, adPhoneName));
                    } else {
                        tvPhone.setText(advertPhone);
                    }
                    llCall.setVisibility(View.VISIBLE);
                } else {
                    llCall.setVisibility(View.GONE);
                }

                if (!advertLat.equals("") && !advertLng.equals("")) {
                    ivLocation.setVisibility(View.VISIBLE);
                    llLocation.setClickable(true);
                } else {
                    ivLocation.setVisibility(View.GONE);
                    llLocation.setClickable(false);
                }
            }

            if (llParams != null) {

                // Получаем параметры объявления
                cursor.close();
                cursor = DBManager.getInstance().getAdParameters(advertID);

                if (cursor.moveToFirst()) {

                    // Заполняем компоненты параметрами объявления
                    for (int i = 0; i < cursor.getCount(); i++) {

                        // Создаем и заполняем компонент, добавляем его на экран
                        String paramText = cursor.getString(cursor.getColumnIndex(Constants.PARAMETER_NAME_FIELD)) + ": " +
                                cursor.getString(cursor.getColumnIndex(Constants.PARAMETER_VALUE_FIELD));

                        TextView tvParam = new TextView(getApplicationContext());
                        tvParam.setText(paramText);
                        if (Build.VERSION.SDK_INT < 23) {
                            tvParam.setTextAppearance(getApplicationContext(), android.R.style.TextAppearance_Small);
                            tvParam.setTextColor(getResources().getColor(R.color.colorSecondaryText));
                        } else {
                            tvParam.setTextAppearance(android.R.style.TextAppearance_Small);
                            tvParam.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorSecondaryText));
                        }

                        LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);
                        lParams.setMargins(0, 8, 0, 0);

                        llParams.addView(tvParam, lParams);

                        cursor.moveToNext();
                    }

                    llParams.setVisibility(View.VISIBLE);
                } else {
                    llParams.setVisibility(View.GONE);
                }
            }
        }
    }

    public void openMapClick(View v) {

        // Открываем приложение с картами с координатами объекта недвижимости
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("geo:" + advertLat + "," + advertLng +
                        "?q=" + advertLat + "," + advertLng +"(" + advertAddress + ")&z=18"));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void makeCallClick(View v) {

        // Открываем приложение для совершения вызова
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + advertPhone));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void openBrowserClick(View v) {

        // Открываем приложение для отправки ссылки на объявление
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(advertURL));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void evalClick(View v) {

        // Отключаем активность кнопок
        disableButtons();

        // Сообщаем агенту Manager о том, что пользователь оценил сообщение
        Boolean evaluation;
        switch (v.getId()) {
            case R.id.btnLike: {
                evaluation = true;
                break;
            }
            case R.id.btnDislike: {
                evaluation = false;
                break;
            }
        }
        // ToDo отправить сообщение evaluation менеджеру
        // ToDo добавить в БД замечание о том, что объявление было оценено
    }

    private void disableButtons() {

        // Находим кнопки и делаем их неактивными
        Button btnLike = (Button) findViewById(R.id.btnLike);
        Button btnDislike = (Button) findViewById(R.id.btnDislike);
        if (btnLike != null) {
            btnLike.setEnabled(false);
        }
        if (btnDislike != null) {
            btnDislike.setEnabled(false);
        }
    }
}

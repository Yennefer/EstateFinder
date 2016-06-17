package com.yennefer.estatefinder.gui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.koushikdutta.ion.Ion;
import com.yennefer.estatefinder.R;
import com.yennefer.estatefinder.database.Constants;
import com.yennefer.estatefinder.database.DBManager;

/**
 * Created by Yennefer on 29.05.2016.
 * Экран результатов. Отображает список найденных объявлений.
 */
public class ResultsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    // Идентификатор поискового запроса
    static long searchID;
    static long lastSearchID;

    // Адаптер для загрузки результатов поиска
    private SimpleCursorAdapter resultsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        // Настройка панели действий
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Инициализация
        searchID = getIntent().getLongExtra("search_id", lastSearchID);
        lastSearchID = searchID;
        DBManager.init(getApplicationContext());

        // Заполняем визуальные компоненты данными
        fillViews();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Обновляем данные
        getSupportLoaderManager().getLoader(0).forceLoad();
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

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new resultsCursorLoader(getApplicationContext());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        resultsAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    static class resultsCursorLoader extends CursorLoader {

        public resultsCursorLoader(Context context) {
            super(context);
        }

        @Override
        public Cursor loadInBackground() {

            // Выполняем запрос результатов поисков в БД
            return DBManager.getInstance().getResults(lastSearchID);
        }

    }

    private void fillViews() {

        // Параметры для адаптера
        String[] from = { "images", "title", "price", "district", "date", "time" };
        int[] to = { R.id.ivPhoto, R.id.tvTitle, R.id.tvPrice, R.id.tvDistrict, R.id.tvPostDate, R.id.tvPostTime };

        // Ищем список
        ListView lvResults = (ListView) findViewById(R.id.lvResults);

        // Создаем адаптер
        resultsAdapter = new SimpleCursorAdapter(getApplicationContext(),
                R.layout.item_result, null, from, to, 0);

        // Задаем обработчик вывода изображения по url
        resultsAdapter.setViewBinder( new SimpleCursorAdapter.ViewBinder() {

            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if (view.getId() == R.id.ivPhoto) {

                    // Получаем url первой картинки из списка
                    String imageUrl = cursor.getString(cursor.getColumnIndex(Constants.IMAGES_FIELD)).split(",")[0];

                    // Асинхронно загружаем и кэшируем изображение
                    Ion.with((ImageView) view)
                            .error(R.drawable.image_placeholder)
                            .load(imageUrl);
                    return true;
                }
                return false;
            }
        });

        // Настраиваем список
        if (lvResults != null) {

            // Назначаем адаптер
            lvResults.setAdapter(resultsAdapter);

            // Назначаем обработчик нажатия элемента списка
            lvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    // Открываем окно с параметрами объявления
                    Intent intent = new Intent(getApplicationContext(), AdvertActivity.class);
                    intent.putExtra("advert_id", id);
                    startActivity(intent);
                }
            });
        }

        // Зпускаем загрузку результатов поискового запроса
        getSupportLoaderManager().initLoader(0, null, this);
    }
}

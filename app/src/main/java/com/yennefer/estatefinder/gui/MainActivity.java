package com.yennefer.estatefinder.gui;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.yennefer.estatefinder.R;
import com.yennefer.estatefinder.agents.Constants;
import com.yennefer.estatefinder.agents.Evaluator;
import com.yennefer.estatefinder.agents.Finder;
import com.yennefer.estatefinder.agents.Manager;
import com.yennefer.estatefinder.database.DBManager;

import jade.android.AgentContainerHandler;
import jade.android.AgentHandler;
import jade.android.AndroidHelper;
import jade.android.RuntimeCallback;
import jade.android.RuntimeService;
import jade.android.RuntimeServiceBinder;
import jade.core.Profile;
import jade.core.ProfileImpl;

/**
 * Created by Yennefer on 29.05.2016.
 * Основной экран. Отображает список поисков.
 */
public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    // Переменные для запуска МАС:
    // соединение с сервисом
    private ServiceConnection serviceConnection;
    // биндер для работы с сервисом
    private RuntimeServiceBinder runtimeServiceBinder;
    // переменная для управления агентом Manager
//    private ManagerInterface manager;
    private AgentReceiver agentReceiver;

    // Переменные для заполнения списка поисков
    private ListView lvSearches;
    private SimpleCursorAdapter searchesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Настройка панели действий
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Создаем обработку события нажатия кнопки "+"
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Открываем окно с параметрами поиска
                startActivity( new Intent(getApplicationContext(), SearchActivity.class) );
            }
        });

        // Инициализация
        serviceConnection = null;
        DBManager.init(getApplicationContext());

        // Заполняем визуальные компоненты данными
        fillViews();

        // Запускаем МАС
        startAgentSystem();

        // Регестрируем ресивер
        agentReceiver = new AgentReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.REFRESH_ACTION);
        registerReceiver(agentReceiver, filter);
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
    protected void onDestroy() {

        // Отключаем ресивер
        unregisterReceiver(agentReceiver);

        super.onDestroy();

        // Останавливаем работу МАС
        stopAgentSystem();
    }

    private void fillViews() {

        // Параметры для адаптера
        String[] from = {"name", "advert_count"};
        int[] to = {R.id.tvSearchName, R.id.tvCount};

        // Ищем список
        lvSearches = (ListView) findViewById(R.id.lvSearches);

        // Создаем адаптер
        searchesAdapter = new SimpleCursorAdapter(getApplicationContext(),
                R.layout.item_search, null, from, to, 0);

        // Настраиваем список
        if (lvSearches != null) {

            // Назначаем адаптер
            lvSearches.setAdapter(searchesAdapter);

            // Назначаем обработчик нажатия элемента списка
            lvSearches.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    // Открываем окно с результатами поиска
                    Intent intent = new Intent(getApplicationContext(), ResultsActivity.class);
                    intent.putExtra("search_id", id);
                    startActivity(intent);
                }
            });

            // Настриваем возможность выбора нескольких элементов
            lvSearches.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

            // Назначаем и настраиваем контекстную панель действий
            lvSearches.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

                @Override
                public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                    mode.setTitle(String.valueOf(lvSearches.getCheckedItemCount()));
                }

                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    MenuInflater inflater = mode.getMenuInflater();
                    inflater.inflate(R.menu.menu_searches, menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    if (item.getItemId() == R.id.action_delete) {
                        deleteSelectedItems();
                        mode.finish();
                        return true;
                    }
                    return false;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {

                }
            });
        }

        // Зпускаем загрузку списка поисковых запросов
        getSupportLoaderManager().initLoader(0, null, this);
    }

    private void deleteSelectedItems() {

        // Удаляем выбранные в списке присковые запросы
        SparseBooleanArray checkedItems = lvSearches.getCheckedItemPositions();
        for (int i = 0; i < checkedItems.size(); i++) {
            if (checkedItems.valueAt(i)) {
                long id = lvSearches.getAdapter().getItemId(checkedItems.keyAt(i));
                DBManager.getInstance().deleteSearchByID(id);
            }
        }

        // Обновляем список поисковых запросов
        getSupportLoaderManager().getLoader(0).forceLoad();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bndl) {
        return new searchesCursorLoader(getApplicationContext());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        searchesAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    static class searchesCursorLoader extends CursorLoader {

        public searchesCursorLoader(Context context) {
            super(context);
        }

        @Override
        public Cursor loadInBackground() {

            // Выполняем запрос списка поисков в БД
            return DBManager.getInstance().getSearches();
        }

    }

    private void startAgentSystem() {

        // Проверяем подключение к сервису
        if (serviceConnection != null) {
            Log.d("MyLogs", "Подключение к сервису уже было выполнено.");
            return;
        }

        // Подключаемся к сервису, получаем объект для взаимодействия
        // с JADE Runtime (runtimeServiceBinder)
        serviceConnection = new ServiceConnection() {

            public void onServiceConnected(ComponentName className, IBinder service) {
                runtimeServiceBinder = (RuntimeServiceBinder) service;
                Log.d("MyLogs", "Подключение к сервису выполнено успешно. Создаю контейнер.");

                // Запускаем контейнер
                startContainer();
            }

            public void onServiceDisconnected(ComponentName className) {
                Log.d("MyLogs", "Произошла потеря связи с сервисом.");

                // Уничтожаем контейнер
                runtimeServiceBinder = null;
                stopAgentSystem();
            }
        };

        bindService(new Intent(getApplicationContext(), RuntimeService.class),
                serviceConnection, Context.BIND_AUTO_CREATE);

    }

    private void startContainer() {
        
        // Задаем параметры запуска контейнера
        Profile p = new ProfileImpl();
        p.setParameter(Profile.MAIN, Boolean.TRUE.toString());
        p.setParameter(Profile.JVM, Profile.ANDROID);

        if (AndroidHelper.isEmulator()) {
            p.setParameter(Profile.LOCAL_HOST, AndroidHelper.LOOPBACK);
            p.setParameter(Profile.LOCAL_PORT, "2000");
        } else {
            p.setParameter(Profile.LOCAL_HOST,
                    AndroidHelper.getLocalIPAddress());
        }

        // Создаем контейнер для агентов
        runtimeServiceBinder.createAgentContainer(p,

                new RuntimeCallback<AgentContainerHandler>() {

                    @Override
                    public void onSuccess(AgentContainerHandler agentContainerHandler) {
                        Log.d("MyLogs", "Контейнер создан успешно. Запускаю агентов.");

                        // Создаем и запускаем агентов в контейнере
                        startAgent(Constants.FINDER_NAME, Finder.class.getName());
                        startAgent(Constants.EVALUATOR_NAME, Evaluator.class.getName());
                        startAgent(Constants.MANAGER_NAME, Manager.class.getName());
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.d("MyLogs", "Не удалось создать контейнер.");
                    }
                }
        );
    }

    private void startAgent(final String agentNickname, String agentClassName) {

        // Создаем агента
        runtimeServiceBinder.getContainerHandler().createNewAgent(agentNickname,
                agentClassName,
                new Object[]{getApplicationContext()},
                new RuntimeCallback<AgentHandler>() {

                    @Override
                    public void onSuccess(AgentHandler agentHandler) {
                        Log.d("MyLogs", "Агент " + agentNickname + " создан успешно.");

                        // Запускаем агента
                        agentHandler.start(new RuntimeCallback<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("MyLogs", "Агент " + agentNickname + " запущен успешно.");
                            }

                            @Override
                            public void onFailure(Throwable throwable) {
                                Log.d("MyLogs", "Не удалось запустить агента " + agentNickname + ".");
                            }
                        });
//                        // Получаем объект для взаимодействия с агентом Manager
//                        if (agentNickname.equals("Manager")) {
//                            try {
//                                manager = agentHandler.getAgentController().
//                                        getO2AInterface(ManagerInterface.class);
//                                Log.d("MyLogs", "Инициализировала объект manager");
//                            } catch (StaleProxyException e) {
//                                e.printStackTrace();
//                            }
//                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.d("MyLogs", "Не удалось создать агента " + agentNickname + ".");
                    }
                }
        );
    }

    private void stopAgentSystem() {

        // Уничтожаем контейнер с агентами
        if (runtimeServiceBinder.getContainerHandler() != null) {

            runtimeServiceBinder.getContainerHandler().kill(new RuntimeCallback<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("MyLogs", "Контейнер с агентами успешно уничтожен.");
                }

                @Override
                public void onFailure(Throwable throwable) {
                    Log.d("MyLogs", "Не удалось уничтожить контейнер с агентами.");
                }
            });
        }

        // Проверяем подключение к сервису
        if (serviceConnection == null) {
            Log.d("MyLogs", "Подключение к сервису не было выполнено.");
            return;
        }

        // Отключаемся от сервиса
        unbindService(serviceConnection);
        serviceConnection = null;
        Log.d("MyLogs", "Выполнено отключение от сервиса.");
    }

    class AgentReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constants.REFRESH_ACTION)) {

                // Обновляем данные
                Log.d("MyLogs", "Обновляем данные");
                getSupportLoaderManager().getLoader(0).forceLoad();
            }
        }
    }

}

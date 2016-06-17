package com.yennefer.estatefinder.agents;

import android.content.Context;
import android.util.Log;

import com.yennefer.estatefinder.database.DBManager;
import com.yennefer.estatefinder.model.Advert;
import com.yennefer.estatefinder.model.AdvertParam;
import com.yennefer.estatefinder.model.AdvertsData;
import com.yennefer.estatefinder.model.IRestAPI;

import java.util.Map;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Yennefer on 01.06.2016.
 * Агент - искатель. Отвечает за поиск объявлений в сети.
 */
public class Finder extends Agent {

    // Контекст
    Context context;

    // Идентификатор поиска, над которым работает агент
    int currentSearchID = 0;

    // Инициализация после запуска агента
    protected void setup() {
        Log.d("MyLogs", "Hello! agent Finder is ready to work.");

        // Получаем контекст
        context = (Context) getArguments()[0];

        // Инициализируем менеджер БД
        DBManager.init(context);

        // Добавляем поведение для получения сообщений
        addBehaviour(new CyclicBehaviour() {

            @Override
            public void action() {
                ACLMessage msg = myAgent.receive();
                if (msg != null) {

                    // Обработка полученого сообщения
                    makeASearch(msg.getContent());
                } else {
                    block();
                }
            }
        });
    }

    private void makeASearch(String searchID) {

        // Если обработка текущего поиска еще ведется, не повторяем ее
        if (currentSearchID == Integer.valueOf(searchID)) {
            return;
        }

        currentSearchID = Integer.valueOf(searchID);

        // Запрос параметров поиска
        int categoryID = DBManager.getInstance().getSearchCategory(searchID);
        int regionID = DBManager.getInstance().getSearchRegion(searchID);
        int cityID = DBManager.getInstance().getSearchCity(searchID);
        int withPhoto = DBManager.getInstance().getSearchWithPhoto(searchID);
        String startDate = DBManager.getInstance().getSearchStartDate(searchID);
        String endDate = DBManager.getInstance().getSearchEndDate(searchID);
        float minPrice = DBManager.getInstance().getSearchMinPrice(searchID);
        float maxPrice = DBManager.getInstance().getSearchMaxPrice(searchID);

        // Поиск объявлений
        getAvitoAds(categoryID, regionID, cityID, withPhoto, startDate, endDate, minPrice, maxPrice);
    }

    private void getAvitoAds(final int categoryID, final int regionID, final int cityID,
                             final int withPhoto, final String startDate, final String endDate,
                             final float minPrice, final float maxPrice) {

        // Данные для подключения к AvitoAPI
        String emailAddress = "maghelyenn@gmail.com";
        String accessToken = "c0450c0c334726946c6087ae6410a905";
        String apiURL = "http://rest-app.net/api/";

        // Создаем подключение
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(apiURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        IRestAPI restAPI = retrofit.create(IRestAPI.class);
        Call<AdvertsData> call;

        // Выбираем тип запроса
        if (minPrice <= 0 && maxPrice <= 0) {
            call = restAPI.getAdsWithoutPrice(emailAddress, accessToken, categoryID, regionID,
                    startDate, endDate);
        } else if (minPrice <= 0 && maxPrice > 0) {
            call = restAPI.getAdsWithMaxPrice(emailAddress, accessToken, categoryID, regionID,
                    startDate, endDate, maxPrice);
        } else if (minPrice > 0 && maxPrice <= 0) {
            call = restAPI.getAdsWithMinPrice(emailAddress, accessToken, categoryID, regionID,
                    startDate, endDate, minPrice);
        } else {
            call = restAPI.getAdsWithPrice(emailAddress, accessToken, categoryID, regionID,
                    startDate, endDate, minPrice, maxPrice);
        }

        // Выполняем запрос
        call.enqueue(new Callback<AdvertsData>() {
            @Override
            public void onResponse(Call<AdvertsData> call, Response<AdvertsData> response) {
                Log.d("MyLogs", response.raw().toString());

                AdvertsData advertsData = response.body();
                if (advertsData != null) {

                    // Если объявления по запросу найдены, применяем фильтрацию
                    if (advertsData.getAdvertsCont() > 0) {
                        applyFilter(categoryID, cityID, withPhoto, advertsData);
                    }
                }
            }

            @Override
            public void onFailure(Call<AdvertsData> call, Throwable t) {
                Log.i("MyLogs", "Finder: Неудачное завершение запроса");
                currentSearchID = 0;
                t.printStackTrace();
            }
        });
    }

    private void applyFilter(final int categoryID, final int cityID, final int withPhoto, AdvertsData advertsData) {

        // Получаем параметры поиска
        Map<String, String> searchParams =
                DBManager.getInstance().getSearchParams( String.valueOf(currentSearchID) );

        if ( !searchParams.isEmpty() ) {

            // Фильтруем объявления
            for (Advert ad : advertsData.getData()) {

                // Фильтрация по городу
                String cityName = DBManager.getInstance().getCityByID(cityID);
                if (!cityName.equals("") && !cityName.equals(ad.getCity())) {
                    ad.markAsDelete();
                } else {

                    // Фильтрация по фото
                    if (withPhoto == 1 && ad.getImages().equals("")) {
                        ad.markAsDelete();
                    } else {

                        // Фильтрация по количествуву комнат
                        if (categoryID == 1) {

                        }

                        // Сравниваем дополнительные параметры поиска с параметрами объявления
                        for (AdvertParam adParam : ad.getParams()) {
                            for (String key : searchParams.keySet()) {

                                String paramName = adParam.getName();
                                String adParamValue = adParam.getValue();
                                String searchParamValue = searchParams.get(key);

                                // Фильтрация по виду объекта
                                if (categoryID == 1) {

                                }

                                if (paramName.equals(key)) {
                                    if (!searchParamValue.equals(adParamValue)) {
                                        ad.markAsDelete();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Удаляем объявления, не прошедшие фильтрацию
        advertsData.deleteAds();

        // Если после фильтрации осталось хотя бы одно объявление
        if (advertsData.getAdvertsCont() > 0) {

            // Сохраняем объявления
            DBManager.getInstance().saveAds(advertsData.getData(), currentSearchID);
        }

        // Обновляем дату поиска
        DBManager.getInstance().updateSearchDate(currentSearchID);

        // Сообщаем агенту Manager об удачном выполнении запроса
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(new AID(Constants.MANAGER_NAME, AID.ISLOCALNAME));
        msg.setContent(Constants.FOUND_MSG);
        send(msg);

        // Обнуляем идентификатор текущего поиска
        currentSearchID = 0;
    }

    // Выполнение необходимых действий перед удалением агента
    protected void takeDown() {
        Log.d("MyLogs", "Bye! agent Finder terminating.");
    }
}

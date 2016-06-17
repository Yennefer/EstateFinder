package com.yennefer.estatefinder.model;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Yennefer on 27.05.2016.
 * Интерфейс для обращения к Авито API.
 */
public interface IRestAPI {

    @GET("ads")
    Call<AdvertsData> getAdsWithPrice(@Query("login") String login,
                                      @Query("token") String token,
                                      @Query("category_id") int category_id,
                                      @Query("region_id") int region_id,
                                      @Query("date1") String date1,
                                      @Query("date2") String date2,
                                      @Query("price1") float price1,
                                      @Query("price2") float price2);

    @GET("ads")
    Call<AdvertsData> getAdsWithoutPrice(@Query("login") String login,
                                        @Query("token") String token,
                                        @Query("category_id") int category_id,
                                        @Query("region_id") int region_id,
                                        @Query("date1") String date1,
                                        @Query("date2") String date2);

    @GET("ads")
    Call<AdvertsData> getAdsWithMaxPrice(@Query("login") String login,
                                         @Query("token") String token,
                                         @Query("category_id") int category_id,
                                         @Query("region_id") int region_id,
                                         @Query("date1") String date1,
                                         @Query("date2") String date2,
                                         @Query("price2") float price2);

    @GET("ads")
    Call<AdvertsData> getAdsWithMinPrice(@Query("login") String login,
                                         @Query("token") String token,
                                         @Query("category_id") int category_id,
                                         @Query("region_id") int region_id,
                                         @Query("date1") String date1,
                                         @Query("date2") String date2,
                                         @Query("price1") float price1);

    @GET("ad")
    Call<AdvertsData> getAd(@Query("login") String login,
                            @Query("token") String token,
                            @Query("id") int id);

}

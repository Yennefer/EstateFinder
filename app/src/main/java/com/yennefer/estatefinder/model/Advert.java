package com.yennefer.estatefinder.model;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Yennefer on 27.05.2016.
 */
public class Advert {

    private String Id, avito_id, price, phone, url, title, time, operator, name, region, city,
            district, address, metro, images, description;
    private List<AdvertParam> params = new LinkedList<>();
    private AdvertCoordinate coords = new AdvertCoordinate();

    private boolean NeedToDelete = false;

    public String getId() {
        return Id;
    }

    public String getAvito_id() {
        return avito_id;
    }

    public String getPrice() {
        return price;
    }

    public String getPhone() {
        return phone;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public String getTime() {
        return time;
    }

    public String getOperator() {
        return operator;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public String getDistrict() {
        return district;
    }

    public String getAddress() {
        return address;
    }

    public String getMetro() {
        return metro;
    }

    public String getImages() {
        return images;
    }

    public String getDescription() {
        return description;
    }

    public List<AdvertParam> getParams() {
        return params;
    }

    public AdvertCoordinate getCoords() {
        return coords;
    }

    public void markAsDelete() {
        NeedToDelete = true;
    }

    public boolean doesNeedToDelete() {
        return NeedToDelete;
    }
}

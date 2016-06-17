package com.yennefer.estatefinder.model;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Yennefer on 29.05.2016.
 */
public class AdvertsData {

    private String status;
    private List<Advert> data = new LinkedList<Advert>();

    public List<Advert> getData() {
        return data;
    }

    public int getAdvertsCont() {
        return data.size();
    }

    public void deleteAds() {
        for (Iterator<Advert> iterator = getData().iterator(); iterator.hasNext();) {
            Advert ad = iterator.next();
            if ( ad.doesNeedToDelete() ) {
                iterator.remove();
            }
        }
    }
}

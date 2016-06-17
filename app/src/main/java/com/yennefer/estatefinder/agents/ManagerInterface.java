package com.yennefer.estatefinder.agents;

/**
 * Created by Yennefer on 11.06.2016.
 * Интерфейс позволяет делать запрос к агенту Manager из Activity
 */
public interface ManagerInterface {

    // Метод, сообщающий агенту, что произошла оценка оъявления пользователем
    void AdEvaluated(final String advert_id, final String search_id, final Boolean doesLike);
}

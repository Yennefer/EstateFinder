package com.yennefer.estatefinder.agents;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.yennefer.estatefinder.database.DBManager;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

/**
 * Created by Yennefer on 01.06.2016.
 * Агент - менеджер. Следит за обновлениями БД и управляет работой других агентов.
 */
public class Manager extends Agent implements ManagerInterface {

    // Контекст
    Context context;

    // Идентификаторы оцененного объявления и его поискового запроса
    String advertID, searchID;
    // Оценка объявления (true - понравилось, false - не понравилось)
    Boolean like;

    // Инициализация после запуска агента
    protected void setup() {
        Log.d("MyLogs", "Hello! Agent Manager is ready to work.");

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
                        if (msg.getContent().equals(Constants.FOUND_MSG)) {

                            // Отправка команды агенту Evaluator
                            msg = new ACLMessage(ACLMessage.REQUEST);
                            msg.addReceiver(new AID(Constants.EVALUATOR_NAME, AID.ISLOCALNAME));
                            msg.setContent(Constants.EVAL_NEW_MSG);
                            send(msg);
                        } else if (msg.getContent().equals(Constants.EVALUATED_MSG)) {

                            // Сообщение всем заинтересованным об обновлении данных
                            broadcastIntent();
                        } else {
                            block();
                        }
                    }
                }
            });

        // Добавляем поведение для проверки БД на необходимость выполнения поиска
        addBehaviour(new TickerBehaviour(this, 1000) {

            @Override
            public void onTick() {
                int search = checkForSearchesToPreform();
                if (search != 0) {

                    // Отправление команды агенту Finder
                    ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                    msg.addReceiver(new AID(Constants.FINDER_NAME, AID.ISLOCALNAME));
                    msg.setContent(String.valueOf(search));
                    send(msg);
                }
            }
        });

        // Регестрируем интерфейс для взаимодействия с Activity
        registerO2AInterface(ManagerInterface.class, this);
    }

    // Выполнение необходимых действий перед удалением агента
    protected void takeDown() {
        Log.d("MyLogs", "Bye! Agent Manager terminating.");
    }

    private int checkForSearchesToPreform() {

        // Проверяем необходимость проведения поиска и возвращаем идентификатор
        // поискового запроса, который необходимо выполнить
        return DBManager.getInstance().getSearchToDoID();
    }

    // Метод вызывается, когда пользователь оценивает объявление
    @Override
    public void AdEvaluated(String advert_id, String search_id, Boolean doesLike) {

        // Сохраняем идентификатор и оценку объявления
        advertID = advert_id;
        searchID = search_id;
        like = doesLike;

        // Добавляем поведение оповещения агента Evaluator о необходиомсти переоценки объявлений
        addBehaviour(new OneShotBehaviour() {

            @Override
            public void action() {

                // Отправляем команды агенту Evaluator
                ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                msg.addReceiver(new AID(Constants.EVALUATOR_NAME, AID.ISLOCALNAME));
                msg.setContent(Constants.REEVAL_ALL_MSG);
                send(msg);
            }
        });
    }

    private void broadcastIntent() {
        Intent broadcast = new Intent();
        broadcast.setAction(Constants.REFRESH_ACTION);
        context.sendBroadcast(broadcast);
    }
}

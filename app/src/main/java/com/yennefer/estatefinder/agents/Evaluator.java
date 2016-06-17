package com.yennefer.estatefinder.agents;

import android.util.Log;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

/**
 * Created by Yennefer on 01.06.2016.
 * Агент - оценщик. Оценивает объявления, присваивая им определенный рейтинг.
 */
public class Evaluator extends Agent {

    // Инициализация после запуска агента
    protected void setup() {
        Log.d("MyLogs", "Hello! agent Evaluator is ready to work.");

        // Добавляем поведение для получения собщений
        addBehaviour(new CyclicBehaviour() {

            @Override
            public void action() {
                ACLMessage msg = myAgent.receive();
                if (msg != null) {

                    // Обработка полученого сообщения
                    switch (msg.getContent()) {
                        case Constants.EVAL_NEW_MSG: {

                            // Оцениваем новые объявления
                            evalNewAds();
                            break;
                        }
                        case Constants.REEVAL_ALL_MSG: {

                            // Переоцениваем объявления
                            reEvalAllAds();
                            break;
                        }
                        default:
                            break;
                    }

                    // Отправка ответа
                    ACLMessage reply = msg.createReply();
                    reply.setContent(Constants.EVALUATED_MSG);
                    myAgent.send(reply);
                } else {
                    block();
                }
            }
        });
    }

    private void reEvalAllAds() {
    }

    private void evalNewAds() {
    }

    // Выполнение необходимых действий перед удалением агента
    protected void takeDown() {
        Log.d("MyLogs", "Bye! agent Evaluator terminating.");
    }
}

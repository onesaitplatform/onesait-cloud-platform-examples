package com.minsait.onesaitplatform.digitaltwins.fcm;

import java.util.Map;

import io.reactivex.Notification;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

// Gestor de notificaciones para implementar la comunicación entre el servicio de FCM y la actividad.
public class NotificationsManager {

    private static PublishSubject<Notification<Map<String, String>>> notificationPublisher;

    public static PublishSubject<Notification<Map<String, String>>> getPublisher() {
        if (notificationPublisher == null) {
            notificationPublisher = PublishSubject.create();
        }

        return notificationPublisher;
    }

    public static Observable<Notification<Map<String, String>>> getNotificationObservable() {
        return getPublisher();
    }
}

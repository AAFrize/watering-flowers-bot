package aa.frieze.wateringflowersbot.service;

import aa.frieze.wateringflowersbot.domain.Notification;

import java.time.ZoneId;

public interface NotificationService {

    String mapAndUpdateNotification(Notification notification);

    String getNotificationInfo(Notification notification, ZoneId zoneId);

    String getActualNotificationInfo(Notification notification, ZoneId zoneId);

}

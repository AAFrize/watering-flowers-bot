package aa.frieze.wateringflowersbot.service;

import aa.frieze.wateringflowersbot.domain.Notification;

public interface NotificationService {

    String mapAndUpdateNotification(Notification notification);

    String getNotificationInfo(Notification notification);

    String getActualNotificationInfo(Notification notification);

}

package aa.frieze.wateringflowersbot.service;

import aa.frieze.wateringflowersbot.domain.Notification;
import aa.frieze.wateringflowersbot.domain.TelegramAccount;

import java.time.ZoneId;

public interface NotificationService {

    String mapAndUpdateNotification(Notification notification, TelegramAccount account);

    String getNotificationInfo(Notification notification, ZoneId zoneId);

    String getActualNotificationInfo(Notification notification, ZoneId zoneId);

}

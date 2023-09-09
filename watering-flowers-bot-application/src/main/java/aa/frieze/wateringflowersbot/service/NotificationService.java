package aa.frieze.wateringflowersbot.service;

import aa.frieze.wateringflowersbot.domain.Notification;
import aa.frieze.wateringflowersbot.domain.dto.Settings;

public interface NotificationService {

    String mapAndUpdateNotification(Notification notification);

    String getNotificationInfo(Notification notification);

    String getActualNotificationInfo(Notification notification);

    Notification createOrUpdateNotification(Settings.SettingDto settingDto, Long accountId);

}

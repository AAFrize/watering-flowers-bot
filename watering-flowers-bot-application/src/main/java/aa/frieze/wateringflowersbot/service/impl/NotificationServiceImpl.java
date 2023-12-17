package aa.frieze.wateringflowersbot.service.impl;

import aa.frieze.wateringflowersbot.domain.Notification;
import aa.frieze.wateringflowersbot.domain.TelegramAccount;
import aa.frieze.wateringflowersbot.domain.dto.Settings;
import aa.frieze.wateringflowersbot.repository.NotificationRepository;
import aa.frieze.wateringflowersbot.service.NotificationService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;

import static aa.frieze.wateringflowersbot.service.util.Constants.*;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final ObjectMapper mapper;
    private final NotificationRepository notificationRepository;

    @Override
    @SneakyThrows
    @Transactional
    public String mapAndUpdateNotification(Notification notification, TelegramAccount account) {
        JsonNode settingsNode = account.getSettings();
        if (Objects.isNull(settingsNode)) {
            return null;
        }

        Settings settings = mapper.treeToValue(settingsNode, Settings.class);
        Settings.SettingDto settingDto = settings.getSettings().stream().filter(setting ->
                StringUtils.equals(setting.getTitle(), notification.getTitle())).findFirst().orElse(null);
        if (Objects.isNull(settingDto)) {
            return null;
        }

        ZonedDateTime nextNotificationDate = Objects.isNull(notification.getNextNotificationDate())
                ? ZonedDateTime.now().plus(settingDto.getPeriodValue(), settingDto.getPeriodUnit())
                : notification.getNextNotificationDate().plus(settingDto.getPeriodValue(),
                settingDto.getPeriodUnit());

        notification.setNextNotificationDate(nextNotificationDate);
        notification.setLastNotificationDate(ZonedDateTime.now());
        notificationRepository.save(notification);

        return String.format(NOTIFYING_MESSAGE, STOPWATCH_EMOJI, notification.getTitle(),
                dateFormatter.format(notification.getNextNotificationDate()
                        .withZoneSameInstant(settings.getTimeZone())));
    }

    @Override
    public String getNotificationInfo(Notification notification, ZoneId zoneId) {
        return String.format(NOTIFICATION_INFO, notification.getTitle(), notification.isArchived()
                        ? STOP_BUTTON_EMOJI : ARROW_FORWARD_EMOJI, notification.getArchivedString(),
                Objects.isNull(notification.getLastNotificationDate()) ? "-" :
                dateFormatter.format(notification.getLastNotificationDate()
                        .withZoneSameInstant(zoneId)),
                dateFormatter.format(notification.getNextNotificationDate()
                        .withZoneSameInstant(zoneId)));
    }

    @Override
    public String getActualNotificationInfo(Notification notification, ZoneId zoneId) {
        return String.format(ACTUAL_NOTIFICATION_INFO, notification.getTitle(),
                Objects.isNull(notification.getLastNotificationDate()) ? "-" :
                dateFormatter.format(notification.getLastNotificationDate()
                        .withZoneSameInstant(zoneId)),
                dateFormatter.format(notification.getNextNotificationDate()
                        .withZoneSameInstant(zoneId)));
    }

}

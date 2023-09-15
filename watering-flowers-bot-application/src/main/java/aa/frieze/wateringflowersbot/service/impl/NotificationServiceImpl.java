package aa.frieze.wateringflowersbot.service.impl;

import aa.frieze.wateringflowersbot.domain.Notification;
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
    public String mapAndUpdateNotification(Notification notification) {
        JsonNode settingsNode = notification.getTelegramAccount().getSettings();
        if (Objects.isNull(settingsNode)) {
            return null;
        }

        Settings settings = mapper.treeToValue(settingsNode, Settings.class);
        Settings.SettingDto settingDto = settings.getSettings().stream().filter(setting ->
                StringUtils.equals(setting.getTitle(), notification.getTitle())).findFirst().orElse(null);
        if (Objects.isNull(settingDto)) {
            return null;
        }

        notification.setNextNotificationDate(ZonedDateTime.now().plus(settingDto.getPeriodValue(),
                settingDto.getPeriodUnit()));
        notification.setLastNotificationDate(ZonedDateTime.now());
        notificationRepository.save(notification);

        return String.format(NOTIFYING_MESSAGE, STOPWATCH_EMOJI, notification.getTitle(),
                dateFormatter.format(notification.getNextNotificationDate()
                        .withZoneSameInstant(settings.getTimeZone())));
    }

    @Override
    public String getNotificationInfo(Notification notification) {
        return String.format(NOTIFICATION_INFO, notification.getTitle(), notification.isArchived()
                        ? STOP_BUTTON_EMOJI : ARROW_FORWARD_EMOJI, notification.getArchivedString(),
                Objects.isNull(notification.getLastNotificationDate()) ? "-" :
                dateFormatter.format(notification.getLastNotificationDate()),
                dateFormatter.format(notification.getNextNotificationDate()));
    }

    @Override
    public String getActualNotificationInfo(Notification notification) {
        return String.format(ACTUAL_NOTIFICATION_INFO, notification.getTitle(),
                Objects.isNull(notification.getLastNotificationDate()) ? "-" :
                dateFormatter.format(notification.getLastNotificationDate()),
                dateFormatter.format(notification.getNextNotificationDate()));
    }

/*    @Transactional
    public Notification createOrUpdateNotification(Settings.SettingDto settingDto, Long accountId) {
        Notification notification = notificationRepository
                .findByTitleAndTelegramAccountId(settingDto.getTitle(), accountId)
                .orElseGet(Notification::new);

        ZonedDateTime lastDate = notification.getLastNotificationDate();
        lastDate = Objects.nonNull(lastDate) ? lastDate : settingDto.getFirstNotificationDate();
        lastDate = Objects.nonNull(lastDate) ? lastDate : ZonedDateTime.now();

        ZonedDateTime nextDate = notification.getNextNotificationDate();
        nextDate = Objects.nonNull(nextDate) ? nextDate : lastDate.plus(settingDto.getPeriodValue(),
                settingDto.getPeriodUnit());

        notification.setTitle(settingDto.getTitle());
        notification.setArchived(false);
        notification.setLastNotificationDate(lastDate);
        notification.setNextNotificationDate(nextDate);
        return notificationRepository.save(notification);
    }*/
}

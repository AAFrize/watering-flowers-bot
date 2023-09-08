package aa.frieze.wateringflowersbot.service.impl;

import aa.frieze.wateringflowersbot.domain.Notification;
import aa.frieze.wateringflowersbot.domain.TelegramAccount;
import aa.frieze.wateringflowersbot.domain.dto.Settings;
import aa.frieze.wateringflowersbot.repository.NotificationRepository;
import aa.frieze.wateringflowersbot.repository.TelegramAccountRepository;
import aa.frieze.wateringflowersbot.service.ScheduleService;
import aa.frieze.wateringflowersbot.service.TelegramService;
import aa.frieze.wateringflowersbot.service.json.JsonMappingService;
import aa.frieze.wateringflowersbot.service.util.Constants;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static aa.frieze.wateringflowersbot.service.util.Constants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    @Value("${telegram-bot.enabled}")
    private Boolean botEnabled;
    @Value("${scheduler.enabled}")
    private Boolean schedulerEnabled;

    private final ObjectMapper mapper;
    private final TelegramService telegramService;

    private final TelegramAccountRepository accountRepository;
    private final NotificationRepository notificationRepository;

    @Override
    @Scheduled(fixedDelayString = "${scheduler.notifyingPeriod}")
    public void notifyAboutWatering() {
        log.info("Start auto notifying");
        forceNotifyAboutWatering();
        log.info("End auto notifying");
    }

    private void forceNotifyAboutWatering() {
        if (!BooleanUtils.and(new Boolean[]{botEnabled, schedulerEnabled})) {
            return;
        }
        List<TelegramAccount> subscribedAccounts = accountRepository.findAllBySubscribedTrue();
        for (TelegramAccount account : subscribedAccounts) {
            String allNotifyingMessages = account.getNotifications().stream()
                    .filter(notification ->
                            !ZonedDateTime.now().isBefore(notification.getNextNotificationDate()))
                    .map(this::mapNotificationMessage)
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining("\n\n"));
            if (StringUtils.isBlank(allNotifyingMessages)) {
                continue;
            }
            telegramService.sendMessage(account.getChatId(), allNotifyingMessages + NOTIFYING_MESSAGE_END);
            log.info("Notification was send to user: {}", account.getUsername());
        }
    }

    @SneakyThrows
    @Transactional
    protected String mapNotificationMessage(Notification notification) {
        JsonNode settingsNode = notification.getTelegramAccount().getSettings();
        if (Objects.isNull(settingsNode)) {
            return null;
        }

        Settings settings = mapper.treeToValue(settingsNode, Settings.class);
        Settings.SettingDto settingDto = settings.getSettings().stream().filter(setting -> StringUtils.equals(setting.getTitle(),
                notification.getTitle())).findFirst().orElse(null);
        if (Objects.isNull(settingDto)) {
            return null;
        }

        notification.setNextNotificationDate(ZonedDateTime.now().plus(settingDto.getPeriodValue(),
                settingDto.getPeriodUnit()));
        notification.setLastNotificationDate(ZonedDateTime.now());
        notificationRepository.save(notification);

        return String.format(NOTIFYING_MESSAGE, STOPWATCH_EMOJI, notification.getTitle(),
                dateFormatter.format(notification.getNextNotificationDate()));
    }
}

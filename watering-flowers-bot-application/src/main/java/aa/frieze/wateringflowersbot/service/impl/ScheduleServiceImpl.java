package aa.frieze.wateringflowersbot.service.impl;

import aa.frieze.wateringflowersbot.domain.TelegramAccount;
import aa.frieze.wateringflowersbot.repository.TelegramAccountRepository;
import aa.frieze.wateringflowersbot.service.NotificationService;
import aa.frieze.wateringflowersbot.service.ScheduleService;
import aa.frieze.wateringflowersbot.service.TelegramService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static aa.frieze.wateringflowersbot.service.util.Constants.NOTIFYING_MESSAGE_END;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    @Value("${telegram-bot.enabled}")
    private Boolean botEnabled;
    @Value("${scheduler.enabled}")
    private Boolean schedulerEnabled;

    private final TelegramService telegramService;
    private final NotificationService notificationService;

    private final TelegramAccountRepository accountRepository;

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
                    .map(notification -> notificationService.mapAndUpdateNotification(notification, account))
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining("\n\n"));

            if (StringUtils.isBlank(allNotifyingMessages)) {
                continue;
            }

            telegramService.sendMessage(account.getChatId(), allNotifyingMessages + NOTIFYING_MESSAGE_END);
            log.info("Notification was send to user: {}", account.getUsername());
        }
    }

}

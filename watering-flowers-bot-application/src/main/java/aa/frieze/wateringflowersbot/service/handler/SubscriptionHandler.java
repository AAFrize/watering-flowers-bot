package aa.frieze.wateringflowersbot.service.handler;

import aa.frieze.wateringflowersbot.domain.Notification;
import aa.frieze.wateringflowersbot.domain.TelegramAccount;
import aa.frieze.wateringflowersbot.domain.dto.Settings;
import aa.frieze.wateringflowersbot.domain.enumeration.BotState;
import aa.frieze.wateringflowersbot.error.AppException;
import aa.frieze.wateringflowersbot.repository.NotificationRepository;
import aa.frieze.wateringflowersbot.repository.TelegramAccountRepository;
import aa.frieze.wateringflowersbot.service.DataCache;
import aa.frieze.wateringflowersbot.service.ReplyKeyboardService;
import aa.frieze.wateringflowersbot.service.impl.AbstractTelegramCallbackBot;
import aa.frieze.wateringflowersbot.service.json.JsonMappingService;
import aa.frieze.wateringflowersbot.service.util.UnitParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;

import static aa.frieze.wateringflowersbot.service.util.Constants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionHandler implements InputMessageHandler {

    private final DataCache dataCache;
    private final ReplyKeyboardService replyKeyboardService;

    private final ObjectMapper mapper;
    private final UnitParser unitParser;
    private final JsonMappingService jsonMappingService;

    private final TelegramAccountRepository accountRepository;
    private final NotificationRepository notificationRepository;

    @Override
    public SendMessage handle(Message message, TelegramAccount telegramAccount, AbstractTelegramCallbackBot bot) {
        if (BotState.SHOW_MAIN_MENU.equals(dataCache.getUsersCurrentBotState(message.getFrom().getId()))) {
            dataCache.setUsersCurrentBotState(message.getFrom().getId(), BotState.SUBSCRIBE);
        }
        return processUsersInput(message, bot, telegramAccount);
    }

    private SendMessage processUsersInput(Message inputMsg, AbstractTelegramCallbackBot bot,
                                          TelegramAccount telegramAccount) {
        Long userId = inputMsg.getFrom().getId();
        Long chatId = inputMsg.getChatId();
        SendMessage replyToUser = new SendMessage(String.valueOf(chatId), TRY_AGAIN_MESSAGE);
        BotState botState = dataCache.getUsersCurrentBotState(userId);

        switch (botState) {
            case SUBSCRIBE -> {
                return processAskTitle(chatId, replyToUser);
            }
            case WAITING_FOR_TITLE -> {
                return processReceiveTitleAndAskTimeZone(chatId, replyToUser, inputMsg, telegramAccount);
            }
            case WAITING_FOR_TIMEZONE -> {
                return processReceiveTimeZoneAndAskStartDate(chatId, replyToUser, inputMsg);
            }
            case WAITING_FOR_START_DATE -> {
                return processReceiveStartDateAndAskDuration(chatId, replyToUser, inputMsg);
            }
            case WAITING_FOR_DURATION -> {
                return processReceiveDurationAndComplete(chatId, replyToUser, inputMsg, telegramAccount);
            }
            default -> {
                return replyKeyboardService.getMainMenuMessage(chatId, replyToUser.getText());
            }
        }
    }

    private SendMessage processAskTitle(Long userId, SendMessage replyToUser) {
        dataCache.setUsersCurrentBotState(userId, BotState.WAITING_FOR_TITLE);
        replyToUser.setText(TITLE_WAITING_MESSAGE);
        return replyToUser;
    }

    @SneakyThrows
    private SendMessage processReceiveTitleAndAskTimeZone(Long userId, SendMessage replyToUser, Message inputMsg,
                                                          TelegramAccount telegramAccount) {
        Settings settings = mapper.treeToValue(telegramAccount.getSettings(), Settings.class);
        if (Objects.nonNull(settings) && Objects.nonNull(settings.getSettings())
                && settings.getSettings().stream()
                .map(Settings.SettingDto::getTitle)
                .anyMatch(title -> title.equals(inputMsg.getText()))) {
            replyToUser.setText(TITLE_WARNING_MESSAGE);
            return replyToUser;
        }
        if (Objects.nonNull(settings) && Objects.nonNull(settings.getTimeZone())) {
            dataCache.setUsersCurrentZone(userId, settings.getTimeZone());
            dataCache.setUsersCurrentBotState(userId, BotState.WAITING_FOR_START_DATE);
            replyKeyboardService.getStartDateMessage(userId, START_DATE_CHOOSING_MESSAGE);
        }
        dataCache.setUsersCurrentBotState(userId, BotState.WAITING_FOR_TIMEZONE);
        dataCache.setUsersCurrentTitle(userId, inputMsg.getText());
        replyToUser.setText(TIMEZONE_WAITING_MESSAGE);
        return replyToUser;
    }

    private SendMessage processReceiveTimeZoneAndAskStartDate(Long userId, SendMessage replyToUser, Message inputMsg) {
        ZoneId inputZone;
        try {
            inputZone = ZoneId.of(inputMsg.getText());
        } catch (DateTimeException exception) {
            replyToUser.setText(TIMEZONE_WARNING_MESSAGE);
            return replyToUser;
        }

        dataCache.setUsersCurrentZone(userId, inputZone);
        dataCache.setUsersCurrentBotState(userId, BotState.WAITING_FOR_START_DATE);
        return replyKeyboardService.getStartDateMessage(userId, START_DATE_CHOOSING_MESSAGE);
    }

    private SendMessage processReceiveStartDateAndAskDuration(Long userId, SendMessage replyToUser, Message inputMsg) {
        ZonedDateTime startDate = ZonedDateTime.now(dataCache.getUsersCurrentZone(userId));
        switch (inputMsg.getText()) {
            case CURRENT_DATE_BUTTON -> saveUserStartDate(userId, replyToUser, startDate, null);
            case CUSTOM_DATE_BUTTON -> replyToUser.setText(START_DATE_CUSTOM_MESSAGE);
            default -> saveUserStartDate(userId, replyToUser, null, inputMsg.getText());
        }
        return replyToUser;
    }

    @SneakyThrows
    @Transactional
    protected SendMessage processReceiveDurationAndComplete(Long userId, SendMessage replyToUser, Message inputMsg,
                                                            TelegramAccount telegramAccount) {
        Pair<ChronoUnit, Long> unitLongPair;
        try {
            unitLongPair = unitParser.parsePeriodFromString(inputMsg.getText());
        } catch (AppException exception) {
            replyToUser.setText(DURATION_WARNING_MESSAGE);
            return replyToUser;
        }

        ZonedDateTime usersStartDate = dataCache.getUsersStartDate(userId);
        String title = dataCache.getUsersCurrentTitle(userId);
        Settings.SettingDto dto = Settings.mapSettingDto(unitLongPair, usersStartDate, title);

        Settings settings = Optional.ofNullable(mapper.treeToValue(telegramAccount.getSettings(), Settings.class))
                .orElse(new Settings());
        if (Objects.isNull(settings.getTimeZone())) {
            settings.setTimeZone(dataCache.getUsersCurrentZone(userId));
        }
        settings.getSettings().add(dto);
        telegramAccount.setSettings(jsonMappingService.transformToJsonNode(settings));
        telegramAccount.setSubscribed(true);
        accountRepository.save(telegramAccount);

        Notification notification = Notification.mapNotification(telegramAccount, unitLongPair, usersStartDate, title);
        notificationRepository.save(notification);

        dataCache.clearUsersCurrentBotState(userId);
        dataCache.clearUsersCurrentZone(userId);
        replyToUser.setText(String.format(NEW_NOTIFICATION_INFO, title, dateFormatter.format(usersStartDate),
                dateFormatter.format(notification.getNextNotificationDate())));
        return replyKeyboardService.getMainMenuMessage(userId, replyToUser.getText());
    }


    private void saveUserStartDate(Long userId, SendMessage replyToUser, ZonedDateTime startDate, String startDateString) {
        if (Objects.isNull(startDate) && Objects.nonNull(startDateString)) {
            startDate = getZonedDateTimeAndCheck(userId, replyToUser, startDateString);
        }
        if (Objects.isNull(startDate)) {
            return;
        }
        dataCache.setUsersStartDate(userId, startDate);
        dataCache.setUsersCurrentBotState(userId, BotState.WAITING_FOR_DURATION);
        replyToUser.setText(DURATION_WAITING_MESSAGE);
    }

    private ZonedDateTime getZonedDateTimeAndCheck(Long userId, SendMessage replyToUser, String startDateString) {
        try {
            LocalDateTime startDateLocal = LocalDateTime.from(dateFormatter.parse(startDateString));
            ZonedDateTime startDate = ZonedDateTime.of(startDateLocal, dataCache.getUsersCurrentZone(userId));
            Preconditions.checkState(ZonedDateTime.now().isBefore(startDate));
            return startDate;
        } catch (DateTimeParseException exception) {
            replyToUser.setText(START_DATE_WARNING_MESSAGE);
            return null;
        } catch (IllegalStateException exception) {
            replyToUser.setText(START_DATE_ILLEGAL_WARNING_MESSAGE);
            return null;
        }
    }

    @Override
    public BotState getHandlerName() {
        return BotState.SUBSCRIBE;
    }

}
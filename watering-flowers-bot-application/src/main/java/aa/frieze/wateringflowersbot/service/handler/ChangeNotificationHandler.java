package aa.frieze.wateringflowersbot.service.handler;

import aa.frieze.wateringflowersbot.domain.Notification;
import aa.frieze.wateringflowersbot.domain.TelegramAccount;
import aa.frieze.wateringflowersbot.domain.dto.Settings;
import aa.frieze.wateringflowersbot.domain.enumeration.BotState;
import aa.frieze.wateringflowersbot.error.AppException;
import aa.frieze.wateringflowersbot.error.BusinessErrorEnum;
import aa.frieze.wateringflowersbot.repository.NotificationRepository;
import aa.frieze.wateringflowersbot.repository.TelegramAccountRepository;
import aa.frieze.wateringflowersbot.service.DataCache;
import aa.frieze.wateringflowersbot.service.ReplyKeyboardService;
import aa.frieze.wateringflowersbot.service.impl.AbstractTelegramCallbackBot;
import aa.frieze.wateringflowersbot.service.json.JsonMappingService;
import aa.frieze.wateringflowersbot.service.util.UnitParser;
import aa.frieze.wateringflowersbot.service.util.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static aa.frieze.wateringflowersbot.service.util.Constants.CHANGE_DURATION_BUTTON;
import static aa.frieze.wateringflowersbot.service.util.Constants.CHANGE_NEXT_NOTE_DATE_BUTTON;
import static aa.frieze.wateringflowersbot.service.util.Constants.CHANGE_TITLE_BUTTON;
import static aa.frieze.wateringflowersbot.service.util.Constants.DURATION_WAITING_MESSAGE;
import static aa.frieze.wateringflowersbot.service.util.Constants.DURATION_WARNING_MESSAGE;
import static aa.frieze.wateringflowersbot.service.util.Constants.NEW_NOTIFICATION_INFO;
import static aa.frieze.wateringflowersbot.service.util.Constants.NOTIFICATIONS_NOT_FOUND_MESSAGE;
import static aa.frieze.wateringflowersbot.service.util.Constants.SELECT_ACTION_MESSAGE;
import static aa.frieze.wateringflowersbot.service.util.Constants.SELECT_NOTIFICATION_MESSAGE;
import static aa.frieze.wateringflowersbot.service.util.Constants.START_DATE_CUSTOM_MESSAGE;
import static aa.frieze.wateringflowersbot.service.util.Constants.SUCCESS_CHANGING_MESSAGE;
import static aa.frieze.wateringflowersbot.service.util.Constants.TITLE_WAITING_MESSAGE;
import static aa.frieze.wateringflowersbot.service.util.Constants.TITLE_WARNING_MESSAGE;
import static aa.frieze.wateringflowersbot.service.util.Constants.TRY_AGAIN_MESSAGE;
import static aa.frieze.wateringflowersbot.service.util.Constants.dateFormatter;

@Service
@RequiredArgsConstructor
public class ChangeNotificationHandler implements InputMessageHandler {

    private final ObjectMapper mapper;
    private final UnitParser unitParser;

    private final DataCache dataCache;
    private final ReplyKeyboardService replyKeyboardService;
    private final JsonMappingService jsonMappingService;

    private final TelegramAccountRepository accountRepository;
    private final NotificationRepository notificationRepository;

    @Override
    public SendMessage handle(Message message, TelegramAccount telegramAccount, AbstractTelegramCallbackBot bot) {
        if (BotState.SHOW_MAIN_MENU.equals(dataCache.getUsersCurrentBotState(message.getFrom().getId()))) {
            dataCache.setUsersCurrentBotState(message.getFrom().getId(), BotState.CHANGE_NOTIFICATION);
        }
        return processUsersInput(message, bot, telegramAccount);
    }

    private SendMessage processUsersInput(Message inputMsg, AbstractTelegramCallbackBot bot,
                                          TelegramAccount telegramAccount) {
        Long userId = inputMsg.getFrom().getId();
        Long chatId = inputMsg.getChatId();
        SendMessage replyToUser = new SendMessage(String.valueOf(chatId), TRY_AGAIN_MESSAGE);
        BotState botState = dataCache.getUsersCurrentBotState(userId);

        return switch (botState) {
            case CHANGE_NOTIFICATION -> returnNotificationForChanging(chatId, userId, telegramAccount);
            case SELECT_NOTIFICATION -> returnAction(chatId, userId, inputMsg);
            case CHANGE_TITLE -> changeTitle(chatId, userId, replyToUser, inputMsg, telegramAccount);
            case CHANGE_NEXT_DATE -> changeNextDate(chatId, userId, replyToUser, inputMsg, telegramAccount);
            case CHANGE_DURATION -> changeDuration(chatId, userId, replyToUser, inputMsg, telegramAccount);
            default -> replyKeyboardService.getMainMenuMessage(chatId, replyToUser.getText());
        };
    }

    @SneakyThrows
    private SendMessage returnNotificationForChanging(Long chatId, Long userId, TelegramAccount telegramAccount) {
        List<Notification> notifications = telegramAccount.getNotifications();

        String text = SELECT_NOTIFICATION_MESSAGE;
        if (CollectionUtils.isEmpty(notifications)) {
            text = NOTIFICATIONS_NOT_FOUND_MESSAGE;
        }

        List<String> titles = notifications.stream().map(Notification::getTitle).toList();

        dataCache.setUsersCurrentBotState(userId, BotState.SELECT_NOTIFICATION);
        return replyKeyboardService.getSelectNotificationMessage(chatId, text, titles);
    }

    @SneakyThrows
    private SendMessage returnAction(Long chatId, Long userId, Message inputMsg) {
        dataCache.setUsersCurrentTitle(userId, inputMsg.getText());

        return replyKeyboardService.getSelectActionMessage(chatId, SELECT_ACTION_MESSAGE);
    }

    @SneakyThrows
    private SendMessage changeTitle(Long userId, Long chatId, SendMessage replyToUser, Message inputMsg,
                                    TelegramAccount telegramAccount) {
        String newTitle = inputMsg.getText();
        if (CHANGE_TITLE_BUTTON.equals(newTitle)) {
            replyToUser.setText(TITLE_WAITING_MESSAGE);
            return replyToUser;
        }

        Settings settings = mapper.treeToValue(telegramAccount.getSettings(), Settings.class);
        if (Objects.nonNull(settings) && Objects.nonNull(settings.getSettings())
                && settings.getSettings().stream()
                .map(Settings.SettingDto::getTitle)
                .anyMatch(title -> title.equals(newTitle))) {
            replyToUser.setText(TITLE_WARNING_MESSAGE);
            return replyToUser;
        }

        String oldTitle = dataCache.getUsersCurrentTitle(userId);

        Settings.SettingDto dto = settings.getSettings().stream()
                .filter(settingDto -> oldTitle.equals(settingDto.getTitle()))
                .findFirst()
                .orElse(new Settings.SettingDto());
        dto.setTitle(newTitle);
        telegramAccount.setSettings(jsonMappingService.transformToJsonNode(settings));
        accountRepository.save(telegramAccount);

        Notification notification = notificationRepository.findByTitleAndTelegramAccountId(oldTitle,
                        telegramAccount.getId()).orElseThrow(BusinessErrorEnum.L999::thr);
        notification.setTitle(newTitle);
        notificationRepository.save(notification);

        dataCache.clearUsersCurrentBotState(userId);

        return replyKeyboardService.getMainMenuMessage(chatId, SUCCESS_CHANGING_MESSAGE);
    }
    @SneakyThrows
    private SendMessage changeNextDate(Long userId, Long chatId, SendMessage replyToUser, Message inputMsg,
                                    TelegramAccount telegramAccount) {
        String newDate = inputMsg.getText();
        if (CHANGE_NEXT_NOTE_DATE_BUTTON.equals(newDate)) {
            replyToUser.setText(START_DATE_CUSTOM_MESSAGE);
            return replyToUser;
        }

        ZonedDateTime startDate = Utils.getZonedDateTimeAndCheck(replyToUser, newDate,
                dataCache.getUsersCurrentZone(userId));
        if (Objects.isNull(startDate)) {
            return replyToUser;
        }

        String oldTitle = dataCache.getUsersCurrentTitle(userId);
        Notification notification = notificationRepository.findByTitleAndTelegramAccountId(oldTitle,
                        telegramAccount.getId()).orElseThrow(BusinessErrorEnum.L999::thr);
        notification.setNextNotificationDate(startDate);
        notificationRepository.save(notification);

        dataCache.clearUsersCurrentBotState(userId);
        return replyKeyboardService.getMainMenuMessage(chatId, SUCCESS_CHANGING_MESSAGE);
    }

    @SneakyThrows
    private SendMessage changeDuration(Long userId, Long chatId, SendMessage replyToUser, Message inputMsg,
                                       TelegramAccount telegramAccount) {
        String durationText = inputMsg.getText();
        if (CHANGE_DURATION_BUTTON.equals(durationText)) {
            replyToUser.setText(DURATION_WAITING_MESSAGE);
            return replyToUser;
        }
        Pair<ChronoUnit, Long> unitLongPair;
        try {
            unitLongPair = unitParser.parsePeriodFromString(durationText);
        } catch (AppException exception) {
            replyToUser.setText(DURATION_WARNING_MESSAGE);
            return replyToUser;
        }

        String oldTitle = dataCache.getUsersCurrentTitle(userId);

        Settings settings = mapper.treeToValue(telegramAccount.getSettings(), Settings.class);
        Settings.SettingDto dto = settings.getSettings().stream()
                .filter(settingDto -> oldTitle.equals(settingDto.getTitle()))
                .findFirst()
                .orElse(new Settings.SettingDto());
        dto.setPeriodUnit(unitLongPair.getLeft());
        dto.setPeriodValue(unitLongPair.getValue());
        telegramAccount.setSettings(jsonMappingService.transformToJsonNode(settings));
        accountRepository.save(telegramAccount);

        Notification notification = notificationRepository.findByTitleAndTelegramAccountId(oldTitle,
                telegramAccount.getId()).orElseThrow(BusinessErrorEnum.L999::thr);

        ZonedDateTime usersStartDate = notification.getLastNotificationDate();
        notification.setNextNotificationDate(usersStartDate.plus(unitLongPair.getRight(), unitLongPair.getLeft()));

        // settings.getSettings().add(dto);
        telegramAccount.setSettings(jsonMappingService.transformToJsonNode(settings));
        telegramAccount.setSubscribed(true);
        accountRepository.save(telegramAccount);

        notificationRepository.save(notification);

        dataCache.clearUsersCurrentBotState(userId);
        return replyKeyboardService.getMainMenuMessage(chatId, SUCCESS_CHANGING_MESSAGE);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.CHANGE_NOTIFICATION;
    }

}

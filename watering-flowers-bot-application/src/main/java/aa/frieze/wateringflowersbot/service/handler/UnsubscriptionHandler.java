package aa.frieze.wateringflowersbot.service.handler;

import aa.frieze.wateringflowersbot.domain.Notification;
import aa.frieze.wateringflowersbot.domain.TelegramAccount;
import aa.frieze.wateringflowersbot.domain.dto.Settings;
import aa.frieze.wateringflowersbot.domain.enumeration.BotState;
import aa.frieze.wateringflowersbot.domain.enumeration.CallbackQueryType;
import aa.frieze.wateringflowersbot.repository.NotificationRepository;
import aa.frieze.wateringflowersbot.repository.TelegramAccountRepository;
import aa.frieze.wateringflowersbot.service.DataCache;
import aa.frieze.wateringflowersbot.service.NotificationService;
import aa.frieze.wateringflowersbot.service.ReplyKeyboardService;
import aa.frieze.wateringflowersbot.service.impl.AbstractTelegramCallbackBot;
import aa.frieze.wateringflowersbot.service.json.JsonMappingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static aa.frieze.wateringflowersbot.service.util.Constants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UnsubscriptionHandler implements InputMessageHandler {

    private final DataCache dataCache;
    private final ReplyKeyboardService replyKeyboardService;

    private final ObjectMapper mapper;
    private final JsonMappingService jsonMappingService;
    private final NotificationService notificationService;

    private final TelegramAccountRepository accountRepository;
    private final NotificationRepository notificationRepository;

    @Override
    public SendMessage handle(Message message, TelegramAccount telegramAccount, AbstractTelegramCallbackBot bot) {
        if (BotState.SHOW_MAIN_MENU.equals(dataCache.getUsersCurrentBotState(message.getFrom().getId()))) {
            dataCache.setUsersCurrentBotState(message.getFrom().getId(), BotState.UNSUBSCRIBE);
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
            case UNSUBSCRIBE -> {
                return replyKeyboardService.getUnsubscribeMenuMessage(chatId, UNSUBSCRIPTION_MENU_MESSAGE);
            }
            case UNSUBSCRIBE_ALL -> {
                return processUnsubscribeAll(chatId, telegramAccount);
            }
            case UNSUBSCRIBE_CUSTOM -> {
                return processUnsubscribeCustom(chatId, telegramAccount, bot);
            }
            default -> {
                return replyKeyboardService.getMainMenuMessage(chatId, replyToUser.getText());
            }
        }
    }

    @SneakyThrows
    @Transactional
    protected SendMessage processUnsubscribeAll(Long chatId, TelegramAccount telegramAccount) {
        dataCache.clearUsersCurrentBotState(chatId);

        Settings settings = mapper.treeToValue(telegramAccount.getSettings(), Settings.class);
        if (Objects.isNull(settings) || CollectionUtils.isEmpty(settings.getSettings())) {
            return replyKeyboardService.getMainMenuMessage(chatId, ACTUAL_NOTIFICATIONS_NOT_FOUND_MESSAGE);
        }

        List<Notification> notifications = telegramAccount.getNotifications();
        notifications.forEach(notification -> notification.setArchived(true));
        notificationRepository.saveAll(notifications);

        settings.setSettings(Collections.emptyList());
        telegramAccount.setSettings(jsonMappingService.transformToJsonNode(settings));
        telegramAccount.setSubscribed(false);
        accountRepository.save(telegramAccount);

        return replyKeyboardService.getMainMenuMessage(chatId, UNSUBSCRIBE_SUCCESS_MESSAGE);
    }

    protected SendMessage processUnsubscribeCustom(Long chatId, TelegramAccount telegramAccount,
                                                   AbstractTelegramCallbackBot bot) {
        List<Notification> notifications = telegramAccount.getNotifications();
        String text = MAIN_MENU_MESSAGE;
        if (CollectionUtils.isEmpty(notifications)) {
            text = NOTIFICATIONS_NOT_FOUND_MESSAGE;
        }
        for (Notification notification : notifications) {
            bot.sendInlineKeyBoardMessage(chatId, notificationService.getActualNotificationInfo(notification),
                    Map.of(UNSUBSCRIBE_ONE_BUTTON, CallbackQueryType.UNSUBSCRIBE.name()));
        }

        dataCache.clearUsersCurrentBotState(chatId);
        return replyKeyboardService.getMainMenuMessage(chatId, text);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.UNSUBSCRIBE;
    }

}

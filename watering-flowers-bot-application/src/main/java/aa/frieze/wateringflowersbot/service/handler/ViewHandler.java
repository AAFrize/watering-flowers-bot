package aa.frieze.wateringflowersbot.service.handler;

import aa.frieze.wateringflowersbot.domain.Notification;
import aa.frieze.wateringflowersbot.domain.TelegramAccount;
import aa.frieze.wateringflowersbot.domain.enumeration.BotState;
import aa.frieze.wateringflowersbot.repository.NotificationRepository;
import aa.frieze.wateringflowersbot.service.DataCache;
import aa.frieze.wateringflowersbot.service.NotificationService;
import aa.frieze.wateringflowersbot.service.ReplyKeyboardService;
import aa.frieze.wateringflowersbot.service.impl.AbstractTelegramCallbackBot;
import aa.frieze.wateringflowersbot.service.util.Utils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static aa.frieze.wateringflowersbot.service.util.Constants.*;

@Service
@RequiredArgsConstructor
public class ViewHandler implements InputMessageHandler {

    private final ObjectMapper mapper;

    private final DataCache dataCache;
    private final ReplyKeyboardService replyKeyboardService;
    private final NotificationService notificationService;

    private final NotificationRepository notificationRepository;

    @Override
    public SendMessage handle(Message message, TelegramAccount telegramAccount, AbstractTelegramCallbackBot bot) {
        if (BotState.SHOW_MAIN_MENU.equals(dataCache.getUsersCurrentBotState(message.getFrom().getId()))) {
            dataCache.setUsersCurrentBotState(message.getFrom().getId(), BotState.VIEW);
        }
        return processUsersInput(message, telegramAccount);
    }

    private SendMessage processUsersInput(Message inputMsg, TelegramAccount telegramAccount) {
        Long userId = inputMsg.getFrom().getId();
        Long chatId = inputMsg.getChatId();
        SendMessage replyToUser = new SendMessage(String.valueOf(chatId), TRY_AGAIN_MESSAGE);
        BotState botState = dataCache.getUsersCurrentBotState(userId);

        return switch (botState) {
            case VIEW -> replyKeyboardService.getViewMenuMessage(chatId, VIEW_MENU_MESSAGE);
            case VIEW_ALL -> processView(chatId, telegramAccount, false);
            case VIEW_ACTUAL -> processView(chatId, telegramAccount, true);
            default -> replyKeyboardService.getMainMenuMessage(chatId, replyToUser.getText());
        };
    }

    @SneakyThrows
    private SendMessage processView(Long chatId, TelegramAccount telegramAccount, boolean actual) {
        dataCache.clearUsersCurrentBotState(chatId);
        List<Notification> notifications = actual ? telegramAccount.getNotifications()
                : notificationRepository.findAllByTelegramAccountId(telegramAccount.getId());

        String text = null;
        if (CollectionUtils.isEmpty(notifications)) {
            text = NOTIFICATIONS_NOT_FOUND_MESSAGE;
        }

        JsonNode timeZone = Utils.safeGet(telegramAccount.getSettings(), jsonNode -> jsonNode.findValue("timeZone"));
        ZoneId zoneId = mapper.treeToValue(timeZone, ZoneId.class);

        text = Objects.isNull(text) ? notifications.stream().map(notification -> actual
                ? notificationService.getActualNotificationInfo(notification, zoneId)
                : notificationService.getNotificationInfo(notification, zoneId))
                .collect(Collectors.joining("\n\n")) : text;

        return replyKeyboardService.getMainMenuMessage(chatId, text);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.VIEW;
    }

}

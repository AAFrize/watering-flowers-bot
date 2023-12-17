package aa.frieze.wateringflowersbot.service.impl;

import aa.frieze.wateringflowersbot.domain.TelegramAccount;
import aa.frieze.wateringflowersbot.domain.enumeration.BotState;
import aa.frieze.wateringflowersbot.service.handler.InputMessageHandler;
import aa.frieze.wateringflowersbot.service.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static aa.frieze.wateringflowersbot.domain.enumeration.BotState.CHANGE_DURATION;
import static aa.frieze.wateringflowersbot.domain.enumeration.BotState.CHANGE_NEXT_DATE;
import static aa.frieze.wateringflowersbot.domain.enumeration.BotState.CHANGE_NOTIFICATION;
import static aa.frieze.wateringflowersbot.domain.enumeration.BotState.CHANGE_TITLE;
import static aa.frieze.wateringflowersbot.domain.enumeration.BotState.SELECT_NOTIFICATION;
import static aa.frieze.wateringflowersbot.domain.enumeration.BotState.SUBSCRIBE;
import static aa.frieze.wateringflowersbot.domain.enumeration.BotState.UNSUBSCRIBE;
import static aa.frieze.wateringflowersbot.domain.enumeration.BotState.UNSUBSCRIBE_ALL;
import static aa.frieze.wateringflowersbot.domain.enumeration.BotState.UNSUBSCRIBE_CUSTOM;
import static aa.frieze.wateringflowersbot.domain.enumeration.BotState.VIEW;
import static aa.frieze.wateringflowersbot.domain.enumeration.BotState.VIEW_ACTUAL;
import static aa.frieze.wateringflowersbot.domain.enumeration.BotState.VIEW_ALL;
import static aa.frieze.wateringflowersbot.domain.enumeration.BotState.WAITING_FOR_DURATION;
import static aa.frieze.wateringflowersbot.domain.enumeration.BotState.WAITING_FOR_START_DATE;
import static aa.frieze.wateringflowersbot.domain.enumeration.BotState.WAITING_FOR_TIMEZONE;
import static aa.frieze.wateringflowersbot.domain.enumeration.BotState.WAITING_FOR_TITLE;
import static aa.frieze.wateringflowersbot.service.util.Constants.HANDLER_NOT_FOUND_MESSAGE;

@Slf4j
@Service
public class BotStateContext {

    private final Map<BotState, InputMessageHandler> messageHandlers = new HashMap<>();
    private final Map<BotState, BotState> commonStates = new HashMap<>();

    public BotStateContext(List<InputMessageHandler> messageHandlers) {
        messageHandlers.forEach(handler -> this.messageHandlers.put(handler.getHandlerName(), handler));

        // subscriptions states
        commonStates.put(WAITING_FOR_TITLE, SUBSCRIBE);
        commonStates.put(WAITING_FOR_TIMEZONE, SUBSCRIBE);
        commonStates.put(WAITING_FOR_START_DATE, SUBSCRIBE);
        commonStates.put(WAITING_FOR_DURATION, SUBSCRIBE);
        // unsubscriptions states
        commonStates.put(UNSUBSCRIBE_ALL, UNSUBSCRIBE);
        commonStates.put(UNSUBSCRIBE_CUSTOM, UNSUBSCRIBE);
        // view states
        commonStates.put(VIEW_ALL, VIEW);
        commonStates.put(VIEW_ACTUAL, VIEW);
        // change states
        commonStates.put(SELECT_NOTIFICATION, CHANGE_NOTIFICATION);
        commonStates.put(CHANGE_TITLE, CHANGE_NOTIFICATION);
        commonStates.put(CHANGE_NEXT_DATE, CHANGE_NOTIFICATION);
        commonStates.put(CHANGE_DURATION, CHANGE_NOTIFICATION);
    }

    public SendMessage processInputMessage(BotState currentState, Message message,
                                           TelegramAccount telegramAccount, AbstractTelegramCallbackBot bot) {
        InputMessageHandler currentMessageHandler = findMessageHandler(currentState);
        if (Objects.isNull(currentMessageHandler)) {
            SendMessage response = new SendMessage();
            response.setChatId(message.getChatId());
            response.setText(HANDLER_NOT_FOUND_MESSAGE);
            log.error("Handler not found. Bot state: {}. Input message: {}. Telegram account: {}",
                    currentState, message.getText(), Utils.safeGet(telegramAccount, TelegramAccount::getChatId));
            return response;
        }
        return currentMessageHandler.handle(message, telegramAccount, bot);
    }

    private InputMessageHandler findMessageHandler(BotState currentState) {
        return messageHandlers.get(commonStates.getOrDefault(currentState, currentState));
    }

}

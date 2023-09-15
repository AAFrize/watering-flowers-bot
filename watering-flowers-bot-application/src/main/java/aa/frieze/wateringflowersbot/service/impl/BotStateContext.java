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

import static aa.frieze.wateringflowersbot.service.util.Constants.HANDLER_NOT_FOUND_MESSAGE;

@Slf4j
@Service
public class BotStateContext {

    private final Map<BotState, InputMessageHandler> messageHandlers = new HashMap<>();

    public BotStateContext(List<InputMessageHandler> messageHandlers) {
        messageHandlers.forEach(handler -> this.messageHandlers.put(handler.getHandlerName(), handler));
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
        if (isSubscriptionState(currentState)) {
            return messageHandlers.get(BotState.SUBSCRIBE);
        }
        if (isUnsubscriptionState(currentState)) {
            return messageHandlers.get(BotState.UNSUBSCRIBE);
        }
        if (isViewState(currentState)) {
            return messageHandlers.get(BotState.VIEW);
        }
        return messageHandlers.get(currentState);
    }

    private boolean isSubscriptionState(BotState currentState) {
        return switch (currentState) {
            case SUBSCRIBE,
                    WAITING_FOR_TITLE,
                    WAITING_FOR_TIMEZONE,
                    WAITING_FOR_START_DATE,
                    WAITING_FOR_DURATION -> true;
            default -> false;
        };
    }

    private boolean isUnsubscriptionState(BotState currentState) {
        return switch (currentState) {
            case UNSUBSCRIBE,
                    UNSUBSCRIBE_ALL,
                    UNSUBSCRIBE_CUSTOM -> true;
            default -> false;
        };
    }

    private boolean isViewState(BotState currentState) {
        return switch (currentState) {
            case VIEW,
                    VIEW_ALL,
                    VIEW_ACTUAL -> true;
            default -> false;
        };
    }

}

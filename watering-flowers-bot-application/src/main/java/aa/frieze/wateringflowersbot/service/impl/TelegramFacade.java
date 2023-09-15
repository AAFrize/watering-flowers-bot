package aa.frieze.wateringflowersbot.service.impl;

import aa.frieze.wateringflowersbot.domain.TelegramAccount;
import aa.frieze.wateringflowersbot.domain.enumeration.BotState;
import aa.frieze.wateringflowersbot.repository.TelegramAccountRepository;
import aa.frieze.wateringflowersbot.service.DataCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Objects;

import static aa.frieze.wateringflowersbot.service.util.Constants.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramFacade {

    private final DataCache userDataCache;
    private final BotStateContext botStateContext;
    private final CallbackQueryFacade callbackQueryFacade;
    private final TelegramAccountRepository telegramAccountRepository;

    public SendMessage handleUpdate(Update update, AbstractTelegramCallbackBot bot) {
        SendMessage replyMessage = null;

        if (update.hasCallbackQuery()) {
            log.info("New callbackQuery from User: {} with data: {}", update.getCallbackQuery().getFrom().getUserName(),
                update.getCallbackQuery().getData());
            return callbackQueryFacade.processCallbackQuery(update.getCallbackQuery());
        }

        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            log.info("New message from User:{}, chatId: {},  with text: {}",
                message.getFrom().getUserName(), message.getChatId(), message.getText());
            replyMessage = handleInputMessage(message, bot);
        }

        if (Objects.nonNull(replyMessage)) {
            replyMessage.enableMarkdown(true);
        }

        return replyMessage;
    }

    private SendMessage handleInputMessage(Message message, AbstractTelegramCallbackBot bot) {
        String inputMsg = message.getText();
        long userId = message.getFrom().getId();
        BotState botState;
        SendMessage replyMessage;

        switch (inputMsg) {
            case SUBSCRIBE_BUTTON -> botState = BotState.SUBSCRIBE;
            case UNSUBSCRIBE_BUTTON -> botState = BotState.UNSUBSCRIBE;
            case VIEW_INFO_BUTTON -> botState = BotState.VIEW;
            case UNSUBSCRIBE_ALL_BUTTON -> botState = BotState.UNSUBSCRIBE_ALL;
            case UNSUBSCRIBE_CUSTOM_BUTTON -> botState = BotState.UNSUBSCRIBE_CUSTOM;
            case VIEW_ACTUAL_INFO_BUTTON -> botState = BotState.VIEW_ACTUAL;
            case VIEW_ALL_INFO_BUTTON -> botState = BotState.VIEW_ALL;
            default -> botState = userDataCache.getUsersCurrentBotState(userId);
        }

        userDataCache.setUsersCurrentBotState(userId, botState);
        TelegramAccount telegramAccount = telegramAccountRepository
            .findByChatId(message.getChatId()).orElse(null);
        if (Objects.isNull(telegramAccount)) {
            replyMessage = new SendMessage(String.valueOf(message.getChatId()), INCORRECT_TG_ACCOUNT_MESSAGE);
        } else {
            replyMessage = botStateContext.processInputMessage(botState, message, telegramAccount, bot);
        }
        return replyMessage;
    }

}

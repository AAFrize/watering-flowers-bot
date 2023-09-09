package aa.frieze.wateringflowersbot.service.handler;

import aa.frieze.wateringflowersbot.domain.TelegramAccount;
import aa.frieze.wateringflowersbot.domain.dto.Settings;
import aa.frieze.wateringflowersbot.domain.enumeration.BotState;
import aa.frieze.wateringflowersbot.service.DataCache;
import aa.frieze.wateringflowersbot.service.ReplyKeyboardService;
import aa.frieze.wateringflowersbot.service.impl.AbstractTelegramCallbackBot;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Objects;

import static aa.frieze.wateringflowersbot.service.util.Constants.TITLE_WAITING_MESSAGE;
import static aa.frieze.wateringflowersbot.service.util.Constants.TITLE_WARNING_MESSAGE;
import static aa.frieze.wateringflowersbot.service.util.Constants.TRY_AGAIN_MESSAGE;

@Slf4j
@Service
@RequiredArgsConstructor
public abstract class SubscriptionHandler implements InputMessageHandler {

    private final DataCache dataCache;
    private final ReplyKeyboardService replyKeyboardService;

    private final ObjectMapper mapper;

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

        if (BotState.SUBSCRIBE.equals(botState)) {
            return processAskTitle(chatId, replyToUser);
        }
        return replyKeyboardService.getMainMenuMessage(chatId, replyToUser.getText());
    }

    private SendMessage processAskTitle(Long userId, SendMessage replyToUser) {
        dataCache.setUsersCurrentBotState(userId, BotState.WAITING_FOR_TITLE);
        replyToUser.setText(TITLE_WAITING_MESSAGE);
        return replyToUser;
    }

    @SneakyThrows
    private SendMessage processReceiveTitleAndAskStartDate(Long userId, SendMessage replyToUser, Message inputMsg,
                                                           TelegramAccount telegramAccount) {
        Settings settings = mapper.treeToValue(telegramAccount.getSettings(), Settings.class);
        if (Objects.nonNull(settings) && Objects.nonNull(settings.getSettings())
                && settings.getSettings().stream()
                .map(Settings.SettingDto::getTitle)
                .anyMatch(title -> title.equals(inputMsg.getText()))) {
            replyToUser.setText(TITLE_WARNING_MESSAGE);
            return replyToUser;
        }

        dataCache.setUsersCurrentBotState(userId, BotState.WAITING_FOR_TITLE);
        dataCache.setUsersCurrentTitle(userId, inputMsg.getText());
        // todo ask start date
        return replyToUser;
    }

    @Override
    public BotState getHandlerName() {
        return BotState.SUBSCRIBE;
    }

}

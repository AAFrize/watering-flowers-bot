package aa.frieze.wateringflowersbot.service.handler;

import aa.frieze.wateringflowersbot.domain.TelegramAccount;
import aa.frieze.wateringflowersbot.domain.dto.Settings;
import aa.frieze.wateringflowersbot.domain.enumeration.BotState;
import aa.frieze.wateringflowersbot.repository.TelegramAccountRepository;
import aa.frieze.wateringflowersbot.service.DataCache;
import aa.frieze.wateringflowersbot.service.ReplyKeyboardService;
import aa.frieze.wateringflowersbot.service.impl.AbstractTelegramCallbackBot;
import aa.frieze.wateringflowersbot.service.json.JsonMappingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.DateTimeException;
import java.time.ZoneId;
import java.util.Optional;

import static aa.frieze.wateringflowersbot.service.util.Constants.*;

@Service
@RequiredArgsConstructor
public class ChangeTimezoneHandler implements InputMessageHandler {

    private final ObjectMapper mapper;
    private final JsonMappingService jsonMappingService;

    private final DataCache dataCache;
    private final ReplyKeyboardService replyKeyboardService;

    private final TelegramAccountRepository accountRepository;

    @Override
    @SneakyThrows
    public SendMessage handle(Message message, TelegramAccount telegramAccount, AbstractTelegramCallbackBot bot) {
        if (BotState.SHOW_MAIN_MENU.equals(dataCache.getUsersCurrentBotState(message.getFrom().getId()))) {
            dataCache.setUsersCurrentBotState(message.getFrom().getId(), BotState.SUBSCRIBE);
        }

        Long chatId = message.getChatId();
        Long userId = message.getFrom().getId();
        SendMessage replyToUser = new SendMessage(String.valueOf(chatId), TRY_AGAIN_MESSAGE);

        if (CHANGE_TIMEZONE_BUTTON.equals(message.getText())) {
            replyToUser.setText(TIMEZONE_WAITING_MESSAGE);
            return replyToUser;
        }

        ZoneId inputZone;
        try {
            inputZone = ZoneId.of(message.getText());
        } catch (DateTimeException exception) {
            replyToUser.setText(TIMEZONE_WARNING_MESSAGE);
            return replyToUser;
        }

        Settings settings = Optional.ofNullable(mapper.treeToValue(telegramAccount.getSettings(), Settings.class))
                .orElse(new Settings());
        settings.setTimeZone(inputZone);

        telegramAccount.setSettings(jsonMappingService.transformToJsonNode(settings));
        accountRepository.save(telegramAccount);

        dataCache.clearUsersCurrentBotState(userId);
        return replyKeyboardService.getMainMenuMessage(chatId,
                String.format(TIMEZONE_CHANGING_SUCCESS_MESSAGE, inputZone));
    }

    @Override
    public BotState getHandlerName() {
        return BotState.CHANGE_TIMEZONE;
    }
}

package aa.frieze.wateringflowersbot.service.callback;

import aa.frieze.wateringflowersbot.domain.Notification;
import aa.frieze.wateringflowersbot.domain.TelegramAccount;
import aa.frieze.wateringflowersbot.domain.dto.Settings;
import aa.frieze.wateringflowersbot.domain.enumeration.CallbackQueryType;
import aa.frieze.wateringflowersbot.error.BusinessErrorEnum;
import aa.frieze.wateringflowersbot.repository.NotificationRepository;
import aa.frieze.wateringflowersbot.repository.TelegramAccountRepository;
import aa.frieze.wateringflowersbot.service.CallbackQueryHandler;
import aa.frieze.wateringflowersbot.service.ReplyKeyboardService;
import aa.frieze.wateringflowersbot.service.json.JsonMappingService;
import aa.frieze.wateringflowersbot.service.util.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Service
@RequiredArgsConstructor
public class UnsubscribeCallbackHandler implements CallbackQueryHandler {

    private final ObjectMapper mapper;

    private final ReplyKeyboardService replyKeyboardService;
    private final JsonMappingService jsonMappingService;

    private final TelegramAccountRepository accountRepository;
    private final NotificationRepository notificationRepository;

    @Override
    @SneakyThrows
    @Transactional
    public SendMessage handleCallbackQuery(CallbackQuery callbackQuery) {
        String text = callbackQuery.getMessage().getText();
        // fixme mb if title contains this chars
        String title = text.substring(text.indexOf("*") + 1, text.indexOf("*:\n"));
        Long chatId = callbackQuery.getMessage().getChatId();

        TelegramAccount account = accountRepository.findByChatId(chatId).orElseThrow(BusinessErrorEnum.L999::thr);
        Settings settings = mapper.treeToValue(account.getSettings(), Settings.class);
        settings.getSettings().removeIf(dto -> title.equals(dto.getTitle()));
        account.setSettings(jsonMappingService.transformToJsonNode(settings));

        if (CollectionUtils.isEmpty(settings.getSettings())) {
            account.setSubscribed(false);
        }

        accountRepository.save(account);

        Notification notification = account.getNotifications().stream()
                .filter(n -> title.equals(n.getTitle()))
                .findFirst()
                .orElseThrow(BusinessErrorEnum.L999::thr);
        notification.setArchived(true);
        notificationRepository.save(notification);

        return replyKeyboardService.getMainMenuMessage(callbackQuery.getFrom().getId(),
                String.format(Constants.UNSUBSCRIBE_SUCCESS_CUSTOM_MESSAGE, title));
    }

    @Override
    public CallbackQueryType getHandlerQueryType() {
        return CallbackQueryType.UNSUBSCRIBE;
    }
}

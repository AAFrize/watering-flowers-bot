package aa.frieze.wateringflowersbot.service.impl;

import aa.frieze.wateringflowersbot.domain.enumeration.CallbackQueryType;
import aa.frieze.wateringflowersbot.service.CallbackQueryHandler;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.List;
import java.util.Optional;

import static aa.frieze.wateringflowersbot.service.util.Constants.HANDLER_NOT_FOUND_MESSAGE;

@Service
public class CallbackQueryFacade {
    private final List<CallbackQueryHandler> callbackQueryHandlers;

    public CallbackQueryFacade(List<CallbackQueryHandler> callbackQueryHandlers) {
        this.callbackQueryHandlers = callbackQueryHandlers;
    }

    public SendMessage processCallbackQuery(CallbackQuery usersQuery) {
        CallbackQueryType usersQueryType = CallbackQueryType.valueOf(usersQuery.getData());

        Optional<CallbackQueryHandler> queryHandler = callbackQueryHandlers.stream()
                .filter(callbackQuery -> callbackQuery.getHandlerQueryType().equals(usersQueryType))
                .findFirst();

        return queryHandler.map(handler -> handler.handleCallbackQuery(usersQuery))
            .orElse(new SendMessage(usersQuery.getMessage().getChatId().toString(), HANDLER_NOT_FOUND_MESSAGE));
    }
}

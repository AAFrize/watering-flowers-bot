package aa.frieze.wateringflowersbot.service.handler;

import aa.frieze.wateringflowersbot.domain.TelegramAccount;
import aa.frieze.wateringflowersbot.domain.enumeration.BotState;
import aa.frieze.wateringflowersbot.service.impl.AbstractTelegramCallbackBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface InputMessageHandler {
    SendMessage handle(Message message, TelegramAccount telegramAccount, AbstractTelegramCallbackBot bot);
    BotState getHandlerName();
}

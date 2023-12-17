package aa.frieze.wateringflowersbot.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.util.List;

public interface ReplyKeyboardService {
    SendMessage getMainMenuMessage(long chatId, String textMessage);

    SendMessage getUnsubscribeMenuMessage(long chatId, String textMessage);

    SendMessage getViewMenuMessage(long chatId, String textMessage);

    SendMessage getStartDateMessage(long chatId, String textMessage);

    SendMessage getSelectNotificationMessage(long chatId, String textMessage, List<String> buttons);

    SendMessage getSelectActionMessage(long chatId, String textMessage);

    SendMessage createMessageWithKeyboard(long chatId, String textMessage,
                                          ReplyKeyboardMarkup replyKeyboardMarkup);
}

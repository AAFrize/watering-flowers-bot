package aa.frieze.wateringflowersbot.service.impl;

import aa.frieze.wateringflowersbot.service.ReplyKeyboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

import static aa.frieze.wateringflowersbot.service.util.Constants.*;

/**
 * Управляет отображением главного меню в чате.
 */
@Service
@RequiredArgsConstructor
public class ReplyKeyboardServiceImpl implements ReplyKeyboardService {

    @Override
    public SendMessage getMainMenuMessage(long chatId, String textMessage) {
        ReplyKeyboardMarkup replyKeyboardMarkup = getMainMenuKeyboard();

        return createMessageWithKeyboard(chatId, textMessage, replyKeyboardMarkup);
    }

    @Override
    public SendMessage getStartDateMessage(long chatId, String textMessage) {
        ReplyKeyboardMarkup replyKeyboardMarkup = getStartDateReceiveKeyboard();

        return createMessageWithKeyboard(chatId, textMessage, replyKeyboardMarkup);
    }

    private ReplyKeyboardMarkup getMainMenuKeyboard() {

        final ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        KeyboardButton button = KeyboardButton.builder().text(SUBSCRIBE_BUTTON).build();

        row1.add(button);
        keyboard.add(row1);
        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }

    private ReplyKeyboardMarkup getStartDateReceiveKeyboard() {

        final ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardButton button1 = KeyboardButton.builder().text(CURRENT_DATE_BUTTON).build();
        KeyboardButton button2 = KeyboardButton.builder().text(CUSTOM_DATE_BUTTON).build();
        KeyboardRow row1 = new KeyboardRow();
        row1.addAll(List.of(button1, button2));
        keyboard.add(row1);
        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }

    @Override
    public SendMessage createMessageWithKeyboard(long chatId, String textMessage,
                                                 ReplyKeyboardMarkup replyKeyboardMarkup) {
        final SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setText(textMessage);
        if (replyKeyboardMarkup != null) {
            sendMessage.setReplyMarkup(replyKeyboardMarkup);
        } else {
            sendMessage.setText("Данные не найдены");
        }
        return sendMessage;
    }
}

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
 * Управляет отображением меню в чате.
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

    @Override
    public SendMessage getUnsubscribeMenuMessage(long chatId, String textMessage) {
        ReplyKeyboardMarkup replyKeyboardMarkup = getUnsubscriptionKeyboard();

        return createMessageWithKeyboard(chatId, textMessage, replyKeyboardMarkup);
    }

    @Override
    public SendMessage getViewMenuMessage(long chatId, String textMessage) {
        ReplyKeyboardMarkup replyKeyboardMarkup = geViewKeyboard();

        return createMessageWithKeyboard(chatId, textMessage, replyKeyboardMarkup);
    }

    private ReplyKeyboardMarkup getMainMenuKeyboard() {
        return getKeyboardWithOneButtonsInRow(List.of(SUBSCRIBE_BUTTON, UNSUBSCRIBE_BUTTON,
                VIEW_INFO_BUTTON, CHANGE_TIMEZONE_BUTTON));
    }

    private ReplyKeyboardMarkup getStartDateReceiveKeyboard() {
        return getKeyboardWith2ButtonsAnd1Row(CURRENT_DATE_BUTTON, CUSTOM_DATE_BUTTON);
    }

    private ReplyKeyboardMarkup getUnsubscriptionKeyboard() {
        return getKeyboardWithOneButtonsInRow(List.of(UNSUBSCRIBE_ALL_BUTTON, UNSUBSCRIBE_CUSTOM_BUTTON));
    }

    private ReplyKeyboardMarkup geViewKeyboard() {
        return getKeyboardWithOneButtonsInRow(List.of(VIEW_ACTUAL_INFO_BUTTON, VIEW_ALL_INFO_BUTTON));
    }

    private ReplyKeyboardMarkup getKeyboardWithOneButtonsInRow(List<String> buttons) {
        final ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        List<KeyboardRow> keyboardRows = new ArrayList<>();
        for (String button : buttons) {
            KeyboardButton buttonKey = KeyboardButton.builder().text(button).build();
            KeyboardRow row = new KeyboardRow();
            row.add(buttonKey);
            keyboardRows.add(row);
        }

        List<KeyboardRow> keyboard = new ArrayList<>(keyboardRows);
        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }

    private ReplyKeyboardMarkup getKeyboardWith2ButtonsAnd1Row(String firstButton, String secondButton) {
        final ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();

        KeyboardButton button1 = KeyboardButton.builder().text(firstButton).build();
        KeyboardButton button2 = KeyboardButton.builder().text(secondButton).build();
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

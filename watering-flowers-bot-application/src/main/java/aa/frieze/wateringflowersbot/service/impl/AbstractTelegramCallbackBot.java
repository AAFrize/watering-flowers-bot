package aa.frieze.wateringflowersbot.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractTelegramCallbackBot extends TelegramLongPollingCommandBot {

    private final TelegramFacade facade;

    public void sendChangedInlineButtonText(CallbackQuery callbackQuery, String buttonText, String callbackData) {
        final InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        final List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        final long message_id = callbackQuery.getMessage().getMessageId();
        final long chat_id = callbackQuery.getMessage().getChatId();
        final List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        keyboardButtonsRow1.add(InlineKeyboardButton.builder().callbackData(callbackData).text(buttonText).build());
        rowList.add(keyboardButtonsRow1);
        inlineKeyboardMarkup.setKeyboard(rowList);

        EditMessageText editMessageText = EditMessageText.builder().chatId(chat_id).messageId((int) (message_id))
            .text(callbackQuery.getMessage().getText()).build();

        editMessageText.setReplyMarkup(inlineKeyboardMarkup);
        try {
            execute(editMessageText);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    public void sendInlineKeyBoardMessage(long chatId, String messageText, Map<String, String> buttonTextAndCallbackData) {

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        for (Map.Entry<String, String> buttonAndCallback : buttonTextAndCallbackData.entrySet()) {
            List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
            InlineKeyboardButton keyboardButton = InlineKeyboardButton.builder().text(buttonAndCallback.getKey()).build();
            String callbackData = buttonAndCallback.getValue();
            if (callbackData != null) {
                keyboardButton.setCallbackData(callbackData);
            }
            keyboardButtonsRow1.add(keyboardButton);
            rowList.add(keyboardButtonsRow1);
        }
        inlineKeyboardMarkup.setKeyboard(rowList);

        try {
            execute(SendMessage.builder().chatId(chatId).text(messageText).replyMarkup(inlineKeyboardMarkup).build());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        SendMessage answer = facade.handleUpdate(update, this);
        try {
            if (answer.getText().length() > 4000) {
                String answerText = answer.getText();
                for (int i = 0; i <= answerText.length() / 4000; i++) {
                    String partOfText = answerText.substring(i * 4000,
                        Math.min(answerText.length() - 1, (i + 1) * 4000));
                    answer.setText(partOfText);
                    execute(answer);
                }
            } else {
                execute(answer);
            }
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

}

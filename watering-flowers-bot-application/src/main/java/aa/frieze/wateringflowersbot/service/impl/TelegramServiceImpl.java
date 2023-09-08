package aa.frieze.wateringflowersbot.service.impl;

import aa.frieze.wateringflowersbot.repository.TelegramAccountRepository;
import aa.frieze.wateringflowersbot.service.TelegramService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramServiceImpl implements TelegramService {

    private final TelegramBot telegramBot;

    private final TelegramAccountRepository telegramAccountRepository;

    @Override
    public void sendMessage(Long chatId, String text) {
        if (Objects.isNull(chatId)) return;
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        try {
            telegramBot.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.warn("Something went wrong while sending TG message: chatId - {}, message - {}", chatId, text);
        }
    }

}

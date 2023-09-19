package aa.frieze.wateringflowersbot.service.impl;

import aa.frieze.wateringflowersbot.repository.TelegramAccountRepository;
import aa.frieze.wateringflowersbot.service.DataCache;
import aa.frieze.wateringflowersbot.service.commands.StartCommand;
import aa.frieze.wateringflowersbot.service.util.Constants;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;

@Slf4j
@Setter
@Getter
@Service
@EqualsAndHashCode(callSuper = true)
public class TelegramBot extends AbstractTelegramCallbackBot {

    @Value("${telegram-bot.name}")
    private String botUsername;
    @Value("${telegram-bot.token}")
    private String botToken;
    @Value("${telegram-bot.enabled}")
    private Boolean botEnabled;

    private final TelegramBotsApi api;
    private final DataCache dataCache;
    private final TelegramAccountRepository telegramAccountRepository;
    private final TelegramFacade facade;

    public TelegramBot(TelegramFacade facade, TelegramBotsApi api, DataCache dataCache,
                       TelegramAccountRepository telegramAccountRepository) {
        super(facade);
        this.api = api;
        this.dataCache = dataCache;
        this.telegramAccountRepository = telegramAccountRepository;
        this.facade = facade;
    }

    @PostConstruct
    private void register() throws TelegramApiException {
        // todo
        if (BooleanUtils.isTrue(botEnabled)) {
            this.api.registerBot(this);
            register(new StartCommand("start", "Старт", telegramAccountRepository,
                    Constants.START_MESSAGE));
            log.info("Bot is registered");
        }
    }

}

package aa.frieze.wateringflowersbot.service.impl;

import aa.frieze.wateringflowersbot.service.ScheduleService;
import aa.frieze.wateringflowersbot.service.TelegramService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    @Value("${telegram-bot.enabled}")
    private Boolean botEnabled;

    private final TelegramService telegramService;

    @Override
    public void notifyAboutWatering() {
        
    }
}

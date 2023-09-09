package aa.frieze.wateringflowersbot.service.impl;

import aa.frieze.wateringflowersbot.domain.enumeration.BotState;
import aa.frieze.wateringflowersbot.service.DataCache;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class DataCacheImpl implements DataCache {

    private final Map<Long, BotState> usersBotStates = new HashMap<>();
    private final Map<Long, String> usersTitles = new HashMap<>();
    private final Map<Long, ZonedDateTime> usersStartDate = new HashMap<>();

    @Override
    public void setUsersCurrentBotState(Long userId, BotState botState) {
        usersBotStates.put(userId, botState);
    }

    @Override
    public BotState getUsersCurrentBotState(Long userId) {
        BotState botState = usersBotStates.get(userId);
        if (botState == null) {
            botState = BotState.SHOW_MAIN_MENU;
        }

        return botState;
    }

    @Override
    public void setUsersCurrentTitle(Long userId, String title) {
        usersTitles.put(userId, title);
    }

    @Override
    public String getUsersCurrentTitle(Long userId) {
        return usersTitles.get(userId);
    }

}

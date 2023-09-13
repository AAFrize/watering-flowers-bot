package aa.frieze.wateringflowersbot.service.impl;

import aa.frieze.wateringflowersbot.domain.enumeration.BotState;
import aa.frieze.wateringflowersbot.service.DataCache;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class DataCacheImpl implements DataCache {

    private final Map<Long, BotState> usersBotStates = new HashMap<>();
    private final Map<Long, String> usersTitles = new HashMap<>();
    private final Map<Long, ZoneId> usersTimeZone = new HashMap<>();
    private final Map<Long, ZonedDateTime> usersStartDate = new HashMap<>();

    @Override
    public void setUsersCurrentBotState(Long userId, BotState botState) {
        usersBotStates.put(userId, botState);
    }

    @Override
    public void clearUsersCurrentBotState(Long userId) {
        usersBotStates.remove(userId);
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
        return usersTitles.remove(userId);
    }

    @Override
    public void setUsersCurrentZone(Long userId, ZoneId zoneId) {
        usersTimeZone.put(userId, zoneId);
    }

    @Override
    public ZoneId getUsersCurrentZone(Long userId) {
        return usersTimeZone.get(userId);
    }

    @Override
    public void clearUsersCurrentZone(Long userId) {
        usersTimeZone.remove(userId);
    }

    @Override
    public void setUsersStartDate(Long userId, ZonedDateTime dateTime) {
        usersStartDate.put(userId, dateTime);
    }

    @Override
    public ZonedDateTime getUsersStartDate(Long userId) {
        return usersStartDate.remove(userId);
    }

}

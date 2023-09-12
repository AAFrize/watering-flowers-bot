package aa.frieze.wateringflowersbot.service;

import aa.frieze.wateringflowersbot.domain.enumeration.BotState;

import java.time.ZoneId;

public interface DataCache {

    void setUsersCurrentBotState(Long userId, BotState botState);

    BotState getUsersCurrentBotState(Long userId);

    void setUsersCurrentTitle(Long userId, String title);

    String getUsersCurrentTitle(Long userId);

    void setUsersCurrentZone(Long userId, ZoneId zoneId);

    ZoneId getUsersCurrentZone(Long userId);

}

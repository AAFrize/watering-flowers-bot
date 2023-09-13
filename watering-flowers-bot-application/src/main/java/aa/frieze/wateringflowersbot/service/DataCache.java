package aa.frieze.wateringflowersbot.service;

import aa.frieze.wateringflowersbot.domain.enumeration.BotState;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public interface DataCache {

    void setUsersCurrentBotState(Long userId, BotState botState);

    void clearUsersCurrentBotState(Long userId);

    BotState getUsersCurrentBotState(Long userId);

    void setUsersCurrentTitle(Long userId, String title);

    String getUsersCurrentTitle(Long userId);

    void setUsersCurrentZone(Long userId, ZoneId zoneId);

    ZoneId getUsersCurrentZone(Long userId);

    void clearUsersCurrentZone(Long userId);

    void setUsersStartDate(Long userId, ZonedDateTime dateTime);

    ZonedDateTime getUsersStartDate(Long userId);
}

package aa.frieze.wateringflowersbot.service;

import aa.frieze.wateringflowersbot.domain.enumeration.BotState;

public interface DataCache {

    void setUsersCurrentBotState(Long userId, BotState botState);

    BotState getUsersCurrentBotState(Long userId);

}

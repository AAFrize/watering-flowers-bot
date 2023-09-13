package aa.frieze.wateringflowersbot.domain.dto;

import lombok.Data;
import org.apache.commons.lang3.tuple.Pair;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Data
public class Settings {

    private List<SettingDto> settings = new ArrayList<>();
    private ZoneId timeZone;

    @Data
    public static class SettingDto {
        private String title;
        private ChronoUnit periodUnit;
        private Long periodValue;
        private ZonedDateTime firstNotificationDate;
    }

    public static Settings.SettingDto mapSettingDto(Pair<ChronoUnit, Long> unitLongPair,
                                                    ZonedDateTime usersStartDate, String title) {
        Settings.SettingDto dto = new Settings.SettingDto();
        dto.setFirstNotificationDate(usersStartDate);
        dto.setPeriodUnit(unitLongPair.getLeft());
        dto.setPeriodValue(unitLongPair.getRight());
        dto.setTitle(title);
        return dto;
    }
}
